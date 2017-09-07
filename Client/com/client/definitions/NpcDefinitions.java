package com.client.definitions;

import com.client.Class36;
import com.client.Client;
import com.client.MRUNodes;
import com.client.Model;
import com.client.Stream;
import com.client.StreamLoader;

public final class NpcDefinitions {

	public static NpcDefinitions forID(int i) {
		for (int j = 0; j < 20; j++)
			if (cache[j].interfaceType == i)
				return cache[j];

		anInt56 = (anInt56 + 1) % 20;
		NpcDefinitions entityDef = cache[anInt56] = new NpcDefinitions();
		stream.currentOffset = streamIndices[i];
		entityDef.interfaceType = i;
		entityDef.readValues(stream);
		switch (i) {
		case 3257:
			entityDef.actions = new String[] { "Pickpocket", null, "Trade", null, null };
			break;
			
		case 2580:
			entityDef.actions = new String[] { "Talk-to", null, "Teleport to Abyss", null, null };
			break;
			
		case 5057:
			entityDef.anInt91 = 130;
			entityDef.anInt86 = 130;
			entityDef.aByte68 = 3;
			entityDef.aBoolean87 = false;
			entityDef.aBoolean84 = false;
			break;
			
		case 6611:
			NpcDefinitions skeleton = forID(83);
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
			
		case 6528:
			entityDef.actions = new String[5];
			entityDef.actions[1] = "Attack";
			entityDef.combatLevel = 1225;
			break;
			
		case 4771:
			entityDef.name = "Amik Varze";
			entityDef.actions[2] = "Imbue Rings";
			break;

		case 4444:
			entityDef.actions = new String[5];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.anIntArray94 = new int[2];
			entityDef.anIntArray94[0] = 11216;
			entityDef.anIntArray94[1] = 11217;
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
			entityDef.anIntArray94[0] = 28298;
			entityDef.actions = new String[5];
			entityDef.actions[1] = "Attack";
			entityDef.anInt86 = 84;
			NpcDefinitions callisto = forID(105);
			entityDef.standAnim = callisto.standAnim;
			entityDef.walkAnim = callisto.walkAnim;
			entityDef.actions = callisto.actions;
			entityDef.anInt91 = 72;
			break;
			
		case 4446:
			entityDef.actions = new String[5];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.anIntArray94 = new int[5];
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
			entityDef.anIntArray94 = new int[4];
			NpcDefinitions mole = forID(5779);
			entityDef.anIntArray94[0] = mole.anIntArray94[0];
			entityDef.anIntArray94[1] = mole.anIntArray94[1];
			entityDef.anIntArray94[2] = mole.anIntArray94[2];
			entityDef.anIntArray94[3] = mole.anIntArray94[3];
			entityDef.walkAnim = mole.walkAnim;
			entityDef.standAnim = mole.standAnim;
			entityDef.anInt91 = 37; // width?
			entityDef.anInt86 = 37; // height?
			entityDef.name = "Giant Mole Jr";
			entityDef.description = "A smaller version of the giant mole.";
			break;
			
		case 3996:
			entityDef.anIntArray94 = new int[1];
			entityDef.anIntArray94[0] = 28231;
			entityDef.name = "Kraken Jr";
			NpcDefinitions cavejr = forID(4315);
			entityDef.actions = new String[5];
			entityDef.actions[0] = "Pick-up";
			entityDef.actions[2] = "Talk-to";
			entityDef.anIntArray94 = new int[1];
			entityDef.anIntArray94[0] = 28231;
			entityDef.standAnim = cavejr.standAnim;
			entityDef.walkAnim = cavejr.walkAnim;
			entityDef.anInt86 = 20;
			entityDef.anInt91 = 20;
			entityDef.description = "A smaller version of the kraken.";
			break;
			
		}
		return entityDef;
	}
	
	public static void unpackConfig(StreamLoader streamLoader) {
		stream = new Stream(streamLoader.getDataForName("npc.dat"));
		Stream stream = new Stream(streamLoader.getDataForName("npc.idx"));
		int totalNPCs = stream.readUnsignedWord();
		System.out.println("Total npcs: " + totalNPCs);
		streamIndices = new int[totalNPCs];
		int i = 2;
		for (int j = 0; j < totalNPCs; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}

		cache = new NpcDefinitions[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new NpcDefinitions();
		for (int index = 0; index < totalNPCs; index++) {
			NpcDefinitions ed = forID(index);
			if (ed == null)
				continue;
			if (ed.name == null)
				continue;
		}
	}
	
	private void readValues(Stream stream) {
		do {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				return;
			if (opcode == 1) {
				int j = stream.readUnsignedByte();
				anIntArray94 = new int[j];
				for (int j1 = 0; j1 < j; j1++)
					anIntArray94[j1] = stream.readUnsignedWord();

			} else if (opcode == 2)
				name = stream.readString();
			else if (opcode == 3)
				description = stream.readString();
			else if (opcode == 12)
				aByte68 = stream.readSignedByte();
			else if (opcode == 13)
				standAnim = stream.readUnsignedWord();
			else if (opcode == 14)
				walkAnim = stream.readUnsignedWord();
			else if (opcode == 17) {
				walkAnim = stream.readUnsignedWord();
				anInt58 = stream.readUnsignedWord();
				anInt83 = stream.readUnsignedWord();
				anInt55 = stream.readUnsignedWord();
			} else if (opcode >= 30 && opcode < 40) {
				if (actions == null)
					actions = new String[5];
				actions[opcode - 30] = stream.readString();
				if (actions[opcode - 30].equalsIgnoreCase("hidden"))
					actions[opcode - 30] = null;
			} else if (opcode == 40) {
				int k = stream.readUnsignedByte();
				originalColors = new int[k];
				newColors = new int[k];
				for (int k1 = 0; k1 < k; k1++) {
					originalColors[k1] = stream.readUnsignedWord();
					newColors[k1] = stream.readUnsignedWord();
				}

			} else if (opcode == 60) {
				int l = stream.readUnsignedByte();
				anIntArray73 = new int[l];
				for (int l1 = 0; l1 < l; l1++)
					anIntArray73[l1] = stream.readUnsignedWord();

			} else if (opcode == 93)
				aBoolean87 = false;
			else if (opcode == 95)
				combatLevel = stream.readUnsignedWord();
			else if (opcode == 97)
				anInt91 = stream.readUnsignedWord();
			else if (opcode == 98)
				anInt86 = stream.readUnsignedWord();
			else if (opcode == 99)
				aBoolean93 = true;
			else if (opcode == 100)
				anInt85 = stream.readSignedByte();
			else if (opcode == 101)
				anInt92 = stream.readSignedByte();
			else if (opcode == 102)
				anInt75 = stream.readSignedByte();
			else if (opcode == 103)
				anInt79 = stream.readSignedByte();
			else if (opcode == 106) {
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
			} else if (opcode == 107)
				aBoolean84 = false;
		} while (true);
	}
	
	public Model method160() {
		if (childrenIDs != null) {
			NpcDefinitions entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.method160();
		}
		if (anIntArray73 == null) {
			return null;
		}
		boolean flag1 = false;
		for (int i = 0; i < anIntArray73.length; i++)
			if (!Model.method463(anIntArray73[i]))
				flag1 = true;

		if (flag1)
			return null;
		Model aclass30_sub2_sub4_sub6s[] = new Model[anIntArray73.length];
		for (int j = 0; j < anIntArray73.length; j++)
			aclass30_sub2_sub4_sub6s[j] = Model.method462(anIntArray73[j]);

		Model model;
		if (aclass30_sub2_sub4_sub6s.length == 1)
			model = aclass30_sub2_sub4_sub6s[0];
		else
			model = new Model(aclass30_sub2_sub4_sub6s.length,
					aclass30_sub2_sub4_sub6s);
		
		if (originalColors != null)
			for (int k = 0; k < originalColors.length; k++)
				model.replaceColor(originalColors[k], newColors[k]);

		return model;
	}

	public NpcDefinitions method161() {
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
			NpcDefinitions entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.method164(j, k, ai);
		}
		Model model = (Model) mruNodes.insertFromCache(interfaceType);
		if (model == null) {
			boolean flag = false;
			for (int i1 = 0; i1 < anIntArray94.length; i1++)
				if (!Model.method463(anIntArray94[i1]))
					flag = true;

			if (flag)
				return null;
			Model aclass30_sub2_sub4_sub6s[] = new Model[anIntArray94.length];
			for (int j1 = 0; j1 < anIntArray94.length; j1++)
				aclass30_sub2_sub4_sub6s[j1] = Model
						.method462(anIntArray94[j1]);

			if (aclass30_sub2_sub4_sub6s.length == 1)
				model = aclass30_sub2_sub4_sub6s[0];
			else
				model = new Model(aclass30_sub2_sub4_sub6s.length,
						aclass30_sub2_sub4_sub6s);
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
		if (aByte68 == 1)
			model_1.aBoolean1659 = true;
		return model_1;
	}

	private NpcDefinitions() {
		anInt55 = -1;
		anInt57 = -1;
		anInt58 = -1;
		anInt59 = -1;
		combatLevel = -1;
		anInt64 = 1834;
		walkAnim = -1;
		aByte68 = 1;
		anInt75 = -1;
		standAnim = -1;
		interfaceType = -1L;
		anInt79 = 32;
		anInt83 = -1;
		aBoolean84 = true;
		anInt86 = 128;
		aBoolean87 = true;
		anInt91 = 128;
		aBoolean93 = false;
	}
	
	public static void nullLoader() {
		mruNodes = null;
		streamIndices = null;
		cache = null;
		stream = null;
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
	public byte aByte68;
	public int[] newColors;
	public static int[] streamIndices;
	public int[] anIntArray73;
	public int anInt75;
	public int[] originalColors;
	public int standAnim;
	public long interfaceType;
	public int anInt79;
	public static NpcDefinitions[] cache;
	public static Client clientInstance;
	public int anInt83;
	public boolean aBoolean84;
	public int anInt85;
	public int anInt86;
	public boolean aBoolean87;
	public int childrenIDs[];
	public String description;
	public int anInt91;
	public int anInt92;
	public boolean aBoolean93;
	public int[] anIntArray94;
	public static MRUNodes mruNodes = new MRUNodes(30);

}