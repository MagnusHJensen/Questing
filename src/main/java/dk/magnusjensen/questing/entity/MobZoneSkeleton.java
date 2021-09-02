package dk.magnusjensen.questing.entity;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.zones.MobZone;
import dk.magnusjensen.questing.util.QuestingUtils;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntitySkeleton;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MobZoneSkeleton extends EntitySkeleton {
	private MobZone zone;

	public MobZoneSkeleton(EntityTypes<? extends EntitySkeleton> var0, World world, MobZone zone) {
		this(var0, world);
		this.zone = zone;
	}

	public MobZoneSkeleton(EntityTypes<? extends EntitySkeleton> var0, World var1) {
		super(var0, var1);
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
}
