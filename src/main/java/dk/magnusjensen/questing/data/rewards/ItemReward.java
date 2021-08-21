package dk.magnusjensen.questing.data.rewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemReward implements IQuestReward {
	private ArrayList<ItemStack> itemStacks;

	public ItemReward(ArrayList<ItemStack> itemStacks) {
		this.itemStacks = itemStacks;
	}

	@Override
	public void giveRewards(Player player) {
		player.getInventory().addItem(this.itemStacks.toArray(new ItemStack[this.itemStacks.size()]));
	}
}
