package dk.magnusjensen.questing.listeners;

import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTitle().equalsIgnoreCase("Mob Zone List")) {
			this.handleMobZoneInventory(event);
		}
	}

	public void handleMobZoneInventory(InventoryClickEvent event) {
		if (event.getCurrentItem().getType() == Material.ZOMBIE_HEAD) {
				ItemStack stack = CraftItemStack.asNMSCopy(event.getCurrentItem());
				NBTTagCompound compound = stack.getTag();
			if (event.getClick() == ClickType.RIGHT) {

				double x = compound.getDouble("tpX");
				int y = compound.getInt("tpY");
				double z = compound.getDouble("tpZ");

				event.getViewers().get(0).sendMessage("You were tp'ed to mobzone " + compound.getInt("mobZoneId"));
				event.getViewers().get(0).teleport(new Location(event.getViewers().get(0).getWorld(), x, y, z));



			} else if (event.getClick() == ClickType.LEFT) {
				event.getViewers().get(0).sendMessage("You are now editing mobzone " + compound.getInt("mobZoneId"));
				((Player) event.getViewers().get(0)).performCommand("/questing:mobzone edit " + compound.getInt("mobZoneId"));
			}

			event.setCancelled(true);

		}
	}
}
