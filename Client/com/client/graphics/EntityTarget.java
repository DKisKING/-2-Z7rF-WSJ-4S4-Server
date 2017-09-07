package com.client.graphics;

import com.client.Client;
import com.client.DrawingArea;
import com.client.Entity;
import com.client.NPC;
import com.client.Player;
import com.client.RSFont;

public class EntityTarget {
	
	private byte state;
	
	private RSFont rsFont;
	
	private final Client client = Client.getInstance();
	
	private Entity target;
	
	public EntityTarget(byte state, short entityIndex, short currentHealth, short maximumHealth, RSFont rsFont) {
		this.state = state;
		this.rsFont = rsFont;
		if (state > 0 && state < 3) {
			if (state == 1) {
				target = client.npcArray[entityIndex];
			} else if (state == 2) {
				target = client.playerArray[entityIndex];
			}
			if (target != null) {
				target.currentHealth = currentHealth;
				target.maxHealth = maximumHealth;
			}
		}
	}
	
	public void draw() {
		if(state < 1 || state > 2 || target == null) {
			return;
		}
		String name = "";
		
		if(state == 1) {
			name = ((NPC) target).desc.name;
		} else if(state == 2) {
			name = ((Player) target).name;
		} else {
			state = 0;
			return;
		}
		int drawingWidth = 130;
		DrawingArea.drawAlphaBox(5, 20, drawingWidth, 40, 0x000000, 80);
		rsFont.drawCenteredString(name, 70, 34, 0xFFFFFF, 0x000000);
		int barWidth = 124;
		int fillPercentage = target.currentHealth * barWidth / target.maxHealth;
		DrawingArea.drawAlphaBox(9, 44, barWidth, 10, 0x8F0000, 80);
		DrawingArea.drawAlphaBox(9, 44, fillPercentage, 10, 0x005F00, 80);
		rsFont.drawCenteredString(target.currentHealth + "/" + target.maxHealth, 70, 54, 0xFFFFFF, 0x000000);
	}
	
	public void stop() {
		state = 0;
	}

}
