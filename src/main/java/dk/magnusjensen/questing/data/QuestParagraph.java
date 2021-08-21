package dk.magnusjensen.questing.data;

import dk.magnusjensen.questing.Questing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

public class QuestParagraph {
	private ArrayList<String> npcLines;
	private ArrayList<String> playerLines;
	private boolean random;
	private boolean playerLineComesFirst;

	public QuestParagraph(ArrayList<String> npcLines, ArrayList<String> playerLines, boolean random, boolean playerLineComesFirst) {
		this.npcLines = npcLines;
		this.playerLines = playerLines;
		this.random = random;
		this.playerLineComesFirst = playerLineComesFirst;
	}

	public void play(Quest quest, Player player) {
		if (random) {
			player.sendMessage(formatText(quest, npcLines.get((int) Math.floor(Math.random() * npcLines.size())), player));
			return;
		} else {
			new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {

					/*if (playerLineComesFirst) {
						player.sendMessage(formatPlayerText(quest, playerLines.get(i), player));
						player.sendMessage(formatNPCText(quest, npcLines.get(i), player));
					} else {
						player.sendMessage(formatNPCText(quest, playerLines.get(i), player));
						player.sendMessage(formatPlayerText(quest, npcLines.get(i), player));
					}*/

					player.sendMessage(formatNPCText(quest, npcLines.get(i), player));

					++i;
					if (i >= npcLines.size())
						this.cancel();

				}
			}.runTaskTimer(Questing.getPlugin(Questing.class), 0, 100);

		}

	}

	public void play(Quest quest, Player player, TextComponent... components) {
		if (random) {
			String line = formatText(quest, npcLines.get((int) Math.floor(Math.random() * npcLines.size())), player);
			TextComponent fullText = Component.text(line);
			for (TextComponent comp : components) {
				fullText = fullText.append(comp);
			}
			player.sendMessage(fullText);
			return;
		} else {

			new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {
					String line = formatText(quest, npcLines.get(i), player);
					TextComponent fullText = Component.text(line);
					for (TextComponent comp : components) {
						fullText = fullText.append(comp);
					}
					player.sendMessage(fullText);

					++i;
					if (i >= npcLines.size())
						this.cancel();
				}

			}.runTaskTimer(Questing.getPlugin(Questing.class), 0, 100);

		}
	}

	public void playWithAbandon(Quest quest, Player player, ClickEvent abandonEvent) {
		TextComponent abandonText = Component.text("\n[Abandon]")
				.color(TextColor.fromHexString("#FF5555"))
				.clickEvent(abandonEvent);
		play(quest, player, abandonText);
	}

	public void playWithAcceptOrDeny(Quest quest, Player player, ClickEvent acceptEvent, ClickEvent denyEvent) {
		TextComponent acceptText = Component.text("\n[Accept]")
				.color(TextColor.fromHexString("#55FF55"))
				.clickEvent(acceptEvent);
		TextComponent denyText = Component.text("\n[Deny]")
				.color(TextColor.fromHexString("#FF5555"))
				.clickEvent(denyEvent);
		play(quest, player, acceptText, denyText);
	}

	public String formatText(Quest quest, String text, Player player) {
		text = text.replaceAll("%PLAYER%", player.getDisplayName());

		for (Map.Entry<String, String> entry : quest.formatting.entrySet()) {
			text = text.replaceAll("%" + entry.getKey() + "%", entry.getValue());
		}

		text = "[Questing] " + text;
		return text;
	};

	public String formatNPCText(Quest quest, String text, Player player) {
		return formatText(quest, text, player);
		//TODO: Add some sort of NPC identifer, maybe their displayname.
	}

	public String formatPlayerText(Quest quest, String text, Player player) {
		return formatText(quest, text, player);
		// Todo: add some visual that it's a plyer line.
	}

}
