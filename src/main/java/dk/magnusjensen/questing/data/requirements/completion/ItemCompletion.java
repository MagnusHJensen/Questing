package dk.magnusjensen.questing.data.requirements.completion;

import dk.magnusjensen.questing.data.Quest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemCompletion implements IQuestCompletion {
	private ArrayList<ItemStack> itemStacks;
	private boolean take;

	public ItemCompletion(ArrayList<ItemStack> itemStacks, boolean take) {
		this.itemStacks = itemStacks;
		this.take = take;
	}

	@Override
	public void completeQuest(Quest quest, Player player) {
		if (take) {
			for (ItemStack stackRequired : this.itemStacks) {
				player.getInventory().remove(stackRequired);
			}
		}
	}

	@Override
	public boolean canCompleteQuest(Quest quest, Player player) {
		for (ItemStack stackRequired : this.itemStacks) {
			if (!player.getInventory().containsAtLeast(stackRequired, stackRequired.getAmount()))
				return false;
		}

		return true;
	}

	@Override
	public boolean hasMobKills() {
		return false;
	}

	@Override
	public int getAmount() {
		return 0;
	}
}
