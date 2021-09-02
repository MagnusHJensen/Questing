package dk.magnusjensen.questing.util;

import com.google.gson.*;
import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.Quest;
import dk.magnusjensen.questing.data.zones.MobZone;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class MobZoneIO {

	public static ArrayList<MobZone> startParsingMobZones() {
		String path = Questing.DATA_PATH;
		ArrayList<MobZone> mobZones = new ArrayList<>();
		String fullPath = new File(path + "mobzones.json").getAbsolutePath();

		JsonArray fileJson = null;
		try {
			fileJson = new Gson().fromJson(new FileReader(fullPath), JsonArray.class);
		} catch (IOException exception) {
			exception.printStackTrace();
		}


		for (JsonElement mobZoneElement : fileJson) {
			JsonObject mobZoneObject = mobZoneElement.getAsJsonObject();
			World world = ((CraftWorld) Bukkit.getWorld(UUID.fromString(mobZoneObject.get("worldUUID").getAsString()))).getHandle();


			JsonObject bb = mobZoneObject.getAsJsonObject("bbBox");
			BoundingBox boundingBox = new BoundingBox(
					bb.get("minX").getAsDouble(),
					bb.get("minY").getAsDouble(),
					bb.get("minZ").getAsDouble(),
					bb.get("maxX").getAsDouble(),
					bb.get("maxY").getAsDouble(),
					bb.get("maxZ").getAsDouble());

			MobZone zone = new MobZone(
					mobZoneObject.get("id").getAsInt(),
					mobZoneObject.get("amountToKeep").getAsInt(),
					mobZoneObject.get("respawnTime").getAsInt(),
					mobZoneObject.get("mobType").getAsString(),
					boundingBox,
					world);

			zone.reInit();
			mobZones.add(zone);

		}




		return mobZones;
	}

	public static void saveMobZones(ArrayList<MobZone> mobZones) {
		String path = Questing.DATA_PATH;
		String fullPath = new File(path + "mobzones.json").getAbsolutePath();
		JsonArray mobZonesArray = new JsonArray();



		for (MobZone zone : mobZones) {

			JsonObject mobZoneObject = new JsonObject();
			mobZoneObject.addProperty("mobType", zone.type);
			mobZoneObject.addProperty("respawnTime", zone.respawnTime);
			mobZoneObject.addProperty("amountToKeep", zone.amountToKeep);
			mobZoneObject.addProperty("worldUUID", zone.world.getWorld().getUID().toString());

			JsonObject boundingBox = new JsonObject();
			boundingBox.addProperty("minX", zone.area.getMinX());
			boundingBox.addProperty("minY", zone.area.getMinY());
			boundingBox.addProperty("minZ", zone.area.getMinZ());
			boundingBox.addProperty("maxX", zone.area.getMaxX());
			boundingBox.addProperty("maxY", zone.area.getMaxY());
			boundingBox.addProperty("maxZ", zone.area.getMaxZ());

			mobZoneObject.add("bbBox", boundingBox);

			mobZoneObject.addProperty("id", zone.id);

			mobZonesArray.add(mobZoneObject);

		}

		try {
			String json = new GsonBuilder().setPrettyPrinting().create().toJson(mobZonesArray);
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
