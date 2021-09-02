package dk.magnusjensen.questing.entity;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.zones.MobZone;
import dk.magnusjensen.questing.util.QuestingUtils;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityZombie;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MobZoneZombie extends EntityZombie {
	private MobZone zone;

	public MobZoneZombie(World world, MobZone zone) {
		this(world);
		this.zone = zone;
	}

	public MobZoneZombie(World world) {
		super(world);
	}

	@Override
	public void die() {
		this.dead = true;
		for (String tag : this.getScoreboardTags()) {
			if (QuestingUtils.isCustomQuestId(tag)) {
				if (this.lastDamager instanceof EntityHuman) {
					if (Questing.players.get(this.lastDamager.getUniqueID()).get("ongoing").contains(tag)) {
						// Update player killed stats.
						int current = ((EntityHuman)this.lastDamager).getBukkitEntity().getPersistentDataContainer().has(new NamespacedKey("questing", tag), PersistentDataType.INTEGER) ? ((EntityHuman)this.lastDamager).getBukkitEntity().getPersistentDataContainer().get(new NamespacedKey("questing", tag), PersistentDataType.INTEGER) : 0;
						if (current > Questing.quests.get(UUID.fromString(tag.split("/")[0])).get(Integer.parseInt(tag.split("/")[1]) - 1).getCompletionReq().getAmount()) break;
						((EntityHuman)this.lastDamager).getBukkitEntity().getPersistentDataContainer().set(new NamespacedKey("questing", tag), PersistentDataType.INTEGER, current + 1);
					}
				}
			}
		}
		zone.removeMobFromZone(this);
	}

	@Override
	protected boolean T_() {
		return false;
	}
}
