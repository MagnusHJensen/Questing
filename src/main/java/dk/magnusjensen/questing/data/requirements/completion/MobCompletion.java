package dk.magnusjensen.questing.data.requirements.completion;

import dk.magnusjensen.questing.data.Quest;
import dk.magnusjensen.questing.data.zones.MobZone;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class MobCompletion implements IQuestCompletion {
	private int amountToKill;
	private ArrayList<MobZone> mobZones ;

	public MobCompletion(int amountToKill, ArrayList<MobZone> mobZones) {
		this.amountToKill = amountToKill;
		this.mobZones = mobZones;
	}

	@Override
	public void completeQuest(Quest quest, Player player) {

	}

	@Override
	public boolean canCompleteQuest(Quest quest, Player player) {
		if (player.getPersistentDataContainer().has(new NamespacedKey("questing", quest.getCustomQuestId()), PersistentDataType.INTEGER)) {
			int amountKilled = player.getPersistentDataContainer().get(new NamespacedKey("questing", quest.getCustomQuestId()),  PersistentDataType.INTEGER);
			return amountKilled >= amountToKill;
		}
		return false;
	}

	@Override
	public boolean hasMobKills() {
		return true;
	}

	@Override
	public int getAmount() {
		return this.amountToKill;
	}
}
