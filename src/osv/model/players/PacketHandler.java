package osv.model.players;

import osv.Config;
import osv.model.players.packets.AttackPlayer;
import osv.model.players.packets.Bank10;
import osv.model.players.packets.Bank5;
import osv.model.players.packets.BankAll;
import osv.model.players.packets.BankAllButOne;
import osv.model.players.packets.BankModifiableX;
import osv.model.players.packets.BankX1;
import osv.model.players.packets.BankX2;
import osv.model.players.packets.ChallengePlayer;
import osv.model.players.packets.ChangeAppearance;
import osv.model.players.packets.ChangeRegions;
import osv.model.players.packets.Chat;
import osv.model.players.packets.ClickNPC;
import osv.model.players.packets.ClickObject;
import osv.model.players.packets.ClickingButtons;
import osv.model.players.packets.ClickingInGame;
import osv.model.players.packets.ClickingStuff;
import osv.model.players.packets.Commands;
import osv.model.players.packets.Dialogue;
import osv.model.players.packets.DropItem;
import osv.model.players.packets.FollowPlayer;
import osv.model.players.packets.IdleLogout;
import osv.model.players.packets.InputField;
import osv.model.players.packets.ItemOnItem;
import osv.model.players.packets.ItemOnNpc;
import osv.model.players.packets.ItemOnObject;
import osv.model.players.packets.ItemOnPlayer;
import osv.model.players.packets.ItemOptionOneGroundItem;
import osv.model.players.packets.ItemOptionTwoGroundItem;
import osv.model.players.packets.MagicOnFloorItems;
import osv.model.players.packets.MagicOnItems;
import osv.model.players.packets.MagicOnObject;
import osv.model.players.packets.MapRegionChange;
import osv.model.players.packets.MapRegionFinish;
import osv.model.players.packets.Moderate;
import osv.model.players.packets.MoveItems;
import osv.model.players.packets.OperateItem;
import osv.model.players.packets.PickupItem;
import osv.model.players.packets.PrivateMessaging;
import osv.model.players.packets.RemoveItem;
import osv.model.players.packets.Report;
import osv.model.players.packets.SelectItemOnInterface;
import osv.model.players.packets.SilentPacket;
import osv.model.players.packets.Trade;
import osv.model.players.packets.Walking;
import osv.model.players.packets.WearItem;
import osv.model.players.packets.action.InterfaceAction;
import osv.model.players.packets.action.JoinChat;
import osv.model.players.packets.action.ReceiveString;
import osv.model.players.packets.itemoptions.ItemOptionOne;
import osv.model.players.packets.itemoptions.ItemOptionTwo;
import osv.model.players.packets.itemoptions.ItemOptionThree;

public class PacketHandler {

	private static PacketType packetId[] = new PacketType[256];

	static {
		SilentPacket u = new SilentPacket();
		packetId[3] = u;
		packetId[202] = u;
		packetId[77] = u;
		packetId[86] = u;
		packetId[78] = u;
		packetId[36] = u;
		packetId[226] = u;
		packetId[246] = u;
		packetId[148] = u;
		packetId[183] = u;
		packetId[230] = u;
		packetId[136] = u;
		packetId[189] = u;
		packetId[152] = u;
		packetId[200] = u;
		packetId[85] = u;
		packetId[165] = u;
		packetId[238] = u;
		packetId[150] = u;
		packetId[74] = u;
		packetId[234] = u;
		packetId[34] = u;
		packetId[68] = u;
		packetId[79] = u;
		packetId[140] = u;
		// packetId[18] = u;
		packetId[223] = u;
		packetId[8] = new Moderate();
		packetId[142] = new InputField();
		packetId[253] = new ItemOptionTwoGroundItem();
		packetId[218] = new Report();
		packetId[40] = new Dialogue();
		packetId[232] = new OperateItem();
//		KeyEventPacketHandler ke = new KeyEventPacketHandler();
//		packetId[186] = ke;
		ClickObject co = new ClickObject();
		packetId[132] = co;
		packetId[252] = co;
		packetId[70] = co;
		packetId[228] = co;
		packetId[57] = new ItemOnNpc();
		ClickNPC cn = new ClickNPC();
		packetId[72] = cn;
		packetId[131] = cn;
		packetId[155] = cn;
		packetId[17] = cn;
		packetId[21] = cn;
		packetId[18] = cn;
		packetId[124] = new SelectItemOnInterface();
		packetId[16] = new ItemOptionTwo();
		packetId[75] = new ItemOptionThree();
		packetId[122] = new ItemOptionOne();
		packetId[241] = new ClickingInGame();
		packetId[4] = new Chat();
		packetId[236] = new PickupItem();
		packetId[87] = new DropItem();
		packetId[185] = new ClickingButtons();
		packetId[130] = new ClickingStuff();
		packetId[103] = new Commands();
		packetId[214] = new MoveItems();
		packetId[237] = new MagicOnItems();
		packetId[181] = new MagicOnFloorItems();
		packetId[202] = new IdleLogout();
		AttackPlayer ap = new AttackPlayer();
		packetId[73] = ap;
		packetId[249] = ap;
		packetId[128] = new ChallengePlayer();
		packetId[39] = new Trade();
		packetId[139] = new FollowPlayer();
		packetId[41] = new WearItem();
		packetId[145] = new RemoveItem();
		packetId[117] = new Bank5();
		packetId[43] = new Bank10();
		packetId[129] = new BankAll();
		packetId[140] = new BankAllButOne();
		packetId[141] = new BankModifiableX();
		packetId[101] = new ChangeAppearance();
		PrivateMessaging pm = new PrivateMessaging();
		packetId[188] = pm;
		packetId[126] = pm;
		packetId[215] = pm;
		packetId[74] = pm;
		packetId[95] = pm;
		packetId[133] = pm;
		packetId[135] = new BankX1();
		packetId[208] = new BankX2();
		Walking w = new Walking();
		packetId[98] = w;
		packetId[164] = w;
		packetId[248] = w;
		packetId[53] = new ItemOnItem();
		packetId[192] = new ItemOnObject();
		packetId[25] = new ItemOptionOneGroundItem();
		@SuppressWarnings("unused")
		ChangeRegions cr = new ChangeRegions();
		packetId[60] = new JoinChat();
		packetId[127] = new ReceiveString();
		packetId[213] = new InterfaceAction();
		packetId[14] = new ItemOnPlayer();
		packetId[121] = new MapRegionFinish();
		packetId[210] = new MapRegionChange();
		packetId[35] = new MagicOnObject();
	}

	public static void processPacket(Player c, int packetType, int packetSize) {
		PacketType p = packetId[packetType];
		if (p != null && packetType > 0 && packetType < 257 && packetType == c.packetType && packetSize == c.packetSize) {
			if (Config.sendServerPackets && c.getRights().isOrInherits(Right.OWNER)) {
				c.sendMessage("PacketType: " + packetType + ". PacketSize: " + packetSize + ".");
			}
			try {
				p.processPacket(c, packetType, packetSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			c.disconnected = true;
			System.out.println(c.playerName + " is sending invalid PacketType: " + packetType + ". PacketSize: " + packetSize);
		}
	}
}