package dk.magnusjensen.questing.util;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.Quest;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerIO {

	public static HashMap<UUID, HashMap<String, ArrayList<String>>> startParsingPlayers() {
		String path = Questing.DATA_PATH;
		HashMap<UUID, HashMap<String, ArrayList<String>>> players = new HashMap<>();
		String fullPath = new File(path + "players.json").getAbsolutePath();

		JsonArray fileJson = null;
		try {
			fileJson = new Gson().fromJson(new FileReader(fullPath), JsonArray.class);
		} catch (IOException exception) {
			exception.printStackTrace();
		}


		for (JsonElement playerElement : fileJson) {
			JsonObject playerObject = playerElement.getAsJsonObject();
			UUID playerUUUID = UUID.fromString(playerObject.get("uuid").getAsString());
			ArrayList<String> ongoing = new ArrayList<>();
			ArrayList<String> completed = new ArrayList<>();
			playerObject.getAsJsonArray("ongoing").forEach((ele) -> {
				ongoing.add(ele.getAsString());
				Questing.quests.get(UUID.fromString(ele.getAsString().split("/")[0])).get(Integer.parseInt(ele.getAsString().split("/")[1]) - 1).ongoingBy.add(playerUUUID);
			});
			playerObject.getAsJsonArray("completed").forEach((ele) -> {
				Questing.quests.get(UUID.fromString(ele.getAsString().split("/")[0])).get(Integer.parseInt(ele.getAsString().split("/")[1]) - 1).completedBy.add(playerUUUID);
			});

			HashMap<String, ArrayList<String>> innerHash = new HashMap<>();
			innerHash.put("ongoing", ongoing);
			innerHash.put("completed", completed);

			players.put(playerUUUID, innerHash);

		}




		return players;
	}

	public static void savePlayers(HashMap<UUID, HashMap<String, ArrayList<String>>> players) {
		String path = Questing.DATA_PATH;
		String fullPath = new File(path + "players.json").getAbsolutePath();
		JsonArray playerArray = new JsonArray();



		for (Map.Entry<UUID, HashMap<String, ArrayList<String>>> entry : players.entrySet()) {
			JsonObject playerObject = new JsonObject();
			playerObject.addProperty("uuid", entry.getKey().toString()); // Set uuid field.

			JsonArray ongoing = new JsonArray();
			JsonArray completed = new JsonArray();
			entry.getValue().get("ongoing").forEach(ongoing::add);
			entry.getValue().get("completed").forEach(completed::add);

			playerObject.add("ongoing", ongoing);
			playerObject.add("completed", completed);

			playerArray.add(playerObject);

		}

		try {
			String json = new Gson().toJson(playerArray);
			System.out.println(json);
			FileWriter writer = new FileWriter(fullPath);
			writer.write(json);
			writer.close();

		} catch (IOException exception) {
			Bukkit.getLogger().log(Level.SEVERE, exception.getCause().getMessage());
			exception.printStackTrace();
		};


	}




}
