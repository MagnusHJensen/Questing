package dk.magnusjensen.questing.data.zones;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.Quest;
import dk.magnusjensen.questing.entity.MobZoneFactory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;

public class MobZone {
	public int id;
	private ArrayList<String> quests = new ArrayList<>();
	public int amountToKeep;
	private int currentAmount;
	public int respawnTime;
	public String type;
	public BoundingBox area;
	public net.minecraft.server.v1_16_R3.World world;

	public ArrayList<EntityLiving> entities = new ArrayList<>();
	private RespawnTask respawnTask;

	public MobZone(int id, int amountToKeep, int respawnTime, String type, BoundingBox box, net.minecraft.server.v1_16_R3.World world) {
		this.id = id;
		this.amountToKeep = amountToKeep;
		this.respawnTime = respawnTime;
		this.type = type;
		this.area = box;
		this.world = world;

		this.init();
	}

	public void linkQuest(String quest) {
		this.quests.add(quest);
	}




	private void init() {
		if (!Questing.mobZones.contains(this)) {
			Questing.mobZones.add(this);
		}
		this.respawnTask = new RespawnTask(this);
		this.respawnTask.runTaskTimer(Questing.INSTANCE, respawnTime * 20, respawnTime * 20);

	}

	public void cleanUp() {
		this.respawnTask.cancel();
		this.killMobs();
	}

	private void killMobs() {
		for (EntityLiving entity : entities) {
			entity.killEntity();
		}
	}

	public void reInit() {
		cleanUp();
		init();
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public void setAmountToKeep(int amountToKeep) {
		this.amountToKeep = amountToKeep;
	}

	public void showZoneTitle(Audience audience) {

		final Title.Times times = Title.Times.of(Duration.ofMillis(250), Duration.ofMillis(2000), Duration.ofMillis(250));
		final Component mainTitle = Component.text("MobZone " + this.id, NamedTextColor.RED);
		final Component subTitle = Component.text(this.type + "s - Level: 1", NamedTextColor.GRAY);

		final Title title = Title.title(mainTitle, subTitle, times);
		audience.showTitle(title);

	}

	public void addMobToZone(EntityLiving entity) {
		entities.add(entity);
		this.currentAmount++;
	}

	public void removeMobFromZone(EntityLiving entity) {
		entities.remove(entity);
		this.currentAmount--;
	}

	public ArrayList<String> getLinkedQuests() {
		return quests;
	}


	private class RespawnTask extends BukkitRunnable {
		private MobZone zone;

		public RespawnTask(MobZone zone) {
			this.zone = zone;
		}

		@Override
		public void run() {

			if (zone.currentAmount >= amountToKeep) {
				return;
			}
			Random random = new Random();
			double x = random.nextInt((int) (area.getMaxX() - area.getMinX())) + area.getMinX();
			double z = random.nextInt((int) (area.getMaxZ() - area.getMinZ())) + area.getMinZ();
			BlockPosition spawnPos = world.getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, new BlockPosition(x, 0,  z));


			if (spawnPos.getY() > area.getMaxY()) {
				spawnPos = spawnPos.down(spawnPos.getY() - (int)area.getMaxY());
				boolean groundFlag = false;
				do {
					if (!world.getWorld().getBlockAt(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()).isSolid()) {
						spawnPos = spawnPos.down();
					} else {
						groundFlag = true;
						spawnPos = spawnPos.up();
					}
				} while (!groundFlag);
			}


			EntityLiving entity = MobZoneFactory.create(type, world, zone);

			for (String questId : quests) {
				entity.addScoreboardTag(questId);
			}

			entity.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
			world.addEntity(entity);

			zone.addMobToZone(entity);

		}
	}
}
