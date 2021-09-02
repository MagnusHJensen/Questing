package dk.magnusjensen.questing.listeners;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.Quest;
import dk.magnusjensen.questing.data.zones.MobZone;
import dk.magnusjensen.questing.scoreboard.ScoreboardHelper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class OnPlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoinSetupQuests(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (!Questing.players.containsKey(player.getUniqueId())) {
			HashMap<String, ArrayList<String>> questMap = new HashMap<>();
			questMap.put("ongoing", new ArrayList<>());
			questMap.put("completed", new ArrayList<>());
			Questing.players.put(player.getUniqueId(), questMap);
		}

		new PlayerPositionChecker(player).runTaskTimer(Questing.INSTANCE, 40, 40);
	}

	private class PlayerPositionChecker extends BukkitRunnable {

		private Player player;
		private int previousZone = -1;
		public PlayerPositionChecker(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			for (MobZone zone : Questing.mobZones) {
				if (zone.area.clone().expand(5, 128, 5).contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())) {
					if (previousZone != zone.id) {
						zone.showZoneTitle(Audience.audience(player));
						previousZone = zone.id;
					}

					ScoreboardHelper helper;
					if (ScoreboardHelper.hasScore(player)) {
						helper = ScoreboardHelper.getByPlayer(player);
					} else {
						helper = ScoreboardHelper.createScore(player);
						helper.setTitle("&6Slayer Progress:");

					}

					ArrayList<String> quests = zone.getLinkedQuests();

					int currentSlot = 1;
					for (String customQuestId : quests) {
						Quest quest = Questing.quests.get(UUID.fromString(customQuestId.split("/")[0])).get(Integer.parseInt(customQuestId.split("/")[1]) - 1);
						if (quest.requiresMobKills() && quest.ongoingBy.contains(player.getUniqueId())) {
							int current = player.getPersistentDataContainer().get(new NamespacedKey(Questing.INSTANCE, quest.getCustomQuestId()), PersistentDataType.INTEGER).intValue();
							helper.setSlot(currentSlot, quest.getName() + ": " + (current >= quest.getCompletionReq().getAmount() ? "&aDone" : (current + "/" + quest.getCompletionReq().getAmount())));
							currentSlot++;
						}
					}


					return;
				}
			}
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			ScoreboardHelper.removeScore(player);
			previousZone = -1;
		}
	}


}
