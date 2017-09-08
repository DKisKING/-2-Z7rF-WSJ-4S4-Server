package osv.model.players.packets.commands.all;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;

import osv.Server;
import osv.event.CycleEvent;
import osv.event.CycleEventContainer;
import osv.event.CycleEventHandler;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;

/**
 * Moves the selected player home after a period of time.
 * 
 * @author Matt
 */
public class Stuck extends Command {

	@Override
	public void execute(Player c, String input) {
		
		if (Server.getMultiplayerSessionListener().inAnySession(c) || c.underAttackBy > 0) {
			c.sendMessage("Finish what you are doing before doing this.");
			return;
		}
		
		if (c.inClanWars() || c.inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		
		if (!c.inWild()) {
			c.sendMessage("You can only use this in the wilderness.");
			return;
		}

		if (c.isStuck == false) {
			c.isStuck = true;
			List<Player> staff = PlayerHandler.nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(Right.MODERATOR)).collect(Collectors.toList());
			
			if (staff.size() > 0) {
				PlayerHandler.sendMessage("@blu@[Stuck] " + WordUtils.capitalize(c.playerName) + "" + " is stuck, teleport and help them.", staff);
				c.sendMessage("@red@You've activated stuck command and the staff online has been notified.");
				c.sendMessage("@red@Your account will be moved home in approximately 5 minutes.");
				c.sendMessage("@red@You cannot attempt ANY actions whatsoever other than talking.");
				c.sendMessage("@red@Or else your timer will be reset..");
				c.getPA().logStuck();
			} else {
				c.sendMessage("@red@You've activated stuck command and there are no staff-members online.");
				c.sendMessage("@red@Your account will be moved home in approximately 5 minutes.");
				c.sendMessage("@red@You cannot attempt ANY actions whatsoever other than talking.");
				c.sendMessage("@red@Or else your timer will be reset..");
			}
			
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c.disconnected || c.isStuck == false) {
						container.stop();
						return;
					}
					if (c.isStuck) {
						c.getPlayerAssistant().movePlayer(3093, 3493, 0);
						c.sendMessage("@red@Your account has been moved home.");
						c.isStuck = false;
					}
					container.stop();
				}

				@Override
				public void stop() {

				}
			}, 1000);

		} else {
			c.sendMessage("@red@You have already activated stuck command, stay patient.");
			return;
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you home if you are ever stuck");
	}

}
