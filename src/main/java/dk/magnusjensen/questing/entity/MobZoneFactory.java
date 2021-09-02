package dk.magnusjensen.questing.entity;

import dk.magnusjensen.questing.data.zones.MobZone;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;

public class MobZoneFactory {

	public static EntityLiving create(String type, World world, MobZone zone) {
		if (type.equalsIgnoreCase("zombie")) {
			return createZombie(world, zone);
		} else if (type.equalsIgnoreCase("skeleton")) {
			return createSkeleton(world, zone);
		}
		return null;
	}

	public static MobZoneZombie createZombie(World world, MobZone zone) {
		return new MobZoneZombie(world, zone);
	}

	public static MobZoneSkeleton createSkeleton(World world, MobZone zone) {
		return new MobZoneSkeleton(EntityTypes.SKELETON, world, zone);
	}
}
