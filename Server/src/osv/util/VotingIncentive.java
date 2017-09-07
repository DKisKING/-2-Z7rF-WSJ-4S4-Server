package osv.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import osv.Config;
import osv.model.items.GameItem;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.util.motivote.MotivoteHandler;
import osv.util.motivote.Reward;

public class VotingIncentive extends MotivoteHandler<Reward> {
	
	int voteCount = 0;

	@Override
	public void onCompletion(Reward reward) {
		Player player = PlayerHandler.getPlayer(reward.username());
		String rewardName = reward.rewardName();
		if (player == null || !player.initialized) {
			return;
		}
		if (player.getTutorial().isActive()) {
			return;
		}
		if (System.currentTimeMillis() - player.getLastIncentive() < TimeUnit.HOURS.toMillis(12) && !player.getRights().isOrInherits(Right.MODERATOR)) {
			if (!player.receivedIncentiveWarning()) {
				long duration = 12 - TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - player.getLastIncentive());
				player.sendMessage("<col=CC0000>You have a voting reward waiting to be claimed but you have already claimed a</col>");
				player.sendMessage("<col=CC0000>reward in the last 12 hours. You must wait approximately " + duration + " hours.</col>");
				player.updateIncentiveWarning();
			}
			return;
		}
		if (!player.getMode().isVotingPackageClaimable(reward.rewardName())) {
			player.sendMessage("Your game mode prohibits this reward from being claimable.");
			player.sendMessage("Your reward has been switched to 'Vote Ticket'.");
			rewardName = "Vote Ticket";
		}
		voteCount++;
		create(reward, rewardName, player);
		if (Config.DOUBLE_VOTE_INCENTIVES || player.getRights().isOrInherits(Right.MEGA_VIP)) {
			create(reward, rewardName, player);
		}
		player.setLastIncentive(System.currentTimeMillis());
		reward.complete();
	}
	
//	private void randomBoost(Player player) {
//		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
//			if (PlayerHandler.players[i] != null) {
//
//				int time = 100;
//
//				PlayerHandler.executeGlobalMessage("<img=10></img>Another <col=255>10</col> votes has been claimed.");
//
//				switch (Misc.random(3)) {
//				case 0:
//					PlayerHandler.executeGlobalMessage("<img=10></img>Which have enabled 15 minutes of <col=255>Bonus PC Points</col>.");
//					Config.BONUS_PC = true;
//					CycleEventHandler.getSingleton().addEvent(i, new CycleEvent() {
//						@Override
//						public void execute(CycleEventContainer container) {
//							PlayerHandler.executeGlobalMessage("<img=10></img>Ended..");
//							Config.BONUS_PC = false;
//							container.stop();
//						}
//
//						@Override
//						public void stop() {
//
//						}
//					}, time);
//					break;
//
//				case 1:
//					PlayerHandler.executeGlobalMessage("<img=10></img>Which have enabled 15 minutes of <col=255>Bonus Barrows Chance</col>.");
//					Config.BARROWS_RARE_CHANCE = 2;
//					CycleEventHandler.getSingleton().addEvent(i, new CycleEvent() {
//						@Override
//						public void execute(CycleEventContainer container) {
//							PlayerHandler.executeGlobalMessage("<img=10></img>Ended..");
//							Config.BARROWS_RARE_CHANCE = 1;
//							container.stop();
//						}
//
//						@Override
//						public void stop() {
//						}
//					}, time);
//					break;
//
//				case 2:
//					PlayerHandler.executeGlobalMessage("<img=10></img>Which have enabled 15 minutes of <col=255>Double PKP</col>.");
//					Config.DOUBLE_PKP = true;
//					CycleEventHandler.getSingleton().addEvent(i, new CycleEvent() {
//						@Override
//						public void execute(CycleEventContainer container) {
//							PlayerHandler.executeGlobalMessage("<img=10></img>Ended..");
//							Config.DOUBLE_PKP = false;
//							container.stop();
//						}
//
//						@Override
//						public void stop() {
//						}
//					}, time);
//					break;
//
//				case 3:
//					PlayerHandler.executeGlobalMessage("<img=10></img>Which have enabled 15 minutes of <col=255>Double Drops</col>.");
//					Config.DOUBLE_DROPS = true;
//					CycleEventHandler.getSingleton().addEvent(i, new CycleEvent() {
//						@Override
//						public void execute(CycleEventContainer container) {
//							PlayerHandler.executeGlobalMessage("<img=10></img>Ended..");
//							Config.DOUBLE_DROPS = false;
//							container.stop();
//						}
//
//						@Override
//						public void stop() {
//
//						}
//					}, time);
//					break;
//				}
//			}
//		}
//	}

	private void create(Reward reward, String rewardName, Player player) {
//		if (voteCount == 1) {
//			voteCount = 0;
//			randomBoost(player);
//		}
		
		switch (rewardName) {
		case "Vote Ticket":
			player.getItems().addItemUnderAnyCircumstance(1464, 2);
			// player.votePoints += 2;
			PlayerHandler.executeGlobalMessage("<img=10></img><col=255>" + Misc.capitalize(player.playerName) + " </col>has just voted and received <col=CC0000>2x Vote Tickets</col>.");
			break;
		case "Bonus Experience":
			player.bonusXpTime = 3000;
			PlayerHandler.executeGlobalMessage(
					"<img=10></img><col=255>" + Misc.capitalize(player.playerName) + " </col>has just voted and received <col=CC0000>Bonus Experience</col>.");
			break;
		case "Mysterious Emblem (Tier 2)":
			player.getItems().addItemUnderAnyCircumstance(12748, 1);
			PlayerHandler.executeGlobalMessage(
					"<img=10></img><col=255>" + Misc.capitalize(player.playerName) + " </col>has just voted and received a <col=CC0000>Mysterious Emblem (tier 2)</col>.");
			break;
		case "Coins":
			player.getItems().addItemUnderAnyCircumstance(995, 1_000_000);
			PlayerHandler
					.executeGlobalMessage("<img=10></img><col=255>" + Misc.capitalize(player.playerName) + " </col>has just voted and received <col=CC0000>1,000,000gp</col>.");
			break;
		case "Combat Runes":
			List<GameItem> runes = Arrays.asList(new GameItem(560, 500), new GameItem(9075, 1000), new GameItem(557, 2500), new GameItem(565, 500), new GameItem(560, 1000),
					new GameItem(555, 1500));
			if (player.getItems().freeSlots() > 5) {
				runes.forEach(item -> player.getItems().addItem(item.getId(), item.getAmount()));
			} else {
				runes.forEach(item -> player.getItems().addItemToBank(item.getId(), item.getAmount()));
			}
			PlayerHandler.executeGlobalMessage(
					"<img=10></img><col=255>" + Misc.capitalize(player.playerName) + " </col>has just voted and received <col=CC0000>Combat Runes (250 casts)</col>.");
			break;
		default:
			List<Player> staff = PlayerHandler.nonNullStream().filter(pl -> pl.getRights().isOrInherits(Right.MODERATOR)).collect(Collectors.toList());
			staff.forEach(pla -> {
				pla.sendMessage("WARNING: " + player.playerName + " MAY have just attempted to cheat the voting system.");
			});
			break;
		}
	}

}
