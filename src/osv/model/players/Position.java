package osv.model.players;

public final class Position {

	private final int x;
	
	private final int y;
	
	private final int height;
	
	public Position(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
	}
	
	public Position(int x, int y) {
		this(x, y, 0);
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	
}
