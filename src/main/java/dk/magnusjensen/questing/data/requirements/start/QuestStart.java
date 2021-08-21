package dk.magnusjensen.questing.data.requirements.start;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.Quest;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class QuestStart implements IQuestStart {
	private ArrayList<String> questIds;

	public QuestStart(ArrayList<String> questIds) {
		this.questIds = questIds;
	}

	@Override
	public void startQuest(Quest quest, Player player) {

	}

	@Override
	public boolean canStartQuest(Player player) {
		ArrayList<String> completedQuests = Questing.players.get(player.getUniqueId()).get("completedBy");
		boolean canStart = true;
		for (String questId : questIds) {
			if (!completedQuests.contains(questId)) {
				return false;
			}
		}
		return true;
	}
}
