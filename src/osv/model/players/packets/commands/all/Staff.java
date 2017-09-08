package osv.model.players.packets.commands.all;

import java.util.Optional;

import org.apache.commons.lang3.text.WordUtils;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;

/**
 * Sends the player a message containing a list of all online staff members.
 * 
 * @author Emiel - Edit by Matt
 */
public class Staff extends Command {

	@Override
	public void execute(Player c, String input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < PlayerHandler.players.length; i++) {
			if (PlayerHandler.players[i] != null) {
				Player c2 = PlayerHandler.players[i];
				if (c2.getRights().isOrInherits(Right.HELPER) || c2.getRights().isOrInherits(Right.MODERATOR) || c2.getRights().isOrInherits(Right.ADMINISTRATOR)
						|| c2.getRights().isOrInherits(Right.OWNER)) {
					sb.append(c2.playerName + ", ");
				}
			}
		}
		if (sb.length() > 0) {
			String result = "@blu@Staff Online@bla@: " + sb.substring(0, sb.length() - 2);
			String[] wrappedLines = WordUtils.wrap(result, 68).split(System.getProperty("line.separator"));
			for (String line : wrappedLines) {
				c.sendMessage(line);
			}
		} else {
			c.sendMessage("@blu@There are currently no staff online!");
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Lists all online staff players");
	}

}
