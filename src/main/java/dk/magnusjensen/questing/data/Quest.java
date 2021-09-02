package dk.magnusjensen.questing.data;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.requirements.completion.IQuestCompletion;
import dk.magnusjensen.questing.data.requirements.start.IQuestStart;
import dk.magnusjensen.questing.data.rewards.IQuestReward;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

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


		if (!this.startReq.canStartQuest(player) && !this.ongoingBy.contains(player.getUniqueId())) {
			this.paragraphs.get("requirements_not_met").play(this, player);
			return;
		}
		// Have the user accepted the quest, and have not finished the requirements to complete it.
		if (this.ongoingBy.contains(player.getUniqueId()) && !this.completionReq.canCompleteQuest(this, player)) {
			this.paragraphs.get("ongoing").playWithAbandon(this, player, ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/questing:abandon " + this.questOwner.toString() + "/" + this.id));
		} else if (this.ongoingBy.contains(player.getUniqueId()) && this.completionReq.canCompleteQuest(this, player)){
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
		if (this.getCompletionReq().hasMobKills()) {
			player.getPersistentDataContainer().set(new NamespacedKey(Questing.INSTANCE, this.getCustomQuestId()), PersistentDataType.INTEGER, 0);
		}
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

		final Component mainTitle = Component.text("Quest Completed" , NamedTextColor.GREEN);
		final Component subTitle = Component.text(this.name, NamedTextColor.GRAY);
		final Title title = Title.title(mainTitle, subTitle);

		Audience.audience(player).showTitle(title);

		// Set up firework, and start firing off.
		Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);

		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.addEffect(FireworkEffect.builder().withFade(Color.AQUA).with(FireworkEffect.Type.CREEPER).withColor(Color.GREEN, Color.RED).build());
		fwm.setPower(1);

		fw.setFireworkMeta(fwm);

		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				Firework fw1 = player.getWorld().spawn(player.getLocation(), Firework.class);
				fw1.setFireworkMeta(fwm);
				i++;
				if (i == 2) {
					this.cancel();
				}
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("questing"), 5, 15);
	}

	public String getCustomQuestId() {
		return new StringBuilder().append(questOwner.toString()).append("/").append(id).toString();
	}

	public boolean requiresMobKills() {
		return completionReq.hasMobKills();
	}

	public IQuestCompletion getCompletionReq() {
		return completionReq;
	}


	public String getName() {
		return name;
	}
}
