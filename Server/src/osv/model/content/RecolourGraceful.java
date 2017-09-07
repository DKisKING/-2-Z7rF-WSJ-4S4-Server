package osv.model.content;

import osv.model.content.achievement_diary.falador.FaladorDiaryEntry;
import osv.model.players.Player;

public enum RecolourGraceful {

	/**
	 * Graceful Regular, purple, navy blue, gold, red, green
	 */
	GRACEFUL_HOOD(11850, 13579, 13591, 13603, 13615, 13627, 13667), 
	GRACEFUL_CAPE(11852, 13581, 13593, 13605, 13617, 13629, 13669), 
	GRACEFUL_TOP(11854, 13583, 13595, 13607, 13619, 13631, 13671), 
	GRACEFUL_LEGS(11856, 13585, 13597, 13609, 13621, 13633, 13673), 
	GRACEFUL_GLOVES(11858, 13587, 13599, 13611, 13623, 13635, 13675), 
	GRACEFUL_BOOTS(11860, 13589, 13601, 13613, 13625, 13637, 13677);

	private int REGULAR, PURPLE, BLUE, GOLD, RED, GREEN, WHITE;
	public static int ITEM_TO_RECOLOUR = 0;
	public static String COLOR = "";

	public int getRegular() {
		return REGULAR;
	}

	public int getPurple() {
		return PURPLE;
	}

	public int getBlue() {
		return BLUE;
	}

	public int getGold() {
		return GOLD;
	}

	public int getRed() {
		return RED;
	}

	public int getGreen() {
		return GREEN;
	}

	public int getWhite() {
		return WHITE;
	}

	RecolourGraceful(int REGULAR, int PURPLE, int BLUE, int GOLD, int RED, int GREEN, int WHITE) {
		this.REGULAR = REGULAR;
		this.PURPLE = PURPLE;
		this.BLUE = BLUE;
		this.GOLD = GOLD;
		this.RED = RED;
		this.GREEN = GREEN;
		this.WHITE = WHITE;
	}

	public static void recolor(final Player player, int item) {
		for (RecolourGraceful recolor : RecolourGraceful.values()) {
			String name = recolor.name().toLowerCase().replaceAll("_", " ");
			if (recolor.getRegular() == item) {
				if (player.getItems().playerHasItem(item)) {
					player.getItems().deleteItem(12792, 1);
					player.getItems().deleteItem(recolor.getRegular(), 1);
					player.getItems().addItem(
							COLOR == "PURPLE" ? recolor.getPurple() : 
							COLOR == "BLUE" ? recolor.getBlue() : 
							COLOR == "GOLD" ? recolor.getGold() : 
							COLOR == "RED" ? recolor.getRed() : 
							COLOR == "GREEN" ? recolor.getGreen() : 
							COLOR == "WHITE" ? recolor.getWhite() : 
							recolor.getRegular(), 1);
					player.sendMessage("You recoloured your " + name + ".");
					player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.RECOLOR_GRACEFUL);
					ITEM_TO_RECOLOUR = 0;
					COLOR = "";
					player.getPA().closeAllWindows();
				}
			}
		}
	}


}
