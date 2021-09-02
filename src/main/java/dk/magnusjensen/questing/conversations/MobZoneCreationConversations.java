package dk.magnusjensen.questing.conversations;

import dk.magnusjensen.questing.Questing;
import dk.magnusjensen.questing.data.zones.MobZone;
import org.bukkit.conversations.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MobZoneCreationConversations {
	public static class MobZoneConfirmPrompt extends BooleanPrompt {

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, boolean input) {
			if (input) {
				BoundingBox area = (BoundingBox) context.getSessionData("boundingBox");
				int amountToKeep = Integer.parseInt((String) context.getSessionData("amountToKeepNew"));
				int respawnTime = Integer.parseInt((String) context.getSessionData("respawnTimeNew"));
				String mobType = (String) context.getSessionData("mobTypeNew");
				context.getForWhom().sendRawMessage("Creation done.\nInitializing the mob zone.");

				new MobZone(Questing.mobZones.size() + 1, amountToKeep, respawnTime, mobType.toLowerCase(), area,  ((CraftPlayer)context.getForWhom()).getHandle().getWorldServer());
				context.getForWhom().sendRawMessage("You just created a new mobzone with the ID: " + Questing.mobZones.size());

			} else {
				context.getForWhom().sendRawMessage("Creation was abandoned.");
			}
			return END_OF_CONVERSATION;
		}

		@Override
		public @NotNull String getPromptText(@NotNull ConversationContext context) {
			StringBuilder sb = new StringBuilder()
					.append("Are you sure you want to confirm these settings?\n")
					.append("Mob Type: ").append((String) context.getSessionData("mobTypeNew")).append("\n")
					.append("Respawn time: ").append(Integer.parseInt((String) context.getSessionData("respawnTimeNew"))).append("\n")
					.append("Amount To Keep alive: ").append(Integer.parseInt((String) context.getSessionData("amountToKeepNew"))).append("\n");
			return sb.toString();
		}
	}

	public static class MobZoneTypePrompt extends ValidatingPrompt {

		@Override
		protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
			if (Arrays.stream(EntityType.values()).anyMatch((et) -> et.getName().equalsIgnoreCase(input))) {
				context.setSessionData("mobTypeNew", input);
				return true;
			}
			return false;
		}

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			return new MobZoneCreationConversations.MobZoneConfirmPrompt();
		}

		@Override
		protected @Nullable String getFailedValidationText(@NotNull ConversationContext context, @NotNull String invalidInput) {
			return "You need to specify a valid mob type.";
		}

		@Override
		public @NotNull String getPromptText(@NotNull ConversationContext context) {
			return "Specify the mob type that should be spawned here:";
		}
	}

	public static class MobZoneRespawnPrompt extends NumericPrompt {

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number input) {
			context.setSessionData("respawnTimeNew", Integer.toString(input.intValue()));
			return new MobZoneCreationConversations.MobZoneTypePrompt();
		}

		@Override
		public @NotNull String getPromptText(@NotNull ConversationContext context) {
			return "Specify the time in seconds between respawns:";
		}
	}


	public static class MobZoneAmountPrompt extends NumericPrompt {

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number input) {
			context.setSessionData("amountToKeepNew", Integer.toString(input.intValue()));
			return new MobZoneCreationConversations.MobZoneRespawnPrompt();
		}

		@Override
		public @NotNull String getPromptText(@NotNull ConversationContext context) {
			return "Specify the amount of mobs to keep alive in the mobzone:";
		}
	}
}
