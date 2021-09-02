package dk.magnusjensen.questing.listeners;

import dk.magnusjensen.questing.Questing;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

import static net.minecraft.server.v1_16_R3.SoundEffects.ENTITY_GENERIC_HURT;

public class DisablingDamageQuestgivers implements Listener {

	@EventHandler
	public void onQuestgiverDamage(EntityDamageByEntityEvent event) {
		UUID uuid = event.getEntity().getUniqueId();
		if (Questing.quests.containsKey(uuid)) {
			((CraftLivingEntity) event.getEntity()).getHandle().playSound(ENTITY_GENERIC_HURT, 1.0f, 0.2f);

			event.setCancelled(true);
			event.getDamager().sendMessage("[Questing] Ouch...");
		}
	}
}
