package dk.magnusjensen.questing.util;

import java.util.UUID;

public class QuestingUtils {
	public static boolean isCustomQuestId(String text) {
		try {
			UUID uuid = UUID.fromString(text.split("/")[0]);
			int id = Integer.parseInt(text.split("/")[1]);
		} catch (IllegalArgumentException e) {
			return false;
		}


		return true;
	}
}
