package dk.magnusjensen.questing.data.requirements.completion;

import dk.magnusjensen.questing.data.Quest;
import org.bukkit.entity.Player;

public interface IQuestCompletion {

	void completeQuest(Quest quest, Player player);

	boolean canCompleteQuest(Quest quest, Player player);

	boolean hasMobKills();

	int getAmount();

}
