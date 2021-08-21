package dk.magnusjensen.questing.util;

import com.google.gson.JsonObject;
import dk.magnusjensen.questing.data.requirements.start.IQuestStart;
import dk.magnusjensen.questing.data.requirements.start.NoneStart;
import dk.magnusjensen.questing.data.requirements.start.QuestStart;
import dk.magnusjensen.questing.data.rewards.IQuestReward;
import dk.magnusjensen.questing.data.rewards.ItemReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestStartParser {

	protected static IQuestStart parseQuestStart(JsonObject startObject) {
		if (startObject.get("type").getAsString().equalsIgnoreCase("quest")) {
			return parseQuestReq(startObject);
		} else if (startObject.get("type").getAsString().equalsIgnoreCase("none")) {
			return new NoneStart();
		}
		System.out.println("Quest Start type not known!");
		return null;
	}

	private static QuestStart parseQuestReq(JsonObject startObject) {
		JsonObject data = startObject.getAsJsonObject("data");
		ArrayList<String> questIds = new ArrayList<>();
		data.getAsJsonArray("quests").forEach((quest) -> {
			questIds.add(quest.getAsString());
		});

		return new QuestStart(questIds);
	}


}
