package dk.magnusjensen.questing.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.requirements.completion.IQuestCompletion;
import dk.magnusjensen.questing.data.requirements.completion.ItemCompletion;
import dk.magnusjensen.questing.data.requirements.completion.MobCompletion;
import dk.magnusjensen.questing.data.requirements.completion.NoneCompletion;
import dk.magnusjensen.questing.data.requirements.start.IQuestStart;
import dk.magnusjensen.questing.data.requirements.start.NoneStart;
import dk.magnusjensen.questing.data.rewards.ItemReward;
import dk.magnusjensen.questing.data.zones.MobZone;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class QuestCompletionParser {

	protected static IQuestCompletion parseQuestCompletion(String questid, JsonObject completionObject) {
		if (completionObject.get("type").getAsString().equalsIgnoreCase("item")) {
			return parseItemCompeletion(completionObject);
		} else if (completionObject.get("type").getAsString().equalsIgnoreCase("slayer")) {
			return parseSlayerCompletion(questid, completionObject);
		} else {
			return new NoneCompletion();
		}
	}

	private static ItemCompletion parseItemCompeletion(JsonObject completionObject) {
		JsonObject data = completionObject.getAsJsonObject("data");
		Material material = Material.getMaterial(data.get("item").getAsString().toUpperCase());
		int amount = data.get("amount").getAsInt();
		boolean take = data.get("take").getAsBoolean();
		ItemStack stack = new ItemStack(material, amount);
		ItemMeta itemMeta = stack.getItemMeta();
		/*if (data.has("meta")) {
			JsonObject meta = data.getAsJsonObject("meta");
			if (meta.has("lore")) {
				ArrayList<String> loreLines = new ArrayList<>();
				data.getAsJsonArray("lore").forEach((line) -> loreLines.add(line.getAsString()));
				itemMeta.setLore(loreLines);
			}
			if (meta.has("displayName")) {
				itemMeta.setDisplayName(meta.get("displayName").getAsString());
			}
		}*/
		stack.setItemMeta(itemMeta);
		ArrayList<ItemStack> stacks = new ArrayList<>();
		stacks.add(stack);
		return new ItemCompletion(stacks, take);
	}

	private static MobCompletion parseSlayerCompletion(String questid, JsonObject completionObject) {
		JsonObject data = completionObject.getAsJsonObject("data");
		JsonArray zoneIds = data.get("zoneIds").getAsJsonArray();

		ArrayList<MobZone> zones = new ArrayList<>();
		for (JsonElement zoneIdEle : zoneIds) {
			int zoneId = zoneIdEle.getAsInt();
			if (Questing.mobZones.size() < zoneId) {
				continue;
			}
			MobZone zone = Questing.mobZones.get(zoneId - 1);
			if (zone != null) {
				zones.add(zone);
				zone.linkQuest(questid);
			}
		}
		int amountToKill = data.get("amount").getAsInt();

		return new MobCompletion(amountToKill, zones);
	}

}
