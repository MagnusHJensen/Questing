package dk.magnusjensen.questing.data.requirements.start;

import dk.magnusjensen.questing.data.Quest;
import org.bukkit.entity.Player;

public class NoneStart implements IQuestStart {
	@Override
	public void startQuest(Quest quest, Player player) {

	}

	@Override
	public boolean canStartQuest(Player player) {
		return true;
	}
}
