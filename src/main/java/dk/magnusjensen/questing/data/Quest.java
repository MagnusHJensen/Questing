package dk.magnusjensen.questing.data;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.requirements.completion.IQuestCompletion;
import dk.magnusjensen.questing.data.requirements.start.IQuestStart;
import dk.magnusjensen.questing.data.rewards.IQuestReward;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;

import java.util.*;

public class Quest {
	private UUID questOwner;
	private int id;
	private String name;
	private HashMap<String, QuestParagraph> paragraphs;
	private IQuestStart startReq;
	private IQuestCompletion completionReq;
	private IQuestReward reward;
	public HashMap<String, String> formatting;
	public HashSet<UUID> completedBy;
	public HashSet<UUID> ongoingBy;

	public Quest(UUID questOwner, int id, String name, HashMap<String, QuestParagraph> paragraphs, IQuestStart start, IQuestCompletion completion, IQuestReward reward, HashMap<String, String> formatting) {
		this.questOwner = questOwner;
		this.id = id;
		this.name = name;
		this.paragraphs = paragraphs;
		this.startReq = start;
		this.completionReq = completion;
		this.reward = reward;
		this.formatting = formatting;
		this.completedBy = new HashSet<>();
		this.ongoingBy = new HashSet<>();
	}


	public void interact(Player player) {


		// Have the user accepted the quest, and have not finished the requirements to complete it.
		if (this.ongoingBy.contains(player.getUniqueId()) && !this.completionReq.canCompleteQuest(player)) {
			this.paragraphs.get("ongoing").playWithAbandon(this, player, ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/questing:abandon " + this.questOwner.toString() + "/" + this.id));
		} else if (this.ongoingBy.contains(player.getUniqueId()) && this.completionReq.canCompleteQuest(player)){
			// Ongoing and can complete
			this.paragraphs.get("completion").play(this, player);
			this.completeQuest(player);
		} else if (!this.ongoingBy.contains(player.getUniqueId()) && !this.completedBy.contains(player.getUniqueId())) {
			this.paragraphs.get("opening_line").playWithAcceptOrDeny(this, player, ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/questing:start " + this.questOwner.toString() + "/" + this.id), ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/questing:deny " + this.questOwner.toString() + "/" + this.id));
		}
	}

	public void startQuest(Player player) {
		this.startReq.startQuest(this, player);
		this.ongoingBy.add(player.getUniqueId());
		Questing.players.get(player.getUniqueId()).get("ongoing").add(this.questOwner + "/" + this.id);
		this.paragraphs.get("accept").play(this, player);
	}

	public void denyQuest(Player player) {
		this.paragraphs.get("deny").play(this, player);
	}


	public void abandonQuest(Player player) {
		this.paragraphs.get("abandon").play(this, player);
		this.removePlayerFromOngoing(player);
	}

	private void removePlayerFromOngoing(Player player) {
		this.ongoingBy.remove(player.getUniqueId());
		Questing.players.get(player.getUniqueId()).get("ongoing").remove(this.questOwner + "/" + this.id);
	}

	public void completeQuest(Player player) {
		this.completionReq.completeQuest(this, player);
		this.reward.giveRewards(player);
		this.completedBy.add(player.getUniqueId());
		Questing.players.get(player.getUniqueId()).get("completed").add(this.questOwner + "/" + this.id);
		this.removePlayerFromOngoing(player);
	}








}
