package osv.model.npcs;

import osv.clip.Region;
import osv.model.players.Player;

public class NPCDumbPathFinder {

	public static void follow(NPC npc, Player following) {
		walkTowards(npc, following.getX(), following.getY());
	}

	public static void walkTowards(NPC npc, int waypointx, int waypointy) {
		int x = npc.getX();
		int y = npc.getY();
		if (waypointx == x && waypointy == y) {
			return;
		}
		
		int direction = -1;
		final int xDifference = waypointx - x;
		final int yDifference = waypointy - y;

		int toX = 0;
		int toY = 0;

		if (xDifference > 0) {
			toX = 1;
		} else if (xDifference < 0) {
			toX = -1;
		}

		if (yDifference > 0) {
			toY = 1;
		} else if (yDifference < 0) {
			toY = -1;
		}

		int toDir = NPCClipping.getDirection(x, y, x + toX, y + toY);

		if (canMoveTo(npc, toDir)) {
			direction = toDir;
		} else {
			if (toDir == 0) {
				if (canMoveTo(npc, 3)) {
					direction = 3;
				} else if (canMoveTo(npc, 1)) {
					direction = 1;
				}
			} else if (toDir == 2) {
				if (canMoveTo(npc, 1)) {
					direction = 1;
				} else if (canMoveTo(npc, 4)) {
					direction = 4;
				}
			} else if (toDir == 5) {
				if (canMoveTo(npc, 3)) {
					direction = 3;
				} else if (canMoveTo(npc, 6)) {
					direction = 6;
				}
			} else if (toDir == 7) {
				if (canMoveTo(npc, 4)) {
					direction = 4;
				} else if (canMoveTo(npc, 6)) {
					direction = 6;
				}
			}
		}

		if (direction == -1) {
			return;
		}
		
		if (direction == -1) {
			return;
		}
		/*direction >>= 1;	
			
		if (direction < 0) {
			return;
		}*/
		//if (npc.moveX != 0 || npc.moveY != 0)
			//throw new IllegalStateException("walking multiple times.");
		/*try {
			throw new Exception();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		npc.moveX = NPCClipping.DIR[direction][0];
		npc.moveY = NPCClipping.DIR[direction][1];
		//npc.moveY = Server.npcHandler.GetMove(npc.getY(), npcLocation[1] + movey);
		npc.getNextNPCMovement();
		npc.updateRequired = true;
	}
	
	@SuppressWarnings("static-access")
	public static boolean canMoveTo(final NPC mob, final int direction) {
		if (direction == -1) {
			return false;
		}

		final int x = mob.getX();
		final int y = mob.getY();
		final int z = mob.heightLevel > 3 ? mob.heightLevel % 4 : mob.heightLevel;

		final int x5 = mob.getX() + NPCClipping.DIR[direction][0];
		final int y5 = mob.getY() + NPCClipping.DIR[direction][1];

		final int size = mob.getSize();

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < NPCClipping.SIZES[i].length; k++) {
				int x3 = x + NPCClipping.SIZES[i][k][0];
				int y3 = y + NPCClipping.SIZES[i][k][1];

				int x2 = x5 + NPCClipping.SIZES[i][k][0];
				int y2 = y5 + NPCClipping.SIZES[i][k][1];

				if (NPCClipping.withinBlock(x, y, size, x2, y2)) {
					continue;
				}

				Region region = Region.getRegion(x3, y3);
				if (region == null)
					return false;
				
				if (!Region.getRegion(x3, y3).canMove(x3, y3, z, direction)) {
					return false;
				}

				/*if (!virtual) {
					if (Region.getRegion(x2, y2).isNpcOnTile(x2, y2, z))
						return false;
				} else {
					if (region.isMobOnTile(x2, y2, z))
						return false;
				}*/

				for (int j = 0; j < 8; j++) {
					int x6 = x3 + NPCClipping.DIR[j][0];
					int y6 = y3 + NPCClipping.DIR[j][1];

					if (NPCClipping.withinBlock(x5, y5, size, x6, y6)) {
						if (!Region.getRegion(x3, y3).canMove(x3, y3, z, j)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	

}