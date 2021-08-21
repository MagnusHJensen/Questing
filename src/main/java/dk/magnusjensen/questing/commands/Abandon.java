package dk.magnusjensen.questing.commands;

import dk.magnusjensen.questing.Questing;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Abandon implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage("Quest ID was not passed.");
			return false;
		}

		if (sender instanceof Player) {
			Player player = (Player) sender;
			UUID questGiver = UUID.fromString(args[0].split("/")[0]);
			int id = Integer.parseInt(args[0].split("/")[1]);
			player.sendMessage("Abandoning quest with id: " + args[0]);
			Questing.quests.get(questGiver).get(id - 1).abandonQuest(player);
			return true;
		}

		sender.sendMessage("Command is only allowed for players");
		return false;
	}
}
