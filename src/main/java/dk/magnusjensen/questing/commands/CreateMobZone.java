package dk.magnusjensen.questing.commands;

import com.google.common.collect.Lists;
import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.conversations.MobZoneCreationConversations;
import dk.magnusjensen.questing.conversations.MobZoneEditConversations;
import dk.magnusjensen.questing.data.Quest;
import dk.magnusjensen.questing.data.zones.MobZone;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CreateMobZone implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length > 0) {
			if (args[0].contains("pos1")) {
				Player player = (Player) sender;
				Location pos1 = player.getLocation();

				player.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.x1"), PersistentDataType.DOUBLE, pos1.getX());
				player.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.y1"), PersistentDataType.DOUBLE, pos1.getY());
				player.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.z1"), PersistentDataType.DOUBLE, pos1.getZ());

				player.sendMessage("[Questing] Position 1 has been set at X: " + pos1.getBlockX() + " | Z: " + pos1.getBlockZ());
				return true;
			} else if (args[0].contains("pos2")) {
				Player player = (Player) sender;
				Location pos2 = player.getLocation();

				player.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.x2"), PersistentDataType.DOUBLE, pos2.getX());
				player.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.z2"), PersistentDataType.DOUBLE, pos2.getZ());
				if (player.getPersistentDataContainer().get(new NamespacedKey(Questing.INSTANCE, "mobzone.y1"), PersistentDataType.DOUBLE) < player.getLocation().getBlockY()) {
					player.getPersistentDataContainer().set(new NamespacedKey(Questing.INSTANCE, "mobzone.y1"), PersistentDataType.DOUBLE, player.getLocation().getY());
				}


				player.sendMessage("[Questing] Position 2 has been set at X: " + pos2.getBlockX() + " | Z: " + pos2.getBlockZ());
				return true;
			} else if (args[0].contains("create")) {
				return createMobZone(sender, args);
			} else if (args[0].contains("edit")) {
				return editMobZone(sender, args);
			} else if (args[0].contains("list")) {
				return listMobZones(sender, args.length >= 2 ? Integer.parseInt(args[1]) : 0);
			}
		}
		return false;

	}

	private boolean createMobZone(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			CraftPlayer player = (CraftPlayer) sender;

			Location pos1 = new Location(player.getWorld(),
					player.getPersistentDataContainer().get(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.x1"), PersistentDataType.DOUBLE).doubleValue(),
					player.getPersistentDataContainer().get(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.y1"), PersistentDataType.DOUBLE).doubleValue(),
					player.getPersistentDataContainer().get(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.z1"), PersistentDataType.DOUBLE).doubleValue()
			);
			Location pos2 = new Location(player.getWorld(),
					player.getPersistentDataContainer().get(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.x2"), PersistentDataType.DOUBLE).doubleValue(),
					0,
					player.getPersistentDataContainer().get(new NamespacedKey(Bukkit.getPluginManager().getPlugin("questing"), "mobzone.z2"), PersistentDataType.DOUBLE).doubleValue()
			);


			BoundingBox area = new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
			for (MobZone zone : Questing.mobZones) {
				if (zone.area.contains(pos1.getX(), pos1.getY(), pos1.getZ())) {
					player.sendMessage("Position 1 was inside another mobzone.");
					return false;
				} else if (zone.area.contains(pos2.getX(), pos2.getY(), pos1.getZ())) {
					player.sendMessage("Position 2 was inside another mobzone.");
					return false;
				}
			}

			HashMap<Object, Object> contextData = new HashMap<>();
			contextData.put("boundingBox", area);

			Conversation conversation = Questing.conversationFactory.withLocalEcho(false).withFirstPrompt(new MobZoneCreationConversations.MobZoneAmountPrompt())
					.withInitialSessionData(contextData).withPrefix(context -> "[Questing] ").buildConversation((Player) sender);

			conversation.begin();

			return true;
		}
		return false;
	}

	private boolean editMobZone(CommandSender sender, String[] args) {
		int editZoneId = -1;
		if (args.length > 1) {
			editZoneId = Integer.parseInt(args[1]);
		}

		if (editZoneId != -1) {
			if (Questing.mobZones.size() >= editZoneId) {
				MobZone zone = Questing.mobZones.get(editZoneId - 1);
				HashMap<Object, Object> contextData = new HashMap<>();
				contextData.put("zone", zone);
				contextData.put("mobType", zone.type);
				contextData.put("respawnTime", Integer.toString(zone.respawnTime));
				contextData.put("amountToKeep", Integer.toString(zone.amountToKeep));
				Conversation conversation = Questing.conversationFactory.withLocalEcho(false).withFirstPrompt(new MobZoneEditConversations.MobZoneAmountPrompt())
						.withInitialSessionData(contextData).withPrefix(context -> "[Questing] ").buildConversation((Player) sender);

				conversation.begin();
				return true;
			} else {
				sender.sendMessage("MobZone with id: " + editZoneId + " was not found.");
				return false;
			}
		} else {
			Location playerPos = ((Player) sender).getLocation();

			for (int i = 0; i < Questing.mobZones.size(); i++) {
				MobZone zone = Questing.mobZones.get(i);
				if (zone.area.contains(playerPos.getX(), playerPos.getY(), playerPos.getZ())) {
					HashMap<Object, Object> contextData = new HashMap<>();
					contextData.put("zone", zone);
					contextData.put("mobType", zone.type);
					contextData.put("respawnTime", Integer.toString(zone.respawnTime));
					contextData.put("amountToKeep", Integer.toString(zone.amountToKeep));
					Conversation conversation = Questing.conversationFactory.withLocalEcho(false).withFirstPrompt(new MobZoneEditConversations.MobZoneAmountPrompt())
							.withInitialSessionData(contextData).withPrefix(context -> "[Questing] ").buildConversation((Player) sender);

					conversation.begin();
					return true;
				}
			}

		}

		return false;

	}

	private boolean listMobZones(CommandSender sender, int page) {

		Player player = (Player) sender;
		Inventory mobZoneList = Bukkit.createInventory(null, InventoryType.CHEST, "Mob Zone List");
		if (!player.hasPermission("questing.mobzone.list")) {
			player.sendMessage(Bukkit.getPermissionMessage());
			return false;
		}


		int i = 0 + (18 * page);
		for (MobZone zone : Questing.mobZones) {
			ItemStack head = new ItemStack(Material.ZOMBIE_HEAD);

			net.minecraft.server.v1_16_R3.ItemStack stack = CraftItemStack.asNMSCopy(head);

			NBTTagCompound compound = stack.getOrCreateTag();

			compound.setDouble("tpX", zone.area.getCenterX());
			compound.setInt("tpY", player.getWorld().getHighestBlockYAt((int) zone.area.getCenterX(), (int) zone.area.getCenterZ()) + 1);
			compound.setDouble("tpZ", zone.area.getCenterZ());

			compound.setInt("mobZoneId", zone.id);

			stack.setTag(compound);

			head = CraftItemStack.asBukkitCopy(stack);

			ItemMeta meta = head.getItemMeta();


			meta.setLore(Lists.newArrayList("ID: " + zone.id, "Zone - X: " + zone.area.getCenterX() + " | Z: " + zone.area.getCenterZ(),
					"Mob Type: " + zone.type, "Left click to edit this mob zone.", "Right click to TP."));
			head.setItemMeta(meta);

			mobZoneList.setItem(i, head);
			i++;
			if (i == 18 * (page + 1)) break;

		}

		player.openInventory(mobZoneList);


		return true;
	}





}
