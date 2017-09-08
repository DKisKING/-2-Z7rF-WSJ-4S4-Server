package osv.model.players.packets.commands.all;

import java.util.Optional;

import com.rspserver.motivote.Motivote;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/*public class Auth extends Command {
	
	String lastAuth = "";
	
	@Override
	public void execute(Player player, String input) {
		if (lastAuth.equals(input)) {
			player.sendMessage("@cr10@This auth was recently claimed.");
			return;
		}
		lastAuth = input;
		Motivote.run(player, input);
	}
	
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Claims an auth code from ::vote");
	}

	@Override
	public Optional<String> getParameter() {
		return Optional.of("#####");
	}

}*/
