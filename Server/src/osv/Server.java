package osv;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;

import osv.clip.ObjectDef;
import osv.clip.Region;
import osv.clip.doors.DoorDefinition;
import osv.database.ConnectionPool;
import osv.database.logging.BarrowsLogHandler;
import osv.database.logging.ChatLogHandler;
import osv.database.logging.ErrorLogHandler;
import osv.database.logging.HighscoresHandler;
import osv.database.logging.LoginLogHandler;
import osv.database.logging.PunishmentLogHandler;
import osv.database.logging.PvMLogHandler;
import osv.database.logging.PvPLogHandler;
import osv.database.players_online.PlayersOnline;
import osv.event.CycleEventHandler;
import osv.event.EventHandler;
import osv.event.impl.BonusApplianceEvent;
import osv.event.impl.DidYouKnowEvent;
import osv.event.impl.SkeletalMysticEvent;
import osv.event.impl.WheatPortalEvent;
import osv.model.content.Wogw;
import osv.model.content.godwars.GodwarsEquipment;
import osv.model.content.godwars.GodwarsNPCs;
import osv.model.content.grand_exchange.GrandExchange;
import osv.model.content.trails.CasketRewards;
import osv.model.holiday.HolidayController;
import osv.model.items.ItemDefinition;
import osv.model.minigames.FightPits;
import osv.model.multiplayer_session.MultiplayerSessionListener;
import osv.model.npcs.NPCHandler;
import osv.model.npcs.drops.DropManager;
import osv.model.players.PlayerHandler;
import osv.model.players.PlayerSave;
import osv.model.players.packets.Commands;
import osv.net.PipelineFactory;
import osv.punishments.PunishmentCycleEvent;
import osv.punishments.Punishments;
import osv.server.data.ServerData;
import osv.util.VotingIncentive;
import osv.util.date.GameCalendar;
import osv.util.log.Logger;
import osv.util.motivote.Motivote;
import osv.util.motivote.Reward;
import osv.world.ClanManager;
import osv.world.ItemHandler;
import osv.world.ShopHandler;
import osv.world.objects.GlobalObjects;

/**
 * The main class needed to start the server.
 * 
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmctruck30 Revised by Shawn Notes by Shawn
 */
public class Server {

	private static final Punishments PUNISHMENTS = new Punishments();

	private static final DropManager dropManager = new DropManager();

	private static final PlayersOnline playersOnline = new PlayersOnline();

	private static final Motivote<Reward> MOTIVOTE = new Motivote<>(new VotingIncentive(), "https://www.os-v.org/incentive/", "4e1aaa6a");

	/**
	 * A class that will manage game events
	 */
	private static final EventHandler events = new EventHandler();

	//private static final Hitbox hitbox = new Hitbox();

	/**
	 * Represents our calendar with a given delay using the TimeUnit class
	 */
	private static GameCalendar calendar = new osv.util.date.GameCalendar(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"), "GMT-3:00");

	private static HolidayController holidayController = new HolidayController();

	private static MultiplayerSessionListener multiplayerSessionListener = new MultiplayerSessionListener();

	private static GlobalObjects globalObjects = new GlobalObjects();

	/**
	 * ClanChat Added by Valiant
	 */
	public static ClanManager clanManager = new ClanManager();

	/**
	 * Sleep mode of the server.
	 */
	public static boolean sleeping;
	/**
	 * The test thingy
	 */
	public static boolean canGiveReward;

	public static long lastReward = 0;
	/**
	 * Calls the rate in which an event cycles.
	 */
	public static final int cycleRate;

	/**
	 * Server updating.
	 */
	public static boolean UpdateServer = false;

	/**
	 * Calls in which the server was last saved.
	 */
	public static long lastMassSave = System.currentTimeMillis();

	/**
	 * Calls the usage of CycledEvents.
	 */
	// private static long cycleTime, cycles, totalCycleTime, sleepTime;
	private static long sleepTime;

	/**
	 * Used for debugging the server.
	 */
	// private static DecimalFormat debugPercentFormat;

	/**
	 * Forced shutdowns.
	 */
	public static boolean shutdownServer = false;
	public static boolean shutdownClientHandler;

	public static boolean canLoadObjects = false;

	/**
	 * Used to identify the server port.
	 */
	public static int serverlistenerPort;

	/**
	 * Contains data which is saved between sessions.
	 */
	public static ServerData serverData = new ServerData();

	/**
	 * Connection pool used for database queries.
	 */
	public static ConnectionPool punishPool = new ConnectionPool("70.42.74.5", "root", "19940131Matt", "punish");

	/**
	 * Handles the logging of all punishments.
	 */
	private static PunishmentLogHandler punishmentLogHandler = new PunishmentLogHandler();

	/**
	 * Handles the logging of all logins.
	 */
	private static LoginLogHandler loginLogHandler = new LoginLogHandler();

	/**
	 * Handles the logging of all chat messages.
	 */
	private static ChatLogHandler chatLogHandler = new ChatLogHandler();

	/**
	 * Handles the logging of all PvP kills.
	 */
	private static PvPLogHandler pvpLogHandler = new PvPLogHandler();

	/**
	 * Handles the logging of all PvM kills.
	 */
	private static PvMLogHandler pvmLogHandler = new PvMLogHandler();

	/**
	 * Handles the logging of all printed runtime errors.
	 */
	private static ErrorLogHandler errorLogHandler = new ErrorLogHandler();

	/**
	 * Handles the logging of barrows rewards.
	 */
	private static BarrowsLogHandler barrowsLogHandler = new BarrowsLogHandler();

	/**
	 * Handles the updating of the highscores.
	 */
	private static HighscoresHandler highscoresHandler = new HighscoresHandler();

	/**
	 * Calls the usage of player items.
	 */
	public static ItemHandler itemHandler = new ItemHandler();

	/**
	 * Handles logged in players.
	 */
	public static PlayerHandler playerHandler = new PlayerHandler();

	/**
	 * Handles global NPCs.
	 */
	public static NPCHandler npcHandler = new NPCHandler();

	/**
	 * Handles global shops.
	 */
	public static ShopHandler shopHandler = new ShopHandler();

	/**
	 * Handles the fightpits minigame.
	 */
	public static FightPits fightPits = new FightPits();

	private static final GrandExchange GRAND_EXCHANGE = new GrandExchange();

	/**
	 * Handles the main game processing.
	 */
	private static final ScheduledExecutorService GAME_THREAD = Executors.newSingleThreadScheduledExecutor();

	private static final ScheduledExecutorService IO_THREAD = Executors.newSingleThreadScheduledExecutor();

	static {
		serverlistenerPort = Config.SERVER_STATE.getPort();
		cycleRate = 600;
		shutdownServer = false;
		// engineTimer = new SimpleTimer();
		// debugTimer = new SimpleTimer();
		sleepTime = 0;
		// debugPercentFormat = new DecimalFormat("0.0#%");
	}

	private static final Runnable SERVER_TASKS = () -> {
		try {
			itemHandler.process();
			playerHandler.process();
			npcHandler.process();
			shopHandler.process();
			globalObjects.pulse();
			CycleEventHandler.getSingleton().process();
			events.process();
			serverData.processQueue();
		} catch (Throwable t) {
			t.printStackTrace();
			t.getCause();
			t.getMessage();
			t.fillInStackTrace();
			System.out.println("Server tasks - Check for error");
			PlayerHandler.stream().filter(Objects::nonNull).forEach(PlayerSave::save);
		}
	};

	private static final Runnable IO_TASKS = () -> {
		try {
			//hitbox.update();
			playersOnline.run();
			//errorLogHandler.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	};

	@SuppressWarnings("unused")
	private static final Runnable GE_TASK = () -> {
		try {
			GRAND_EXCHANGE.update();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	};

	public static void main(java.lang.String args[]) {
		try {
			long startTime = System.currentTimeMillis();
			System.setOut(extracted());
			//System.setErr(new ErrorLogger(System.err));
			PUNISHMENTS.initialize();
			//CycleEventHandler.getSingleton().addEvent(new Object(), new WellOfGoodWillEvent(), 1500);
			events.submit(new DidYouKnowEvent());
			events.submit(new WheatPortalEvent());
			events.submit(new BonusApplianceEvent());
			events.submit(new SkeletalMysticEvent());
			events.submit(new PunishmentCycleEvent(PUNISHMENTS, 50));
			Wogw.init();
			ItemDefinition.load();
			DoorDefinition.load();
			GodwarsEquipment.load();
			GodwarsNPCs.load();
			dropManager.read();
			CasketRewards.read();
			ObjectDef.loadConfig();			
			globalObjects.loadGlobalObjectFile();
			Region.load();
			bindPorts();
			holidayController.initialize();
			Runtime.getRuntime().addShutdownHook(new ShutdownHook());
			Commands.initializeCommands();
			long endTime = System.currentTimeMillis();
			long elapsed = endTime - startTime;
			System.out.println("Tyras has successfully started up in " + elapsed + " milliseconds.");
			GAME_THREAD.scheduleAtFixedRate(SERVER_TASKS, 0, 600, TimeUnit.MILLISECONDS);
			IO_THREAD.scheduleAtFixedRate(IO_TASKS, 0, 30, TimeUnit.SECONDS);
//			if (Config.SERVER_STATE != ServerState.PRIVATE) {
//				//IO_THREAD.scheduleAtFixedRate(GE_TASK, 0, 500, TimeUnit.MILLISECONDS);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Logger extracted() {
		return new Logger(System.out);
	}

	/**
	 * Gets the sleep mode timer and puts the server into sleep mode.
	 */
	public static long getSleepTimer() {
		return sleepTime;
	}

	public static MultiplayerSessionListener getMultiplayerSessionListener() {
		return multiplayerSessionListener;
	}

	/**
	 * Java connection. Ports.
	 */
	private static void bindPorts() {
		ServerBootstrap serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		serverBootstrap.setPipelineFactory(new PipelineFactory(new HashedWheelTimer()));
		serverBootstrap.bind(new InetSocketAddress(serverlistenerPort));
	}

	public static GameCalendar getCalendar() {
		return calendar;
	}

	public static HolidayController getHolidayController() {
		return holidayController;
	}

	public static ServerData getServerData() {
		return serverData;
	}

	public static GlobalObjects getGlobalObjects() {
		return globalObjects;
	}

	public static PunishmentLogHandler getPunishmentLogHandler() {
		return punishmentLogHandler;
	}

	public static LoginLogHandler getLoginLogHandler() {
		return loginLogHandler;
	}

	public static ChatLogHandler getChatLogHandler() {
		return chatLogHandler;
	}

	public static PvPLogHandler getPvpLogHandler() {
		return pvpLogHandler;
	}

	public static PvMLogHandler getPvmLogHandler() {
		return pvmLogHandler;
	}

	public static ErrorLogHandler getErrorLogHandler() {
		return errorLogHandler;
	}

	public static BarrowsLogHandler getBarrowsLogHandler() {
		return barrowsLogHandler;
	}

	public static HighscoresHandler getHighscoresHandler() {
		return highscoresHandler;
	}

	public static EventHandler getEventHandler() {
		return events;
	}

	public static ConnectionPool getPunishConnections() {
		return punishPool;
	}

	public static DropManager getDropManager() {
		return dropManager;
	}

	public static Punishments getPunishments() {
		return PUNISHMENTS;
	}

	public static Motivote<Reward> getMotivote() {
		return MOTIVOTE;
	}

	public static String getStatus() {
		return "IO_THREAD\n" + "\tShutdown? " + IO_THREAD.isShutdown() + "\n" + "\tTerminated? " + IO_THREAD.isTerminated();
	}
}