package dk.magnusjensen.questing.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.Quest;
import dk.magnusjensen.questing.data.QuestParagraph;
import dk.magnusjensen.questing.data.requirements.completion.IQuestCompletion;
import dk.magnusjensen.questing.data.requirements.start.IQuestStart;
import dk.magnusjensen.questing.data.rewards.IQuestReward;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestParser {

	public static HashMap<UUID, ArrayList<Quest>> startParsingQuests() {
		String path = Questing.DATA_PATH.concat("quests/");
		HashMap<UUID, ArrayList<Quest>> quests = new HashMap<>();
		String fullPath = new File(path + "temp").getAbsolutePath();
		fullPath = fullPath.substring(0, fullPath.length() - 4);
		try {
			DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(fullPath));
			dirStream.forEach((filePath) -> {


				JsonObject fileJson = null;
				try {
					fileJson = new Gson().fromJson(new FileReader(filePath.toAbsolutePath().toString()), JsonObject.class);
				} catch (IOException exception) {
					exception.printStackTrace();
				}

				UUID questGiver = UUID.fromString(filePath.getFileName().toString().replace(".json", ""));
				ArrayList<Quest> questsForNpc = parseQuestsForNPC(questGiver, fileJson);
				quests.put(questGiver, questsForNpc);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return quests;
	}

	private static ArrayList<Quest> parseQuestsForNPC (UUID questGiver, JsonObject fileJson) {
		JsonArray questArray = fileJson.getAsJsonArray("quests");
		ArrayList<Quest> quests = new ArrayList<>();
		for (JsonElement element : questArray) {
			JsonObject quest = element.getAsJsonObject();
			quests.add(mapJsonToQuest(questGiver, quest));
		}
		return quests;
	}

	private static Quest mapJsonToQuest(UUID questGiver, JsonObject quest) {
		IQuestStart start = QuestStartParser.parseQuestStart(quest.getAsJsonObject("requirements").getAsJsonObject("start"));
		IQuestReward reward = QuestRewardParser.parseReward(quest.getAsJsonObject("reward"));

		int id = quest.get("id").getAsInt();
		String name = quest.get("name").getAsString();
		HashMap<String, QuestParagraph> paragraphs = parseParagraphs(quest.getAsJsonObject("paragraphs"));
		HashMap<String, String> formatting = parseFormatting(quest.getAsJsonObject("formatting"));
		IQuestCompletion completion = QuestCompletionParser.parseQuestCompletion(questGiver.toString() + "/" + id,  quest.getAsJsonObject("requirements").getAsJsonObject("completion"));

		return new Quest(questGiver, id, name, paragraphs, start, completion, reward, formatting);
	}


	private static HashMap<String, QuestParagraph> parseParagraphs(JsonObject paragraphObject) {
		HashMap<String, QuestParagraph> paragraphs = new HashMap<>();

		for (Map.Entry<String, JsonElement> set : paragraphObject.entrySet()) {
			ArrayList<String> npcLines = new ArrayList<>();
			ArrayList<String> playerLines = new ArrayList<>();
			JsonObject individualParaObject = set.getValue().getAsJsonObject();
			for (JsonElement line : individualParaObject.get("lines").getAsJsonArray()) {
				npcLines.add(line.getAsString());
			}
			//for (JsonElement playerLine : individualParaObject.getAsJsonArray("playerLines")) {
			//	playerLines.add(playerLine.getAsString());
			//}
			boolean random = individualParaObject.has("random") && individualParaObject.get("random").getAsBoolean();
			//boolean playerLineComesFirst = individualParaObject.has("playerLineFirst") && individualParaObject.get("playerLineFirst").getAsBoolean();
			QuestParagraph paragraph = new QuestParagraph(npcLines, playerLines, random, false);
			paragraphs.put(set.getKey(), paragraph);
		}
		return paragraphs;
	}

	private static HashMap<String, String> parseFormatting(JsonObject formattingObject) {
		HashMap<String, String> formatting = new HashMap<>();
		for (Map.Entry<String, JsonElement> set : formattingObject.entrySet()) {
			formatting.put(set.getKey(), set.getValue().getAsString());
		}
		return formatting;
	}
}
