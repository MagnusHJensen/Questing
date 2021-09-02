package dk.magnusjensen.questing.data.requirements.start;

import dk.magnusjensen.questing.data.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public class ItemStart implements IQuestStart {

	private ArrayList<ItemStack> itemStacks;
	private boolean take;

	public ItemStart(ArrayList<ItemStack> itemStacks, boolean take) {
		this.itemStacks = itemStacks;
		this.take = take;
	}
	@Override
	public void startQuest(Quest quest, Player player) {
		if (take) {
			for (ItemStack stackRequired : this.itemStacks) {
				player.getInventory().remove(stackRequired);
			}
		}
	}

	@Override
	public boolean canStartQuest(Player player) {
		for (ItemStack stackRequired : this.itemStacks) {
			if (!this.containsAtLeast(stackRequired, stackRequired.getAmount(), player.getInventory()))
				return false;
		}

		return true;
	}

	public boolean containsAtLeast(ItemStack item, int amount, PlayerInventory playerInventory) {
		if (item == null) {
			return false;
		} else if (amount <= 0) {
			return true;
		} else {
			ItemStack[] var6;
			int var5 = (var6 = playerInventory.getStorageContents()).length;

			for(int var4 = 0; var4 < var5; ++var4) {
				ItemStack i = var6[var4];

				if (item.isSimilar(i) && (amount -= i.getAmount()) <= 0) {
					return true;
				}


			}

			return false;
		}
	}
}
