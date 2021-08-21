package dk.magnusjensen.questing.listeners;

import dk.magnusjensen.questing.Questing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;

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
	}
}
