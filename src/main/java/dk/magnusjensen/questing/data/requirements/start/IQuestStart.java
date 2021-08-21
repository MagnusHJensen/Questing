package dk.magnusjensen.questing.data.requirements.start;

import dk.magnusjensen.questing.data.Quest;
import org.bukkit.entity.Player;

public interface IQuestStart {
	void startQuest(Quest quest, Player Player);

	boolean canStartQuest(Player player);
}
