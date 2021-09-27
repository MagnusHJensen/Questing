package dk.magnusjensen.questing;

import dk.magnusjensen.questing.commands.*;
import dk.magnusjensen.questing.data.Quest;
import dk.magnusjensen.questing.data.zones.MobZone;
import dk.magnusjensen.questing.listeners.*;
import dk.magnusjensen.questing.util.MobZoneIO;
import dk.magnusjensen.questing.util.PlayerIO;
import dk.magnusjensen.questing.util.QuestParser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Questing extends JavaPlugin {

	public static Questing INSTANCE;
	public static final String DATA_PATH = "plugins/questing/";

	public static HashMap<UUID, ArrayList<Quest>> quests = new HashMap<>();
	public static HashMap<UUID, HashMap<String, ArrayList<String>>> players = new HashMap<>();
	public static ArrayList<MobZone> mobZones = new ArrayList<>();
	public static ConversationFactory conversationFactory = new ConversationFactory(Bukkit.getPluginManager().getPlugin("questing"));

	public static Economy ECONOMY = null;

	@Override
	public void onEnable() {
		// Plugin startup logic
		INSTANCE = this;

		this.getCommand("uuidcheck").setExecutor(new UUIDCheck());
		this.getCommand("abandon").setExecutor(new Abandon());
		this.getCommand("start").setExecutor(new Start());
		this.getCommand("deny").setExecutor(new Deny());
		this.getCommand("save").setExecutor(new Save());
		this.getCommand("mobzone").setExecutor(new CreateMobZone());

		getServer().getPluginManager().registerEvents(new UUIDCheckListener(), this);
		getServer().getPluginManager().registerEvents(new QuestNPCInteract(), this);
		getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
		getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		getServer().getPluginManager().registerEvents(new DisablingDamageQuestgivers(), this);

		mobZones = MobZoneIO.startParsingMobZones();
		quests = QuestParser.startParsingQuests();
		players = PlayerIO.startParsingPlayers();

		// Setup vault api economics
		if (!setupEconomy() ) {
			Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}


		new BukkitRunnable() {
			@Override
			public void run() {
				PlayerIO.savePlayers(players);
			}
		}.runTaskTimerAsynchronously(this, 6000, 12000);

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic

		PlayerIO.savePlayers(players);

		for (MobZone zone : mobZones) {
			zone.cleanUp(); // Clean up mobs on disable.
		}

		MobZoneIO.saveMobZones(mobZones);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		ECONOMY = rsp.getProvider();
		return ECONOMY != null;
	}


}
