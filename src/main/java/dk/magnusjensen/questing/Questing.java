package dk.magnusjensen.questing;

import dk.magnusjensen.questing.commands.*;
import dk.magnusjensen.questing.data.Quest;
import dk.magnusjensen.questing.listeners.OnPlayerJoin;
import dk.magnusjensen.questing.listeners.QuestNPCInteract;
import dk.magnusjensen.questing.listeners.UUIDCheckListener;
import dk.magnusjensen.questing.util.PlayerIO;
import dk.magnusjensen.questing.util.QuestParser;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Questing extends JavaPlugin {

	public static final String DATA_PATH = "plugins/questing/";

	public static HashMap<UUID, ArrayList<Quest>> quests = new HashMap<>();
	public static HashMap<UUID, HashMap<String, ArrayList<String>>> players = new HashMap<>();

	@Override
	public void onEnable() {
		// Plugin startup logic
		this.getCommand("uuidcheck").setExecutor(new UUIDCheck());
		this.getCommand("abandon").setExecutor(new Abandon());
		this.getCommand("start").setExecutor(new Start());
		this.getCommand("deny").setExecutor(new Deny());
		this.getCommand("save").setExecutor(new Save());



		getServer().getPluginManager().registerEvents(new UUIDCheckListener(), this);
		getServer().getPluginManager().registerEvents(new QuestNPCInteract(), this);
		getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);

		quests = QuestParser.startParsingQuests();
		players = PlayerIO.startParsingPlayers();


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
	}
}
