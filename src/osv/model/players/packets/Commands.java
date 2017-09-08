package osv.model.players.packets;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import osv.Config;
import osv.Server;
import osv.ServerState;
import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;
import osv.punishments.PunishmentType;
import osv.util.Misc;

/**
 * Commands
 **/
public class Commands implements PacketType {

	public static final Map<String, Command> COMMAND_MAP = new TreeMap<>();

	public static boolean executeCommand(Player c, String playerCommand, String commandPackage) {
		String commandName = Misc.findCommand(playerCommand);
		String commandInput = Misc.findInput(playerCommand);
		String className;

		if (commandName.length() <= 0) {
			return true;
		} else if (commandName.length() == 1) {
			className = commandName.toUpperCase();
		} else {
			className = Character.toUpperCase(commandName.charAt(0)) + commandName.substring(1).toLowerCase();
		}
		try {
			String path = "osv.model.players.packets.commands." + commandPackage + "." + className;

			if (!COMMAND_MAP.containsKey(path)) {
				initialize(path);
			}
			COMMAND_MAP.get(path).execute(c, commandInput);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		} catch (Exception e) {
			c.sendMessage("Error while executing the following command: " + playerCommand);
			e.printStackTrace();
			return true;
		}
	}

	private static void initialize(String path) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> commandClass = Class.forName(path);
		Object instance = commandClass.newInstance();
		if (instance instanceof Command) {
			Command command = (Command) instance;
			COMMAND_MAP.putIfAbsent(path, command);
		}
	}

	public static void initializeCommands() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		ClassPath classPath = ClassPath.from(Commands.class.getClassLoader());
		String[] packages = { "osv.model.players.packets.commands.admin", "osv.model.players.packets.commands.all", "osv.model.players.packets.commands.donator",
				"osv.model.players.packets.commands.helper", "osv.model.players.packets.commands.moderator", "osv.model.players.packets.commands.owner", "osv.model.players.packets.commands.developer" };

		for (String pack : packages) {
			for (ClassInfo classInfo : classPath.getTopLevelClasses(pack)) {
				initialize(classInfo.getName());
			}
		}
	}

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		String playerCommand = c.getInStream().readString();
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (c.getTutorial().isActive()) {
			c.getTutorial().refresh();
			return;
		}
		if (c.isStuck) {
			c.isStuck = false;
			c.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
			return;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(c) && !c.getRights().isOrInherits(Right.OWNER)) {
			c.sendMessage("You cannot execute a command whilst trading, or dueling.");
			return;
		}

		if (playerCommand.startsWith("changepass")) {
			if (Config.SERVER_STATE == ServerState.PUBLIC_PRIMARY)
			Server.getChatLogHandler().logMessage(c, "Command", "", "::changepassword");
		} else {
			if (Config.SERVER_STATE == ServerState.PUBLIC_PRIMARY)
			Server.getChatLogHandler().logMessage(c, "Command", "", "::" + playerCommand);
		}

		if (playerCommand.startsWith("/")) {
			if (Server.getPunishments().contains(PunishmentType.MUTE, c.playerName) || Server.getPunishments().contains(PunishmentType.NET_BAN, c.connectedFrom)) {
				c.sendMessage("You are muted for breaking a rule.");
				return;
			}
			if (c.clan != null) {
				c.clan.sendChat(c, playerCommand);
				return;
			}
			c.sendMessage("You can only do this in a clan chat..");
			return;
		}

		if (c.getRights().isOrInherits(Right.OWNER) && executeCommand(c, playerCommand, "owner")) {
			return;
		} else if (c.getRights().isOrInherits(Right.GAME_DEVELOPER) && executeCommand(c, playerCommand, "developer")) {
			return;	
		} else if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && executeCommand(c, playerCommand, "admin")) {
			return;
		} else if (c.getRights().isOrInherits(Right.MODERATOR) && executeCommand(c, playerCommand, "moderator")) {
			return;
		} else if (c.getRights().isOrInherits(Right.HELPER) && executeCommand(c, playerCommand, "helper")) {
			return;
		} else if (c.getRights().isOrInherits(Right.CONTRIBUTOR) && executeCommand(c, playerCommand, "donator")) {
			return;
		} else if (executeCommand(c, playerCommand, "all")) {
			return;
		}

	}
}