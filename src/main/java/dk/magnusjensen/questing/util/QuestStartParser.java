package dk.magnusjensen.questing.util;

import com.google.gson.JsonObject;
import dk.magnusjensen.questing.data.requirements.start.IQuestStart;
import dk.magnusjensen.questing.data.requirements.start.ItemStart;
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
		} else if (startObject.get("type").getAsString().equalsIgnoreCase("item")) {
			return parseItemReq(startObject);
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

	private static ItemStart parseItemReq(JsonObject startObject) {
		JsonObject data = startObject.getAsJsonObject("data");
		Material material = Material.getMaterial(data.get("item").getAsString().toUpperCase());
		int amount = data.get("amount").getAsInt();
		boolean take = data.get("take").getAsBoolean();
		ItemStack stack = new ItemStack(material, amount);
		ItemMeta itemMeta = stack.getItemMeta();
		if (data.has("meta")) {
			JsonObject meta = data.getAsJsonObject("meta");
			if (meta.has("lore")) {
				ArrayList<String> loreLines = new ArrayList<>();
				meta.getAsJsonArray("lore").forEach((line) -> loreLines.add(line.getAsString()));
				itemMeta.setLore(loreLines);
			}
			if (meta.has("displayName")) {
				itemMeta.setDisplayName(meta.get("displayName").getAsString());
			}
		}
		stack.setItemMeta(itemMeta);
		ArrayList<ItemStack> stacks = new ArrayList<>();
		stacks.add(stack);
		return new ItemStart(stacks, take);
	}


}
