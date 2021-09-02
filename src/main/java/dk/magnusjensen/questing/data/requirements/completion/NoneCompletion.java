package dk.magnusjensen.questing.data.requirements.completion;

import dk.magnusjensen.questing.data.Quest;
import org.bukkit.entity.Player;

public class NoneCompletion implements IQuestCompletion {
	@Override
	public void completeQuest(Quest quest, Player player) {

	}

	@Override
	public boolean canCompleteQuest(Quest quest, Player player) {
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
