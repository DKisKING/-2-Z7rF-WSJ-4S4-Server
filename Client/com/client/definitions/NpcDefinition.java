package com.client.definitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//
//import org.apache.commons.io.FileUtils;

import com.client.Class36;
import com.client.Client;
import com.client.MRUNodes;
import com.client.Model;
import com.client.Stream;
import com.client.StreamLoader;

public final class NpcDefinition {

	public static NpcDefinition forID(int i) {
		for (int j = 0; j < 20; j++)
			if (cache[j].interfaceType == i)
				return cache[j];

		anInt56 = (anInt56 + 1) % 20;
		NpcDefinition entityDef = cache[anInt56] = new NpcDefinition();
		stream.currentOffset = streamIndices[i];
		entityDef.interfaceType = i;
		entityDef.readValues(stream);
		switch (i) {
		
		case 6462:
		case 6465:
		case 4895:
		case 4893:
			entityDef.onMinimap = false;
			break;
			
		case 7439:
			entityDef.actions = new String[] { null, null, null, null, null };
			break;
			
		case 7597:
			entityDef.actions = new String[] { "Talk-to", null, "Trade", null, null };
			break;
			
		case 3936:
			entityDef.actions = new String[] { "Talk-to", null, "Jatizso", "Neitiznot", null };
			break;	
			
		case 6773:
			entityDef.actions = new String[] { "Talk-to", null, "View safe-deposit box", "Get skull", null };
			break;
			
		case 6774:
			entityDef.actions = new String[] { "Talk-to", null, "Trade", null, null };
			break;
			
		case 2040:
			entityDef.actions = new String[] { "Talk-to", null, "Teleport", null, null };
			break;
			
		case 17:
			entityDef.actions = new String[] { "Pollnivneach", null, "Bedabin Camp", "Nardah", "Sophanem" };
			break;
			
		case 1045:
			entityDef.models = new int[] { 235, 189, 28983, 28979, 4218, 150, 4924, 4925, 4926 };
			entityDef.name = "Santa";
			entityDef.onMinimap = false;
			entityDef.actions = new String[] { "Talk-to", null, "Travel", null, null };
			entityDef.anInt86 = 200;
			entityDef.anInt91 = 200;
			break;
			
		case 1047:
			entityDef.name = "Santa Jr";
			//entityDef.models[0] = 29671;
			entityDef.models = new int[] { 235, 189, 28983, 28979, 4218, 150, 4924, 4925, 4926 };
			entityDef.actions = new String[] { "Pick-up", null, "Talk-to", null, null };
			entityDef.onMinimap = false;
			entityDef.anInt86 = 90;
			entityDef.anInt91 = 90;
			break;
			
		case 1046:
			entityDef.name = "Anti-Santa";
			entityDef.models = new int[] { 235, 28975, 28984, 28980, 4218, 150, 4924, 4925, 4926 };
			entityDef.actions = new String[] { null, "Attack", null, null, null };
			entityDef.combatLevel = 1225;
			entityDef.onMinimap = false;
			entityDef.anInt86 = 200;
			entityDef.anInt91 = 200;
			break;
			
			
		case 1048:
			NpcDefinition antiSantas = forID(1046);
			entityDef.standAnim = antiSantas.standAnim;
			entityDef.walkAnim = antiSantas.walkAnim;
			entityDef.name = "Anti-Santa Jr";
			entityDef.models = new int[] { 235, 28975, 28984, 28980, 4218, 150, 4924, 4925, 4926 };
			entityDef.actions = new String[] { "Pick-up", null, "Talk-to", null, null };
			entityDef.anInt86 = 90;
			entityDef.anInt91 = 90;
			break;
			
		case 1049:
			entityDef.name = "Anti-Santa Minion";
			entityDef.models = new int[] { 235, 28975, 28984, 28980, 4218, 150, 4924, 4925, 4926 };
			entityDef.actions = new String[] { null, "Attack", null, null, null };
			entityDef.combatLevel = 80;
			entityDef.onMinimap = false;
			entityDef.anInt86 = 120;
			entityDef.anInt91 = 120;
			break;

		case 6390:
			entityDef.name = "Grim Reaper";
			entityDef.models = new int[] { 29188, 29187 };
			entityDef.boundDim = 2;
			entityDef.standAnim = entityDef.walkAnim = 7154;
			entityDef.actions = new String[] { "Talk-to", null, null, null, null };
			entityDef.dialogueModels = new int[] { 29183 };
			entityDef.combatLevel = 0;
			entityDef.aBoolean93 = true;
			entityDef.getDegreesToTurn = 0;
			entityDef.onMinimap = false;
			entityDef.walkAnim = -1;
			entityDef.standAnim = -1;
			break;
			
		case 3257:
			entityDef.actions = new String[] { "Pickpocket", null, "Trade", null, null };
			break;

		case 2580:
			entityDef.actions = new String[] { "Talk-to", null, "Teleport to Abyss", "Teleport to Essence Mine", null };
			break;

		case 5057:
			entityDef.anInt91 = 130;
			entityDef.anInt86 = 130;
			entityDef.boundDim = 3;
			entityDef.onMinimap = false;
			entityDef.aBoolean84 = false;
			break;

		case 6611:
			NpcDefinition skeleton = forID(83);
			entityDef.standAnim = skeleton.standAnim;
			entityDef.walkAnim = skeleton.walkAnim;
			break;

		case 2399:
		case 2400:
		case 2401:
		case 2402:
			entityDef.actions = new String[5];
			entityDef.actions[1] = "Attack";
			entityDef.combatLevel = 55;
			break;

		case 4771:
			entityDef.name = "Amik Varze";
			entityDef.actions[2] = "Imbue Rings";
			break;

		case 4444:
			entityDef.actions = new String[5];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.models = new int[2];
			entityDef.models[0] = 11216;
			entityDef.models[1] = 11217;
			entityDef.anInt91 = 42;
			entityDef.anInt86 = 42;
			entityDef.walkAnim = 3145;
			entityDef.standAnim = 3144;
			entityDef.name = "Chaos Elemental Jr";
			entityDef.description = "A smaller version of the chaos elemental.";
			break;

		case 2001:
			entityDef.name = "Callisto";
			entityDef.combatLevel = 470;
			entityDef.models[0] = 28298;
			entityDef.actions = new String[5];
			entityDef.actions[1] = "Attack";
			entityDef.anInt86 = 84;
			NpcDefinition callisto = forID(105);
			entityDef.standAnim = callisto.standAnim;
			entityDef.walkAnim = callisto.walkAnim;
			entityDef.actions = callisto.actions;
			entityDef.anInt91 = 72;
			break;

		case 4446:
			entityDef.actions = new String[5];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.models = new int[5];
			entityDef.walkAnim = 4635;
			entityDef.standAnim = 90;
			entityDef.anInt91 = 37;
			entityDef.anInt86 = 37;
			entityDef.name = "King Black Dragon Jr";
			entityDef.description = "A smaller version of the king black dragon.";
			break;

		case 3999:
			entityDef.actions = new String[5];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.models = new int[4];
			NpcDefinition mole = forID(5779);
			entityDef.models[0] = mole.models[0];
			entityDef.models[1] = mole.models[1];
			entityDef.models[2] = mole.models[2];
			entityDef.models[3] = mole.models[3];
			entityDef.walkAnim = mole.walkAnim;
			entityDef.standAnim = mole.standAnim;
			entityDef.anInt91 = 37; // width?
			entityDef.anInt86 = 37; // height?
			entityDef.name = "Giant Mole Jr";
			entityDef.description = "A smaller version of the giant mole.";
			break;

		case 3996:
			entityDef.models = new int[1];
			entityDef.models[0] = 28231;
			entityDef.name = "Kraken Jr";
			NpcDefinition cavejr = forID(4315);
			entityDef.actions = new String[5];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.models = new int[1];
			entityDef.models[0] = 28231;
			entityDef.standAnim = cavejr.standAnim;
			entityDef.walkAnim = cavejr.walkAnim;
			entityDef.anInt86 = 20;
			entityDef.anInt91 = 20;
			entityDef.description = "A smaller version of the kraken.";
			break;
			
		case 5568: //Red
			NpcDefinition death = forID(5567);
			stream.currentOffset = streamIndices[5567];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.readValues(stream);
			entityDef.models = new int[6];
			entityDef.models[0] = 3188;
			entityDef.models[1] = death.models[1];
			entityDef.models[2] = death.models[2];
			entityDef.models[3] = death.models[3];
			entityDef.models[4] = death.models[4];
			entityDef.models[5] = death.models[5];
			entityDef.name = "Death Jr";
			entityDef.description = "A smaller version of Death himself.";
			entityDef.standAnim = death.standAnim;
			entityDef.walkAnim = death.walkAnim;
			entityDef.anInt86 = 90;
			entityDef.anInt91 = 90;
			break;
			
		case 5569: //Black
			NpcDefinition deathBlack = forID(5567);
			stream.currentOffset = streamIndices[5567];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.readValues(stream);
			entityDef.models = new int[6];
			entityDef.models[0] = 3188;
			entityDef.models[1] = deathBlack.models[1];
			entityDef.models[2] = deathBlack.models[2];
			entityDef.models[3] = deathBlack.models[3];
			entityDef.models[4] = deathBlack.models[4];
			entityDef.models[5] = deathBlack.models[5];
			entityDef.originalColors[0] = 926;
			entityDef.newColors[0] = 0;
			entityDef.name = "Death Jr";
			entityDef.description = "A smaller version of Death himself.";
			entityDef.standAnim = deathBlack.standAnim;
			entityDef.walkAnim = deathBlack.walkAnim;
			entityDef.anInt86 = 90;
			entityDef.anInt91 = 90;
			break;
			
		case 5570: //Blue
			NpcDefinition deathBlue = forID(5567);
			stream.currentOffset = streamIndices[5567];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.readValues(stream);
			entityDef.models = new int[6];
			entityDef.models[0] = 3188;
			entityDef.models[1] = deathBlue.models[1];
			entityDef.models[2] = deathBlue.models[2];
			entityDef.models[3] = deathBlue.models[3];
			entityDef.models[4] = deathBlue.models[4];
			entityDef.models[5] = deathBlue.models[5];
			entityDef.originalColors[0] = 926;
			entityDef.newColors[0] = 43934;
			entityDef.name = "Death Jr";
			entityDef.description = "A smaller version of Death himself.";
			entityDef.standAnim = deathBlue.standAnim;
			entityDef.walkAnim = deathBlue.walkAnim;
			entityDef.anInt86 = 90;
			entityDef.anInt91 = 90;
			break;
			
		case 5571: //Green
			NpcDefinition deathGreen = forID(5567);
			stream.currentOffset = streamIndices[5567];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.readValues(stream);
			entityDef.models = new int[6];
			entityDef.models[0] = 3188;
			entityDef.models[1] = deathGreen.models[1];
			entityDef.models[2] = deathGreen.models[2];
			entityDef.models[3] = deathGreen.models[3];
			entityDef.models[4] = deathGreen.models[4];
			entityDef.models[5] = deathGreen.models[5];
			entityDef.originalColors[0] = 926;
			entityDef.newColors[0] = 86933;
			entityDef.name = "Death Jr";
			entityDef.description = "A smaller version of Death himself.";
			entityDef.standAnim = deathGreen.standAnim;
			entityDef.walkAnim = deathGreen.walkAnim;
			entityDef.anInt86 = 90;
			entityDef.anInt91 = 90;
			break;
			
		case 5572: //Regular
			NpcDefinition deathRegular = forID(5567);
			stream.currentOffset = streamIndices[5567];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.readValues(stream);
			entityDef.models = new int[6];
			//entityDef.models[0] = 3188;
			entityDef.models[1] = deathRegular.models[1];
			entityDef.models[2] = deathRegular.models[2];
			entityDef.models[3] = deathRegular.models[3];
			entityDef.models[4] = deathRegular.models[4];
			entityDef.models[5] = deathRegular.models[5];
			entityDef.name = "Death Jr";
			entityDef.description = "A smaller version of Death himself.";
			entityDef.standAnim = deathRegular.standAnim;
			entityDef.walkAnim = deathRegular.walkAnim;
			entityDef.anInt86 = 90;
			entityDef.anInt91 = 90;
			break;

		}
		return entityDef;
	}

	public static int totalAmount;

	public static void unpackConfig(StreamLoader streamLoader) {
		stream = new Stream(streamLoader.getDataForName("npc.dat"));
		Stream stream = new Stream(streamLoader.getDataForName("npc.idx"));
		totalAmount = stream.readUnsignedWord();
		streamIndices = new int[totalAmount];
		System.out.println(totalAmount + " NPCs loaded.");
		int i = 2;
		for (int j = 0; j < totalAmount; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}

		cache = new NpcDefinition[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new NpcDefinition();
		for (int index = 0; index < totalAmount; index++) {
			NpcDefinition ed = forID(index);
			if (ed == null)
				continue;
			if (ed.name == null)
				continue;
		}
	}
	
	/*public void readValues(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				return;
			if (i == 1) {
				int j = stream.readUnsignedByte();
				models = new int[j];
				for (int j1 = 0; j1 < j; j1++)
					models[j1] = stream.readUnsignedWord();

			} else if (i == 2)
				name = stream.readString();
			else if (i == 3)
				description = stream.readString();
			else if (i == 12)
				squareLength = stream.readSignedByte();
			else if (i == 13)
				standAnim = stream.readUnsignedWord();
			else if (i == 14)
				walkAnim = stream.readUnsignedWord();
			else if (i == 17) {
				walkAnim = stream.readUnsignedWord();
				anInt58 = stream.readUnsignedWord();
				anInt83 = stream.readUnsignedWord();
				anInt55 = stream.readUnsignedWord();
                if (anInt58 == 65535) {
                    anInt58 = -1;
                }
                if (anInt83 == 65535) {
                    anInt83 = -1;
                }
                if (anInt55 == 65535) {
                    anInt55 = -1;
                }
			} else if (i >= 30 && i < 40) {
				if (actions == null)
					actions = new String[5];
				actions[i - 30] = stream.readString();
				if (actions[i - 30].equalsIgnoreCase("hidden"))
					actions[i - 30] = null;
			} else if (i == 40) {
				int k = stream.readUnsignedByte();
				originalColors = new int[k];
				newColors = new int[k];
				for (int k1 = 0; k1 < k; k1++) {
					originalColors[k1] = stream.readUnsignedWord();
					newColors[k1] = stream.readUnsignedWord();
				}

			} else if (i == 60) {
				int l = stream.readUnsignedByte();
				dialogueModels = new int[l];
				for (int l1 = 0; l1 < l; l1++)
					dialogueModels[l1] = stream.readUnsignedWord();

			} else if (i == 90)
				stream.readUnsignedWord();
			else if (i == 91)
				stream.readUnsignedWord();
			else if (i == 92)
				stream.readUnsignedWord();
			else if (i == 93)
				minimapDot = false;
			else if (i == 95)
				combatLevel = stream.readUnsignedWord();
			else if (i == 97)
				anInt91 = stream.readUnsignedWord();
			else if (i == 98)
				anInt86 = stream.readUnsignedWord();
			else if (i == 99)
				aBoolean93 = true;
			else if (i == 100)
				anInt85 = stream.readSignedByte();
			else if (i == 101)
				anInt92 = stream.readSignedByte() * 5;
			else if (i == 102)
				anInt75 = stream.readUnsignedByte();
			else if (i == 103)
				getDegreesToTurn = stream.readUnsignedByte();
			else if (i == 106) {
				anInt57 = stream.readUnsignedWord();
				if (anInt57 == 65535)
					anInt57 = -1;
				anInt59 = stream.readUnsignedWord();
				if (anInt59 == 65535)
					anInt59 = -1;
				int i1 = stream.readUnsignedByte();
				childrenIDs = new int[i1 + 1];
				for (int i2 = 0; i2 <= i1; i2++) {
					childrenIDs[i2] = stream.readUnsignedWord();
					if (childrenIDs[i2] == 65535)
						childrenIDs[i2] = -1;
				}

			} else if (i == 107)
				aBoolean84 = false;
		} while (true);
	}*/
	private void readValues(Stream stream) {
		while(true) {
			int opcode = stream.readUnsignedByte();
			if(opcode == 0)
				return;
			if(opcode == 1) {
				int j = stream.readUnsignedByte();
				models = new int[j];
				for(int j1 = 0; j1 < j; j1++)
					models[j1] = stream.readUnsignedWord();

			} else if(opcode == 2)
				name = stream.readString();
			else if(opcode == 3)
				description = stream.readString();
			else if(opcode == 12)
				boundDim = stream.readSignedByte();
			else if(opcode == 13)
				standAnim = stream.readUnsignedWord();
			else if(opcode == 14)
				walkAnim = stream.readUnsignedWord();
			else if(opcode == 17) {
				walkAnim = stream.readUnsignedWord();
				anInt58 = stream.readUnsignedWord();
				anInt83 = stream.readUnsignedWord();
				anInt55 = stream.readUnsignedWord();
                if (anInt58 == 65535) {
                    anInt58 = -1;
                }
                if (anInt83 == 65535) {
                    anInt83 = -1;
                }
                if (anInt55 == 65535) {
                    anInt55 = -1;
                }
			} else if(opcode >= 30 && opcode < 40) {
				if(actions == null)
					actions = new String[5];
				actions[opcode - 30] = stream.readString();
				if(actions[opcode - 30].equalsIgnoreCase("hidden"))
					actions[opcode - 30] = null;
			} else if(opcode == 40) {
				int k = stream.readUnsignedByte();
				originalColors = new int[k];
				newColors = new int[k];
				for(int k1 = 0; k1 < k; k1++) {
					originalColors[k1] = stream.readUnsignedWord();
					newColors[k1] = stream.readUnsignedWord();
				}

			} else if(opcode == 60) {
				int l = stream.readUnsignedByte();
				dialogueModels = new int[l];
				for(int l1 = 0; l1 < l; l1++)
					dialogueModels[l1] = stream.readUnsignedWord();

			} else if(opcode == 93)
				onMinimap = false;
			else if(opcode == 95)
				combatLevel = stream.readUnsignedWord();
			else if(opcode == 97)
				anInt91 = stream.readUnsignedWord();
			else if(opcode == 98)
				anInt86 = stream.readUnsignedWord();
			else if(opcode == 99)
				aBoolean93 = true;
			else if(opcode == 100)
				anInt85 = stream.readSignedByte();
			else if(opcode == 101)
				anInt92 = stream.readSignedByte();
			else if(opcode == 102)
				anInt75 = stream.readSignedByte();
			else if(opcode == 103)
				getDegreesToTurn = stream.readSignedByte();
			else if(opcode == 106) {
				anInt57 = stream.readUnsignedWord();
				if(anInt57 == 65535)
					anInt57 = -1;
				anInt59 = stream.readUnsignedWord();
				if(anInt59 == 65535)
					anInt59 = -1;
				int i1 = stream.readUnsignedByte();
				childrenIDs = new int[i1 + 1];
				for(int i2 = 0; i2 <= i1; i2++) {
					childrenIDs[i2] = stream.readUnsignedWord();
					if(childrenIDs[i2] == 65535)
						childrenIDs[i2] = -1;
				}
			} else if(opcode == 107)
				aBoolean84 = false;
		}
	}

	public Model method160() {
		if (childrenIDs != null) {
			NpcDefinition entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.method160();
		}
		if (dialogueModels == null) {
			return null;
		}
		boolean flag1 = false;
		for (int i = 0; i < dialogueModels.length; i++)
			if (!Model.method463(dialogueModels[i]))
				flag1 = true;

		if (flag1)
			return null;
		Model aclass30_sub2_sub4_sub6s[] = new Model[dialogueModels.length];
		for (int j = 0; j < dialogueModels.length; j++)
			aclass30_sub2_sub4_sub6s[j] = Model.method462(dialogueModels[j]);

		Model model;
		if (aclass30_sub2_sub4_sub6s.length == 1)
			model = aclass30_sub2_sub4_sub6s[0];
		else
			model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);

		if (originalColors != null)
			for (int k = 0; k < originalColors.length; k++)
				model.replaceColor(originalColors[k], newColors[k]);

		return model;
	}

	public NpcDefinition method161() {
		int j = -1;
		if (anInt57 != -1 && anInt57 <= 2113) {
			VarBit varBit = VarBit.cache[anInt57];
			int k = varBit.anInt648;
			int l = varBit.anInt649;
			int i1 = varBit.anInt650;
			int j1 = Client.anIntArray1232[i1 - l];
			j = clientInstance.variousSettings[k] >> l & j1;
		} else if (anInt59 != -1)
			j = clientInstance.variousSettings[anInt59];
		if (j < 0 || j >= childrenIDs.length || childrenIDs[j] == -1)
			return null;
		else
			return forID(childrenIDs[j]);
	}

	public Model method164(int j, int k, int ai[]) {
		if (childrenIDs != null) {
			NpcDefinition entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.method164(j, k, ai);
		}
		Model model = (Model) mruNodes.insertFromCache(interfaceType);
		if (model == null) {
			boolean flag = false;
			for (int i1 = 0; i1 < models.length; i1++)
				if (!Model.method463(models[i1]))
					flag = true;

			if (flag)
				return null;
			Model aclass30_sub2_sub4_sub6s[] = new Model[models.length];
			for (int j1 = 0; j1 < models.length; j1++)
				aclass30_sub2_sub4_sub6s[j1] = Model.method462(models[j1]);

			if (aclass30_sub2_sub4_sub6s.length == 1)
				model = aclass30_sub2_sub4_sub6s[0];
			else
				model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
			if (originalColors != null) {
				for (int k1 = 0; k1 < originalColors.length; k1++)
					model.replaceColor(originalColors[k1], newColors[k1]);

			}
			model.method469();
			model.method479(84 + anInt85, 1000 + anInt92, -90, -580, -90, true);
			mruNodes.removeFromCache(model, interfaceType);
		}
		Model model_1 = Model.aModel_1621;
		model_1.method464(model, Class36.method532(k) & Class36.method532(j));
		if (k != -1 && j != -1)
			model_1.method471(ai, j, k);
		else if (k != -1)
			model_1.method470(k);
		if (anInt91 != 128 || anInt86 != 128)
			model_1.method478(anInt91, anInt91, anInt86);
		model_1.method466();
		model_1.anIntArrayArray1658 = null;
		model_1.anIntArrayArray1657 = null;
		if (boundDim == 1)
			model_1.aBoolean1659 = true;
		return model_1;
	}

	private NpcDefinition() {
		anInt55 = -1;
		anInt57 = walkAnim;
		anInt58 = walkAnim;
		anInt59 = walkAnim;
		combatLevel = -1;
		anInt64 = 1834;
		walkAnim = -1;
		boundDim = 1;
		anInt75 = -1;
		standAnim = -1;
		interfaceType = -1L;
		getDegreesToTurn = 32;
		anInt83 = -1;
		aBoolean84 = true;
		anInt86 = 128;
		onMinimap = true;
		anInt91 = 128;
		aBoolean93 = false;
	}

	public static void nullLoader() {
		mruNodes = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}
	
	public static void dumpList() {
		try {
			File file = new File("OSRS NPC List.txt");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for (int i = 0; i < totalAmount; i++) {
					NpcDefinition definition = forID(i);
					if (definition != null) {
						writer.write("npc = "+ i + "\t" + definition.name + "\t" + definition.combatLevel  + "\t" + definition.standAnim  + "\t" + definition.walkAnim  + "\t");
						writer.newLine();
					}
				}
			}
			
			System.out.println("Finished dumping npc definitions.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void dumpSizes() {
		try {
			File file = new File("OSRS NPC Sizes.txt");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for (int i = 0; i < totalAmount; i++) {
					NpcDefinition definition = forID(i);
					if (definition != null) {
						writer.write(i + " " + definition.boundDim);
						writer.newLine();
					}
				}
			}
			
			System.out.println("Finished dumping npc definitions.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int anInt55;
	public static int anInt56;
	public int anInt57;
	public int anInt58;
	public int anInt59;
	public static Stream stream;
	public int combatLevel;
	public final int anInt64;
	public String name;
	public String actions[];
	public int walkAnim;
	public byte boundDim;
	public int[] newColors;
	public static int[] streamIndices;
	public int[] dialogueModels;
	public int anInt75;
	public int[] originalColors;
	public int standAnim;
	public long interfaceType;
	public int getDegreesToTurn;
	public static NpcDefinition[] cache;
	public static Client clientInstance;
	public int anInt83;
	public boolean aBoolean84;
	public int anInt85;
	public int anInt86;
	public boolean onMinimap;
	public int childrenIDs[];
	public String description;
	public int anInt91;
	public int anInt92;
	public boolean aBoolean93;
	public int[] models;
	public static MRUNodes mruNodes = new MRUNodes(30);

}