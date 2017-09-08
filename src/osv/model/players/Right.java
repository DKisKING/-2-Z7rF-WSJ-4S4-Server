package osv.model.players;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import osv.util.Misc;

/**
 * The rights of a player determines their authority. Every right can be viewed with a name and a value. The value is used to separate each right from one another.
 * 
 * @author Jason MacK
 * @date January 22, 2015, 5:23:49 PM
 */

public enum Right implements Comparator<Right> {
	PLAYER(0, "000000"), 
	HELPER(11, "004080"), 
	MODERATOR(1, "919191", HELPER), 
	ADMINISTRATOR(2, "F5FF0F", MODERATOR), 
	GAME_DEVELOPER(16, "544FBB", ADMINISTRATOR), 
	OWNER(3, "F5FF0F", GAME_DEVELOPER), 
	UNKNOWN(4, "F5FF0F"), 
	CONTRIBUTOR(5, "B60818"), 
	SPONSOR(6, "063DCF", CONTRIBUTOR), 
	SUPPORTER(7, "118120", SPONSOR), 
	VIP(8, "9E00DE", SUPPORTER), 
	SUPER_VIP(9, "9E6405", VIP),
	MEGA_VIP(17, "9E6405", SUPER_VIP), 
	LEGENDARY(18, "9E6405", MEGA_VIP),
	RESPECTED_MEMBER(10, "272727"), 
	HITBOX(12, "437100"), 
	IRONMAN(13, "3A3A3A"), 
	ULTIMATE_IRONMAN(14, "717070"), 
	YOUTUBER(15, "FE0018"), 
	OSRS(23, "437100");

	/**
	 * The level of rights that define this
	 */
	private final int right;

	/**
	 * The rights inherited by this right
	 */
	private final List<Right> inherited;

	/**
	 * The color associated with the right
	 */
	private final String color;

	/**
	 * Creates a new right with a value to differentiate it between the others
	 * 
	 * @param right the right required
	 * @param color a color thats used to represent the players name when displayed
	 * @param inherited the right or rights inherited with this level of right
	 */
	private Right(int right, String color, Right... inherited) {
		this.right = right;
		this.inherited = Arrays.asList(inherited);
		this.color = color;
	}

	/**
	 * The rights of this enumeration
	 * 
	 * @return the rights
	 */
	public int getValue() {
		return right;
	}

	/**
	 * Returns a {@link Rights} object for the value.
	 * 
	 * @param value the right level
	 * @return the rights object
	 */
	public static Right get(int value) {
		return RIGHTS.stream().filter(element -> element.right == value).findFirst().orElse(PLAYER);
	}

	/**
	 * A {@link Set} of all {@link Rights} elements that cannot be directly modified.
	 */
	private static final Set<Right> RIGHTS = Collections.unmodifiableSet(EnumSet.allOf(Right.class));

	/**
	 * The color associated with the right
	 * 
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Determines if this level of rights inherited another level of rights
	 * 
	 * @param rights the level of rights we're looking to determine is inherited
	 * @return {@code true} if the rights are inherited, otherwise {@code false}
	 */
	public boolean isOrInherits(Right right) {
		return this == right || inherited.size() > 0 && inherited.stream().anyMatch(r -> r.isOrInherits(right));
	}

	/**
	 * An array of {@link Right} objects that represent the order in which some rights should be prioritized over others. The index at which a {@link Right} object exists
	 * determines it's priority. The lower the index the less priority that {@link Right} has over another. The list is ordered from lowest priority to highest priority.
	 * <p>
	 * An example of this would be comparing a {@link #MODERATOR} to a {@link #ADMINISTRATOR}. An {@link #ADMINISTRATOR} can be seen as more 'powerful' when compared to a
	 * {@link #MODERATOR} because they have more power within the community.
	 * </p>
	 */

	public static final Right[] PRIORITY = { PLAYER, OSRS, IRONMAN, ULTIMATE_IRONMAN, CONTRIBUTOR, SPONSOR, SUPPORTER, VIP, SUPER_VIP, MEGA_VIP, LEGENDARY, RESPECTED_MEMBER, HITBOX, YOUTUBER, HELPER,
			MODERATOR, ADMINISTRATOR, GAME_DEVELOPER, OWNER, UNKNOWN, };

	@Override
	public String toString() {
		return Misc.capitalizeJustFirst(name().replaceAll("_", " "));
	}

	@Override
	public int compare(Right arg0, Right arg1) {
		return 0;
	}

}
