package osv.model.players.packets;

import java.util.Objects;

import osv.Server;
import osv.clip.Region;
import osv.event.CycleEvent;
import osv.event.CycleEventContainer;
import osv.event.CycleEventHandler;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.multiplayer_session.MultiplayerSessionStage;
import osv.model.multiplayer_session.MultiplayerSessionType;
import osv.model.multiplayer_session.duel.DuelSession;
import osv.model.players.Boundary;
import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.model.players.Right;
import osv.model.players.skills.farming.FarmingConstants;
import osv.model.players.skills.hunter.impling.PuroPuro;
import osv.util.Misc;

/**
 * Click Object
 */
public class ClickObject implements PacketType {

	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70, FOURTH_CLICK = 228;

	@Override
	public void processPacket(final Player c, int packetType, int packetSize) {
		c.clickObjectType = c.objectX = c.objectId = c.objectY = 0;
		c.objectYOffset = c.objectXOffset = 0;
		c.getPA().resetFollow();
		c.getCombat().resetPlayerAttack();
		if (c.isForceMovementActive()) {
			return;
		}
		switch (packetType) {
		case FIRST_CLICK:
			c.objectX = c.getInStream().readSignedWordBigEndianA();
			c.objectId = c.getInStream().readUnsignedWord();
			c.objectY = c.getInStream().readUnsignedWordA();
			c.objectDistance = 1;	

			if (c.isForceMovementActive()) {
				return ;
			}
			if (c.viewingLootBag || c.addingItemsToLootBag || c.viewingRunePouch) {
				return;
			}
			if (c.objectId == 11834 && Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
				c.getFightCave().leaveGame();
				return;
			}
			
			if (!Region.isWorldObject(c.objectId, c.objectX, c.objectY, c.heightLevel) && !Boundary.isIn(c, Boundary.ICE_DEMON) && !Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(c, Boundary.LIGHTHOUSE)
					&& !Boundary.isIn(c, Boundary.RFD)) {
				c.sendMessage("Warning: The object could not be verified by the server. If you feel this is");
				c.sendMessage("incorrect, please contact a staff member to have this resolved.");
				if (c.getRights().isOrInherits(Right.OWNER)) {
					c.sendMessage("Object Option One: " + c.objectId + ", " + c.objectX + ", " + c.objectY + ", " + c.heightLevel);
				}
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
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}
			if (c.objectId == 9357) {
				if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
					c.getFightCave().leaveGame();
				}
				return;
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}

			if (c.getRights().isOrInherits(Right.OWNER)) {
				c.sendMessage("Object Option One: " + c.objectId + " objectX: " + c.objectX + " objectY: " + c.objectY);
			}
			if (Math.abs(c.getX() - c.objectX) > 25 || Math.abs(c.getY() - c.objectY) > 25) {
				c.resetWalkingQueue();
				break;
			}
			switch (c.objectId) {

			case 25016:
			case 25017:
			case 25018:
			case 25029:
				PuroPuro.magicalWheat(c);
				break;
			
			case 9398:// deposit
				c.getPA().sendFrame126("The Bank of Tyras - Deposit Box", 7421);
				c.getPA().sendFrame248(4465, 197);// 197 just because you can't
													// see it =\
				c.getItems().resetItems(7423);
				break;

			case 1738:
				c.getPA().movePlayer(2840, 3539, 2);
				break;

			case 1733:
			case 10779:
				c.objectYOffset = 2;
				break;

			case 11756:
			case 4551:
			case 4553:
			case 4555:
			case 4557:
			case 23556:
			case 677:
			case 21578:
			case 6461:
			case 6462:
			case 26645:
			case 26646:
			case 26762:
			case 26763:
			case 11374:	
			case 26567:
			case 26568:
			case 26569:
			case 11406:
			case 11430:
			case 26461:
			case 26766:
			case 14911:
			case 1759:
			case 1761:
			case 1753:
			case 14918:
			case 1754:
			case 11819:
			case 11826:
			case 1751:
			case 7471: 
			case 11377:
			case 11375:
			case 11376:	
			case 10817:
			case 10595:
			case 10596:
			case 29082:
			case 7811:
				c.objectDistance = 3;
				break;

			case 23131:
			case 26366: // Godwars altars
			case 26365:
			case 26364:
			case 26363:
			case 678:
			case 16466:
			case 23569:
			case 23568:
			case 23132:
				c.objectDistance = 5;
				break;

			case 3044:
			case 2030:
			case 21764:
			case 17010:
			case 2561:
			case 2562:
			case 2563:
			case 2564:
			case 2565:
			case FarmingConstants.GRASS_OBJECT:
			case FarmingConstants.HERB_OBJECT:
			case FarmingConstants.HERB_PATCH_DEPLETED:
			case 16671:
			case 17068:
			case 27057:
				c.objectDistance = 6;
				break;
			case 5094:
			case 5096:
			case 5097:
			case 5098:
			case 14912:
			case 16511:
				c.objectDistance = 7;
				break;
			case 26562:		
				c.objectDistance = 2;
				break;
			case 26503:
				c.objectDistance = 1;
				break;
			case 26518:
				c.objectDistance = 1;
				break;
			case 26380:
				c.objectDistance = 9;
				break;
			case 26502:
				c.objectDistance = 1;
				break;
				
			case 20720:
			case 20721:
			case 20722:
			case 20770:
			case 20771:
			case 20772:
			case 10777:
			case 10778:		
			case 27255:
				c.objectDistance = 3;
				break;

			case 7674:
				c.objectDistance = 2;
				break;

			case 11758:
			case 11764:
			case 11762:
			case 11759:
			case 23271:
			case 1756:
			case 26724:
				c.objectDistance = 5;
				break;

			case 2491:
				c.objectDistance = 10;
				break;

			case 245:
				c.objectYOffset = -1;
				c.objectDistance = 0;
				break;

			case 272:
				c.objectYOffset = 1;
				c.objectDistance = 0;
				break;

			case 273:
				c.objectYOffset = 1;
				c.objectDistance = 0;
				break;

			case 246:
				c.objectYOffset = 1;
				c.objectDistance = 0;
				break;

			case 4493:
			case 4494:
			case 4496:
			case 4495:
				c.objectDistance = 5;
				break;

			case 20667: // Barrows tomb staircases
			case 20668:
			case 20669:
			case 20670:
				c.objectDistance = 1;
				break;
			case 20671:
			case 20672:
				c.objectDistance = 3;
				break;

			case 10229:
			case 6522:
			case 11734:
			case 11833:
			case 11834:
				c.objectDistance = 7;
				break;
			case 8959:
				c.objectYOffset = 1;
				break;
			case 4417:
				if (c.objectX == 2425 && c.objectY == 3074)
					c.objectYOffset = 2;
				break;
			case 4420:
				if (c.getX() >= 2383 && c.getX() <= 2385) {
					c.objectYOffset = 1;
				} else {
					c.objectYOffset = -2;
				}
				break;
			case 6552:
			case 409:
				c.objectDistance = 2;
				break;

			case 2114:
			case 2118:
			case 2119:
			case 2120:
			case 16509:
			case 21725:
			case 21727:
			case 23104:
				c.objectDistance = 4;
				break;

			case 2879:
			case 2878:
				c.objectDistance = 3;
				break;
			case 2558:
				c.objectDistance = 0;
				if (c.absX > c.objectX && c.objectX == 3044)
					c.objectXOffset = 1;
				if (c.absY > c.objectY)
					c.objectYOffset = 1;
				if (c.absX < c.objectX && c.objectX == 3038)
					c.objectXOffset = -1;
				break;
			case 9356:
				c.objectDistance = 2;
				break;
			case 5959:
			case 1815:
			case 5960:
			case 1816:
				c.objectDistance = 0;
				break;

			case 9293:
				c.objectDistance = 2;
				break;
			case 4418:
				if (c.objectX == 2374 && c.objectY == 3131)
					c.objectYOffset = -2;
				else if (c.objectX == 2369 && c.objectY == 3126)
					c.objectXOffset = 2;
				else if (c.objectX == 2380 && c.objectY == 3127)
					c.objectYOffset = 2;
				else if (c.objectX == 2369 && c.objectY == 3126)
					c.objectXOffset = 2;
				else if (c.objectX == 2374 && c.objectY == 3131)
					c.objectYOffset = -2;
				break;
			case 9706:
				c.objectDistance = 0;
				c.objectXOffset = 1;
				break;
			case 9707:
				c.objectDistance = 0;
				c.objectYOffset = -1;
				break;
			case 4419:
			case 6707: // verac
				c.objectYOffset = 3;
				break;
			case 6823:
				c.objectDistance = 2;
				c.objectYOffset = 1;
				break;

			case 6706: // torag
				c.objectXOffset = 2;
				break;
			case 6772:
				c.objectDistance = 2;
				c.objectYOffset = 1;
				break;

			case 6705: // karils
				c.objectYOffset = -1;
				break;
			case 6822:
				c.objectDistance = 2;
				c.objectYOffset = 1;
				break;

			case 6704: // guthan stairs
				c.objectYOffset = -1;
				break;
			case 6773:
				c.objectDistance = 2;
				c.objectXOffset = 1;
				c.objectYOffset = 1;
				break;

			case 6703: // dharok stairs
				c.objectXOffset = -1;
				break;
			case 6771:
				c.objectDistance = 2;
				c.objectXOffset = 1;
				c.objectYOffset = 1;
				break;

			case 6702: // ahrim stairs
				c.objectXOffset = -1;
				break;
			case 6821:
				c.objectDistance = 2;
				c.objectXOffset = 1;
				c.objectYOffset = 1;
				break;
			case 3192:
				c.objectDistance = 7;
				break;
			case 1276:
			case 1278:// trees
			case 1281: // oak
			case 1308: // willow
			case 1307: // maple
			case 1309: // yew
			case 1306: // yew
				c.objectDistance = 3;
				break;
			default:
				c.objectDistance = 1;
				c.objectXOffset = 0;
				c.objectYOffset = 0;
				break;
			}
			if (c.goodDistance(c.objectX + c.objectXOffset, c.objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
				c.getActions().firstClickObject(c.objectId, c.objectX, c.objectY);
			} else {
				c.clickObjectType = 1;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.clickObjectType == 1 && c.goodDistance(c.objectX + c.objectXOffset, c.objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
							c.getActions().firstClickObject(c.objectId, c.objectX, c.objectY);
							container.stop();
						}
						if (c.clickObjectType > 1 || c.clickObjectType == 0) {
							container.stop();
						}
					}

					@Override
					public void stop() {
						c.clickObjectType = 0;
					}
				}, 1);
			}
			break;

		case SECOND_CLICK:
			c.objectId = c.getInStream().readUnsignedWordBigEndianA();
			c.objectY = c.getInStream().readSignedWordBigEndian();
			c.objectX = c.getInStream().readUnsignedWordA();
			c.objectDistance = 1;
			if (!Region.isWorldObject(c.objectId, c.objectX, c.objectY, c.heightLevel)) {
				c.sendMessage("Warning: The object could not be verified by the server. If you feel this is");
				c.sendMessage("incorrect, please contact a staff member to have this resolved.");
				return;
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.getRights().isOrInherits(Right.OWNER)) {
				Misc.println("Object Option Two: " + c.objectId + "  ObjectX: " + c.objectX + "  objectY: " + c.objectY + " Xoff: " + (c.getX() - c.objectX) + " Yoff: "
						+ (c.getY() - c.objectY));
			}

			switch (c.objectId) {
			case 2561:
			case 2562:
			case 2563:
			case 2564:
			case 2565:
			case 2478:
			case 2483:
			case 2484:
			case 11734:
			case 11731:
			case 23104:
			case 24009:
			case 14011:
			case 635:
			case 7811:
				c.objectDistance = 3;
				break;
			case 6163:
			case 6165:
			case 6166:
			case 6164:
			case 6162:
				c.objectDistance = 2;
				break;
			case 3192:
				c.objectDistance = 7;
				break;

			default:
				c.objectDistance = 1;
				c.objectXOffset = 0;
				c.objectYOffset = 0;
				break;

			}
			if (c.goodDistance(c.objectX + c.objectXOffset, c.objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
				c.getActions().secondClickObject(c.objectId, c.objectX, c.objectY);
			} else {
				c.clickObjectType = 2;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.clickObjectType == 2 && c.goodDistance(c.objectX + c.objectXOffset, c.objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
							c.getActions().secondClickObject(c.objectId, c.objectX, c.objectY);
							container.stop();
						}
						if (c.clickObjectType < 2 || c.clickObjectType > 2)
							container.stop();
					}

					@Override
					public void stop() {
						c.clickObjectType = 0;
					}
				}, 1);
			}
			break;

		case THIRD_CLICK:
			c.objectX = c.getInStream().readSignedWordBigEndian();
			c.objectY = c.getInStream().readUnsignedWord();
			c.objectId = c.getInStream().readUnsignedWordBigEndianA();
			if (!Region.isWorldObject(c.objectId, c.objectX, c.objectY, c.heightLevel)) {
				c.sendMessage("Warning: The object could not be verified by the server. If you feel this is");
				c.sendMessage("incorrect, please contact a staff member to have this resolved.");
				return;
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.getRights().isOrInherits(Right.OWNER)) {
				Misc.println("Object Option Three: " + c.objectId + "  ObjectX: " + c.objectX + "  objectY: " + c.objectY + " Xoff: " + (c.getX() - c.objectX) + " Yoff: "
						+ (c.getY() - c.objectY));
			}

			if (c.objectId == 12309) {
				c.getShops().openShop(14);
			}

			switch (c.objectId) {

			case 7811:
				c.objectDistance = 3;
				break;

			default:

				c.objectDistance = 1;
				c.objectXOffset = 0;
				c.objectYOffset = 0;
				break;
			}
			if (c.goodDistance(c.objectX + c.objectXOffset, c.objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
				c.getActions().thirdClickObject(c.objectId, c.objectX, c.objectY);
			} else {
				c.clickObjectType = 3;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.clickObjectType == 3 && c.goodDistance(c.objectX + c.objectXOffset, c.objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
							c.getActions().thirdClickObject(c.objectId, c.objectX, c.objectY);
							container.stop();
						}
						if (c.clickObjectType < 3)
							container.stop();
					}

					@Override
					public void stop() {
						c.clickObjectType = 0;
					}
				}, 1);
			}
			break;
			
		case FOURTH_CLICK:
			c.objectX = c.getInStream().readSignedWordBigEndian();
			c.objectY = c.getInStream().readUnsignedWord();
			c.objectId = c.getInStream().readUnsignedWordBigEndianA();
			if (!Region.isWorldObject(c.objectId, c.objectX, c.objectY, c.heightLevel)) {
				c.sendMessage("Warning: The object could not be verified by the server. If you feel this is");
				c.sendMessage("incorrect, please contact a staff member to have this resolved.");
				return;
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.getRights().isOrInherits(Right.OWNER)) {
				Misc.println("Object Option Four: " + c.objectId + "  ObjectX: " + c.objectX + "  objectY: " + c.objectY + " Xoff: " + (c.getX() - c.objectX) + " Yoff: " + (c.getY() - c.objectY));
			}
			switch (c.objectId) {

			default:

				c.objectDistance = 1;
				c.objectXOffset = 0;
				c.objectYOffset = 0;
				break;
			}
			if (c.goodDistance(c.objectX + c.objectXOffset, c.objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
				c.getActions().thirdClickObject(c.objectId, c.objectX, c.objectY);
			} else {
				c.clickObjectType = 4;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.clickObjectType == 4 && c.goodDistance(c.objectX + c.objectXOffset, c.objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
							c.getActions().fourthClickObject(c.objectId, c.objectX, c.objectY);
							container.stop();
						}
						if (c.clickObjectType < 4)
							container.stop();
					}

					@Override
					public void stop() {
						c.clickObjectType = 0;
					}
				}, 1);
			}
			break;
		}

	}

	public void handleSpecialCase(Player c, int id, int x, int y) {

	}

}
