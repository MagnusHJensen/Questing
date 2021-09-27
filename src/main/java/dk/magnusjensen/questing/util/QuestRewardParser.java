package dk.magnusjensen.questing.util;

import com.google.gson.JsonObject;
import dk.magnusjensen.questing.data.rewards.IQuestReward;
import dk.magnusjensen.questing.data.rewards.ItemReward;
import dk.magnusjensen.questing.data.rewards.MoneyReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QuestRewardParser {

	protected static IQuestReward parseReward(JsonObject rewardObject) {
		if (rewardObject.get("type").getAsString().equalsIgnoreCase("item")) {
			return parseItemReward(rewardObject);
		} else if (rewardObject.get("type").getAsString().equalsIgnoreCase("items")) {
			return null;
			//return parseItemRewards(rewardObject);
		}  else if (rewardObject.get("type").getAsString().equalsIgnoreCase("money")) {
			return parseMoneyReward(rewardObject);
			//return parseItemRewards(rewardObject);
		}
		System.out.println("Quest Reward type not known!");
		return null;
	}

	private static ItemReward parseItemReward(JsonObject rewardObject) {
		JsonObject data = rewardObject.getAsJsonObject("data");
		Material material = Material.getMaterial(data.get("item").getAsString().toUpperCase());
		int amount = data.get("amount").getAsInt();
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
		return new ItemReward(stacks);
	}

	private static ItemReward parseItemRewards(JsonObject rewardObject) {
		JsonObject data = rewardObject.getAsJsonObject("data");
		return null;
	}

	private static MoneyReward parseMoneyReward(JsonObject rewardObject) {
		JsonObject data = rewardObject.getAsJsonObject("data");
		double money = data.get("amount").getAsDouble();
		//boolean bankDeposit = data.get("bankDeposit").getAsBoolean();

		return new MoneyReward(money, false);
	}
}
