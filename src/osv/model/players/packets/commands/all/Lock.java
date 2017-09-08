package osv.model.players.packets.commands.all;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Prevents the player from gaining any experience.
 * 
 * @author Emiel
 */
public class Lock extends Command {

	@Override
	public void execute(Player c, String input) {
		if (c.expLock == false) {
			c.expLock = true;
			c.sendMessage("Your XP is now: @red@locked@bla@.");
		} else {
			c.expLock = false;
			c.sendMessage("Your XP is now: @gre@unlocked@bla@.");
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Disables or re-enables combat experience");
	}

}
