package dk.magnusjensen.questing.util;

import com.google.gson.JsonObject;
import dk.magnusjensen.questing.data.requirements.completion.IQuestCompletion;
import dk.magnusjensen.questing.data.requirements.completion.ItemCompletion;
import dk.magnusjensen.questing.data.requirements.start.IQuestStart;
import dk.magnusjensen.questing.data.requirements.start.NoneStart;
import dk.magnusjensen.questing.data.rewards.ItemReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class QuestCompletionParser {

	protected static IQuestCompletion parseQuestCompletion(JsonObject completionObject) {
		if (completionObject.get("type").getAsString().equalsIgnoreCase("item")) {
			return parseItemCompeletion(completionObject);
		}
		System.out.println("Quest Start type not known!");
		return null;
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

}
