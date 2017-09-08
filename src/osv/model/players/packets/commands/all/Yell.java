package osv.model.players.packets.commands.all;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import osv.Server;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.RightGroup;
import osv.model.players.packets.commands.Command;
import osv.punishments.PunishmentType;

/**
 * Tells the player they need to be a donator to use this feature.
 * 
 * @author Emiel
 */
public class Yell extends Command {

	private static final Right[] PERMITTED = { Right.CONTRIBUTOR, Right.SUPPORTER, Right.SPONSOR, Right.VIP, Right.SUPER_VIP, Right.MEGA_VIP, 
												Right.LEGENDARY, Right.HELPER, Right.MODERATOR, Right.ADMINISTRATOR,
												Right.OWNER, Right.GAME_DEVELOPER, Right.YOUTUBER, Right.HITBOX };

	private static final String[] ILLEGAL_ARGUMENTS = { ":tradereq:", "<img", "@cr", "<tran", "#url#", ":duelreq:", ":chalreq:" };

	public void test(Player player) {
		for (Right right : Right.values()) {
			player.sendMessage(formatMessage("", right.name(), right.getColor(), right.getValue() - 1, "Hello world!"));
		}
	}

	@Override
	public void execute(Player player, String input) {
		RightGroup rights = player.getRights();

		Set<Right> prohibited = new HashSet<>(Arrays.asList(PERMITTED));

		long count = rights.getSet().stream().filter(r -> prohibited.stream().anyMatch(p -> r.isOrInherits(p))).count();

		if (count == 0) {
			player.sendMessage("You do not have the rights to access this command.");
			return;
		}
		
		if (rights.getPrimary() == Right.PLAYER || rights.getPrimary() == Right.ULTIMATE_IRONMAN) {
			player.sendMessage("You do not have the rights to access this command");
			return;
		}

		long delay = getDelay(player);
		if (System.currentTimeMillis() - player.lastYell < delay) {
			player.sendMessage("You must wait another " + TimeUnit.MILLISECONDS.toSeconds(player.lastYell + delay - System.currentTimeMillis()) + " seconds before you can yell.");
			return;
		}
		if (Server.getPunishments().contains(PunishmentType.MUTE, player.playerName)) {
			player.sendMessage("You are muted, you cannot use this system.");
			return;
		}
		if (Server.getPunishments().contains(PunishmentType.NET_MUTE, player.connectedFrom)) {
			player.sendMessage("You are muted, you cannot use this system.");
			return;
		}
		if (System.currentTimeMillis() < player.muteEnd) {
			player.sendMessage("You are muted, you cannot use this system.");
			return;
		}

		for (String argument : ILLEGAL_ARGUMENTS) {
			if (input.contains(argument) && rights.getPrimary() != Right.OWNER) {
				player.sendMessage("Your message contains an illegal set of characters, you cannot yell this.");
				return;
			}
		}
		String message = formatMessage(player.getTitles().getCurrentTitle(), StringUtils.capitalize(player.playerName.toLowerCase()), player.getRights().getPrimary().getColor(),
				player.getRights().getPrimary().getValue() - 1, StringUtils.capitalize(input));
		player.lastYell = System.currentTimeMillis();
		PlayerHandler.executeGlobalMessage(message);
	}

	private final String formatMessage(String title, String username, String color, int rights, String message) {
		if (title.equalsIgnoreCase("None")) {
			title = "";
		}
		return "[<img=" + rights + "><col=" + color + ">" + title + "</col>][<col=" + color + ">" + username + "</col>]:" + StringUtils.capitalize(message.toLowerCase());
	}

	private final long getDelay(Player player) {
		RightGroup rights = player.getRights();

		if (rights.isOrInherits(Right.MODERATOR)) {
			return 0;
		}
		if (rights.contains(Right.LEGENDARY) || rights.contains(Right.HELPER)) {
			return TimeUnit.SECONDS.toMillis(10);		
		} else if (rights.contains(Right.MEGA_VIP)) {
			return TimeUnit.SECONDS.toMillis(15);
		} else if (rights.contains(Right.SUPER_VIP)) {
			return TimeUnit.SECONDS.toMillis(20);
		} else if (rights.contains(Right.VIP)) {
			return TimeUnit.SECONDS.toMillis(25);
		} else if (rights.contains(Right.SUPPORTER)) {
			return TimeUnit.SECONDS.toMillis(30);
		} else if (rights.contains(Right.SPONSOR)) {
			return TimeUnit.SECONDS.toMillis(45);
		} else if (rights.contains(Right.CONTRIBUTOR) || 
					rights.contains(Right.HITBOX) || 
					rights.contains(Right.YOUTUBER)) {
			return TimeUnit.SECONDS.toMillis(60);
		} else {
			return TimeUnit.SECONDS.toMillis(60);
		}
	}


	@Override
	public Optional<String> getDescription() {
		return Optional.of("Sends a global chat message");
	}

	@Override
	public Optional<String> getParameter() {
		return Optional.of("message");
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}
