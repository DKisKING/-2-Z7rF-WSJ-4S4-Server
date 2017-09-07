package osv.model.players.skills.fletching;

import osv.Config;
import osv.event.Event;
import osv.model.players.Player;
import osv.model.players.mode.ModeType;
import osv.model.players.skills.Skill;

public class StringCrossbowEvent extends Event<Player> {

	private FletchableCrossbow crossbow;

	public StringCrossbowEvent(FletchableCrossbow b, Player attachment, int ticks) {
		super("skilling", attachment, ticks);
		this.crossbow = b;
	}

	@Override
	public void execute() {
		if (attachment == null || attachment.disconnected || attachment.getSession() == null) {
			stop();
			return;
		}
		if (!attachment.getItems().playerHasItem(crossbow.getItem()) || !attachment.getItems().playerHasItem(9438)) {
			stop();
			return;
		}
		attachment.startAnimation(crossbow.getAnimation());
		attachment.getItems().deleteItem2(crossbow.getItem(), 1);
		attachment.getItems().deleteItem2(9438, 1);
		attachment.getItems().addItem(crossbow.getProduct(), 1);
		attachment.getPA().addSkillXP((int) crossbow.getExperience() * (attachment.getMode().getType().equals(ModeType.OSRS) ? 1 : Config.FLETCHING_EXPERIENCE), Skill.FLETCHING.getId(), true);
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment == null || attachment.disconnected || attachment.getSession() == null) {
			return;
		}
		attachment.stopAnimation();
	}

}
