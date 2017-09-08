package osv.model.npcs.bosses.raids;

import osv.model.players.Boundary;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;

public class SkeletalMystic {
	
	public static boolean needRespawn = false;
	public static int respawnTimer = 0;
	public static int deathCount = 0;
	
	public static void rewardPlayers(Player player) {
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.SKELETAL_MYSTICS))
		.forEach(p -> {
			if (deathCount == 4) {
				p.sendMessage("@dre@All the mystics have been killed! Points have been dealt out!");
				p.sendMessage("@dre@You dealt " + p.getSkeletalMysticDamageCounter() + " damage towards the mystics. Granting " + p.getSkeletalMysticDamageCounter() + " points.");
				p.raidPoints += p.getSkeletalMysticDamageCounter();
				p.sendMessage("@dre@You now have a total of " + p.raidPoints + " Raid Points.");
				p.setSkeletalMysticDamageCounter(0);
			} else {
				p.sendMessage("@dre@" + deathCount + "/4 Skeletal Mystics have been killed so far.");
			}
		});
		
		if (deathCount == 4) {
			deathCount = 0;
			respawnTimer = 20;
			needRespawn = true;
		}
	}

}
