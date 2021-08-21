package dk.magnusjensen.questing.data.requirements.completion;

import dk.magnusjensen.questing.data.Quest;
import org.bukkit.entity.Player;

public class TaskCompletion implements IQuestCompletion {
	@Override
	public void completeQuest(Quest quest, Player player) {

	}

	@Override
	public boolean canCompleteQuest(Player player) {
		return false;
	}
}
