package dk.magnusjensen.questing.listeners;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.Quest;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestNPCInteract implements Listener {

	@EventHandler
	public void questNPCInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Quest quest = checkNPCForQuest(event);
		if (quest == null) return;

		quest.interact(event.getPlayer());

	}

	public Quest checkNPCForQuest(PlayerInteractEntityEvent event) {
		ArrayList<Quest> questForNpc = Questing.quests.get(event.getRightClicked().getUniqueId());

		if (questForNpc.size() == 0) return null;

		// Fetch ongoing quest
		List<Quest> onGoing = questForNpc.stream().filter((checkQuest) -> checkQuest.ongoingBy.contains(event.getPlayer().getUniqueId())).collect(Collectors.toList());
		if (onGoing.size() > 0)
			return onGoing.get(0);

		// No ongoing quest, fetch the next in line.
		onGoing = questForNpc.stream().filter((checkQuest) -> !checkQuest.completedBy.contains(event.getPlayer().getUniqueId())).collect(Collectors.toList());
		if (onGoing.size() > 0)
			return onGoing.get(0);


		// All quests for this npc has been completed.
		event.getPlayer().sendMessage("[Questing] Hello traveller, I'm grateful for the help you have given, but you must go on, and find a wiser source to teach you more!");
		return null;
	}
}
