package dk.magnusjensen.questing.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class UUIDCheck implements CommandExecutor {
	public static ArrayList<Player> playersChecking = new ArrayList<>();
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (commandSender instanceof Player) {
			Player player = (Player) commandSender;


			if (playersChecking.contains(player)) {
				playersChecking.remove(player);
				player.sendMessage("UUID checking has been disabled.");
			} else {
				player.sendMessage("UUID checking has been enabled!\nTo Disable do /uuidcheck again.");
				playersChecking.add(player);
			}

			return true;
		} else {
			commandSender.sendMessage("Only players can enable UUID checking.");
			return false;
		}

	}
}
