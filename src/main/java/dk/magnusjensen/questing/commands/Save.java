package dk.magnusjensen.questing.commands;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.util.MobZoneIO;
import dk.magnusjensen.questing.util.PlayerIO;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Save implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		PlayerIO.savePlayers(Questing.players);
		MobZoneIO.saveMobZones(Questing.mobZones);
		return true;
	}
}
