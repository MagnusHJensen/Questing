package dk.magnusjensen.questing.data.rewards;

import dk.magnusjensen.questing.Questing;
import org.bukkit.entity.Player;

public class MoneyReward implements IQuestReward {

	private double money;
	private boolean bankDeposit;

	public MoneyReward(double money, boolean bankDeposit) {
		this.money = money;
	}

	@Override
	public void giveRewards(Player player) {
		Questing.ECONOMY.depositPlayer(player, money);
	}
}
