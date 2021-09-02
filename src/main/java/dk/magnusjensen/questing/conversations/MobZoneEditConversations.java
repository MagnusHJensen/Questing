package dk.magnusjensen.questing.conversations;

import dk.magnusjensen.questing.data.zones.MobZone;
import org.bukkit.conversations.*;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MobZoneEditConversations {
	public static class MobZoneConfirmPrompt extends BooleanPrompt {

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, boolean input) {
			if (input) {
				MobZone zone = (MobZone) context.getSessionData("zone");
				zone.setAmountToKeep(Integer.parseInt((String) context.getSessionData("amountToKeepNew")));
				zone.setRespawnTime(Integer.parseInt((String) context.getSessionData("respawnTimeNew")));
				zone.setType((String) context.getSessionData("mobTypeNew"));
				context.getForWhom().sendRawMessage("Edit has been applied.\nRe-initting the mob zone.");
				zone.reInit();
			} else {
				context.getForWhom().sendRawMessage("Edit was discarded.");
			}
			return END_OF_CONVERSATION;
		}

		@Override
		public @NotNull String getPromptText(@NotNull ConversationContext context) {
			StringBuilder sb = new StringBuilder()
					.append("Are you sure you want to confirm these changes?\n")
					.append("Mob Type: ").append((String) context.getSessionData("mobType")).append(" -> ").append((String) context.getSessionData("mobTypeNew")).append("\n")
					.append("Respawn time: ").append(Integer.parseInt((String) context.getSessionData("respawnTime"))).append(" -> ").append(Integer.parseInt((String) context.getSessionData("respawnTimeNew"))).append("\n")
					.append("Amount To Keep alive: ").append(Integer.parseInt((String) context.getSessionData("amountToKeep"))).append(" -> ").append(Integer.parseInt((String) context.getSessionData("amountToKeepNew"))).append("\n");
			return sb.toString();
		}
	}

	public static class MobZoneTypePrompt extends ValidatingPrompt {

		@Override
		protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
			if (Arrays.stream(EntityType.values()).filter((et) -> et.getName().equalsIgnoreCase(input)).count() == 1) {
				context.setSessionData("mobTypeNew", input);
				return true;
			} else if (input.equalsIgnoreCase("pass")) {
				context.setSessionData("mobTypeNew", context.getSessionData("mobType"));
				return true;
			}
			return false;
		}

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			return new MobZoneConfirmPrompt();
		}

		@Override
		public @NotNull String getPromptText(@NotNull ConversationContext context) {
			return "Specify the mob type that should be spawned here: (Current is " + context.getSessionData("mobType") + ")";
		}
	}

	public static class MobZoneRespawnPrompt extends NumericPrompt {

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number input) {
			context.setSessionData("respawnTimeNew", Integer.toString(input.intValue()));
			return new MobZoneTypePrompt();
		}

		@Override
		public @NotNull String getPromptText(@NotNull ConversationContext context) {
			return "Specify the time in seconds between respawns: (Current is " + context.getSessionData("respawnTime") + ")";
		}
	}


	public static class MobZoneAmountPrompt extends NumericPrompt {

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number input) {
			context.setSessionData("amountToKeepNew", Integer.toString(input.intValue()));
			return new MobZoneRespawnPrompt();
		}

		@Override
		public @NotNull String getPromptText(@NotNull ConversationContext context) {
			return "Specify the amount of mobs to keep alive in the mobzone: (Current is " + context.getSessionData("amountToKeep") + ")";
		}
	}
}
