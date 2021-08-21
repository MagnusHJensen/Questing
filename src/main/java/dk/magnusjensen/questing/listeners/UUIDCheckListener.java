package dk.magnusjensen.questing.listeners;

import dk.magnusjensen.questing.commands.UUIDCheck;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class UUIDCheckListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;

		if (UUIDCheck.playersChecking.contains(event.getPlayer())) {
			TextComponent uuidText = new TextComponent(event.getRightClicked().getUniqueId().toString());
			uuidText.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, event.getRightClicked().getUniqueId().toString()));
			uuidText.setColor(ChatColor.GREEN);
			uuidText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to copy!")));
			TextComponent built = new TextComponent("UUID for " + event.getRightClicked().getType().toString() + ": ");
			built.addExtra(uuidText);
			event.getPlayer().spigot().sendMessage(built);
			event.setCancelled(true);
		}
	}
}
