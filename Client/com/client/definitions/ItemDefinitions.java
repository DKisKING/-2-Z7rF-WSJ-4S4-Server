package com.client.definitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.client.DrawingArea;
import com.client.MRUNodes;
import com.client.Model;
import com.client.Sprite;
import com.client.Stream;
import com.client.StreamLoader;
import com.client.Texture;

public final class ItemDefinitions {
	
	public static void unpackConfig(StreamLoader archive) {
		stream = new Stream(archive.getDataForName("obj.dat"));
		Stream stream = new Stream(archive.getDataForName("obj.idx"));
		totalItems = stream.readUnsignedWord() + 21;
		streamIndices = new int[totalItems + 50000];
		int i = 2;
		for (int j = 0; j < totalItems - 21; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}
		cache = new ItemDefinitions[10];
		for (int k = 0; k < 10; k++) {
			cache[k] = new ItemDefinitions();
		}
	}
	
	public static ItemDefinitions forID(int i) {
		for (int j = 0; j < 10; j++) {
			if (cache[j].id == i) {
				return cache[j];
			}
		}
		cacheIndex = (cacheIndex + 1) % 10;
		ItemDefinitions itemDef = cache[cacheIndex];
		stream.currentOffset = streamIndices[i];
		itemDef.id = i;
		itemDef.setDefaults();
		itemDef.readValues(stream);
		if (itemDef.noteTemplate != -1) 
			itemDef.toNote();
		
		customItems(i);
		return itemDef;
	}
	
	public void readValues(Stream stream) {
		while(true) {
			int opcode = stream.readUnsignedByte();
			if(opcode == 0)
				return;
			if(opcode == 1)
				modelID = stream.readUnsignedWord();
			else if(opcode == 2)
				name = stream.readString();
			else if(opcode == 3)
				description = stream.readString();
			else if(opcode == 4)
				zoom = stream.readUnsignedWord();
			else if(opcode == 5)
				rotationX = stream.readUnsignedWord();
			else if(opcode == 6)
				rotationY = stream.readUnsignedWord();
			else if(opcode == 7) {
				offsetX = stream.readUnsignedWord();
				if(offsetX > 32767)
					offsetX -= 0x10000;
			} else if(opcode == 8) {
				offsetY = stream.readUnsignedWord();
				if(offsetY > 32767)
					offsetY -= 0x10000;
			} else if(opcode == 11)
				stackable = true;
			else if(opcode == 12)
				value = stream.readDWord();
			else if(opcode == 16)
				membersObject = true;
			else if(opcode == 23) {
				maleEquip1 = stream.readUnsignedWord();
				maleEquipOffset = stream.readSignedByte();
			} else if (opcode == 24)
				maleEquip2 = stream.readUnsignedWord();
			else if (opcode == 25) {
				femaleEquip1 = stream.readUnsignedWord();
				femaleEquipOffset = stream.readSignedByte();
			} else if (opcode == 26)
				femaleEquip2 = stream.readUnsignedWord();
			else if(opcode >= 30 && opcode < 35) {
				if(groundOptions == null)
					groundOptions = new String[5];
				groundOptions[opcode - 30] = stream.readString();
				if(groundOptions[opcode - 30].equalsIgnoreCase("hidden"))
					groundOptions[opcode - 30] = null;
			} else if(opcode >= 35 && opcode < 40) {
				if(options == null)
					options = new String[5];
				options[opcode - 35] = stream.readString();
			} else if(opcode == 40) {
				int j = stream.readUnsignedByte();
				originalColors = new int[j];
				newColors = new int[j];
				for(int k = 0; k < j; k++) {
					newColors[k] = stream.readUnsignedWord();
					originalColors[k] = stream.readUnsignedWord();
				}
			} else if(opcode == 65)
				searchableItem = true;
			else if(opcode == 78)
				maleEmblem = stream.readUnsignedWord();
			else if(opcode == 79)
				femaleEmblem = stream.readUnsignedWord();
			else if(opcode == 90)
				maleChatHead = stream.readUnsignedWord();
			else if(opcode == 91)
				femaleChatHead = stream.readUnsignedWord();
			else if(opcode == 92)
				maleChatHeadHat = stream.readUnsignedWord();
			else if(opcode == 93)
				femaleChatHeadHat = stream.readUnsignedWord();
			else if(opcode == 95)
				rotationZ = stream.readUnsignedWord();
			else if(opcode == 97)
				note = stream.readUnsignedWord();
			else if(opcode == 98)
				noteTemplate = stream.readUnsignedWord();
			else if(opcode == 100) {
				int length = stream.readUnsignedByte();
				stacks = new int[length];
				stackAmounts = new int[length];
				for(int k = 0; k < length; k++) {
					stacks[k] = stream.readUnsignedWord();
					stackAmounts[k] = stream.readUnsignedWord();
				}
			} else if(opcode == 110)
				width = stream.readUnsignedWord();
			else if(opcode == 111)
				height = stream.readUnsignedWord();
			else if(opcode == 112)
				depth = stream.readUnsignedWord();
			else if(opcode == 113)
				brightness = stream.readSignedByte();
			else if(opcode == 114)
				contrast = stream.readSignedByte() * 5;
			else if(opcode == 115)
				team = stream.readUnsignedByte();
				
		}
	}
	
	private static void customItems(int itemId) {
		ItemDefinitions itemDef = forID(itemId);

		switch (itemId) {

		case 11773:
		case 11771:
		case 11770:
		case 11772:
			itemDef.brightness += 45;
			break;

		case 2678:
			itemDef.name = "Scroll of Defence";
			itemDef.description = "Read this scroll to reset your defence level.";
			break;
		case 2697:
			itemDef.name = "Contributor Scroll";
			itemDef.description = "Read this scroll to be rewarded with the contributor status.";
			break;
		case 2698:
			itemDef.name = "Sponsor Scroll";
			itemDef.description = "Read this scroll to be rewarded with the sponsor status.";
			break;
		case 2699:
			itemDef.name = "Supporter Scroll";
			itemDef.description = "Read this scroll to be rewarded with the Supporter status.";
			break;
		case 2700:
			itemDef.name = "VIP Scroll";
			itemDef.description = "Read this scroll to be rewarded with the VIP status.";
			break;
		case 2701:
			itemDef.name = "Gambler Scroll";
			itemDef.description = "Read this scroll to be rewarded with the Gambler rank.";
			break;
		case 1464:
			itemDef.name = "Vote Ticket";
			itemDef.description = "This ticket can be exchanged for a voting point.";
			break;
		case 2996:
			itemDef.name = "PK Point Ticket";
			itemDef.description = "Exchange this for a PK Point.";
			break;
		case 8901:
			itemDef.options = new String[5];
			itemDef.options[1] = "Wear";
			itemDef.options[2] = "Assemble";
			break;
		case 2839:
			itemDef.name = "Slayer Helmet Recipe";
			itemDef.description = "Read this scroll to learn the slayer helmet recipe.";
			break;
		case 13738:
			itemDef.options = new String[5];
			itemDef.options[1] = "Wield";
			itemDef.modelID = 40922;
			itemDef.zoom = 1616;
			itemDef.rotationX = 396;
			itemDef.rotationY = 1050;
			itemDef.offsetY = 4;
			itemDef.offsetX = -3;
			itemDef.rotationZ = 0;
			itemDef.maleEquip1 = 40944;
			itemDef.femaleEquip1 = 40944;
			itemDef.stackable = false;
			itemDef.name = "Arcane spirit shield";
			itemDef.description = "An ethereal shield with an arcane sigil attatched to it.";
			break;
		case 13740:
			itemDef.options = new String[5];
			itemDef.options[1] = "Wield";
			itemDef.modelID = 40921;
			itemDef.zoom = 1616;
			itemDef.rotationX = 396;
			itemDef.rotationY = 1050;
			itemDef.offsetY = 4;
			itemDef.offsetX = -3;
			itemDef.rotationZ = 0;
			itemDef.maleEquip1 = 40939;
			itemDef.femaleEquip1 = 40939;
			itemDef.stackable = false;
			itemDef.name = "Divine spirit shield";
			itemDef.description = "An ethereal shield with a divine sigil attatched to it.";
			break;
		case 13742:
			itemDef.options = new String[5];
			itemDef.options[1] = "Wield";
			itemDef.modelID = 40915;
			itemDef.zoom = 1616;
			itemDef.rotationX = 396;
			itemDef.rotationY = 1050;
			itemDef.offsetY = 4;
			itemDef.offsetX = -3;
			itemDef.rotationZ = 0;
			itemDef.maleEquip1 = 40942;
			itemDef.femaleEquip1 = 40942;
			itemDef.stackable = false;
			itemDef.name = "Elysian spirit shield";
			itemDef.description = "An ethereal shield with a elysian sigil attatched to it.";
			break;

		case 13744:
			itemDef.options = new String[5];
			itemDef.options[1] = "Wield";
			itemDef.modelID = 40920;
			itemDef.zoom = 1620;
			itemDef.rotationX = 396;
			itemDef.rotationY = 1050;
			itemDef.offsetY = 4;
			itemDef.offsetX = -2;
			itemDef.rotationZ = 0;
			itemDef.maleEquip1 = 40940;
			itemDef.femaleEquip1 = 40940;
			itemDef.stackable = false;
			itemDef.name = "Spectral spirit shield";
			itemDef.description = "An ethereal shield with a spectral sigil attatched to it.";
			break;
		case 15098:
			itemDef.name = "Dice (up to 100)";
			itemDef.zoom = 1104;
			itemDef.rotationY = 215;
			itemDef.rotationX = 94;
			itemDef.offsetY = 1;
			itemDef.groundOptions = new String[] { null, null, "Take", null,
					null };
			itemDef.options[1] = "Public-roll";
			itemDef.options[2] = "Switch-dice";
			itemDef.options[4] = "Drop";
			itemDef.modelID = 47852;
			itemDef.brightness = 15;
			itemDef.contrast = 25;
			break;
		case 11778:
			itemDef.options = new String[5];
			itemDef.options[1] = "Wear";
			itemDef.femaleEquip1 = -1;
			itemDef.offsetY = -6;
			itemDef.offsetX = 3;
			itemDef.zoom = 830;
			itemDef.rotationX = 268;
			itemDef.rotationY = 180;
			itemDef.maleEquip1 = -1;
			itemDef.modelID = 47733;
			itemDef.name = "Onyx ring (i)";
			break;
		case 14484:
			itemDef.options = new String[5];
			itemDef.options[1] = "Wield";
			itemDef.modelID = 44590;
			itemDef.maleEquip1 = 43660;// anInt165
			itemDef.femaleEquip1 = 43660;// anInt200
			itemDef.zoom = 789;
			itemDef.rotationX = 240;
			itemDef.rotationY = 60;
			itemDef.offsetX = -1;
			itemDef.offsetY = -23;
			itemDef.name = "Dragon claws";
			itemDef.description = "A set of fighting claws.";
			break;
		case 11730:
			itemDef.maleEquip1 = 27652;
			itemDef.femaleEquip1 = 27652;
			break;
		case 11694:
			itemDef.maleEquip1 = 27649;
			itemDef.femaleEquip1 = 27649;
			break;

		case 11696:
			itemDef.maleEquip1 = 27648;
			itemDef.femaleEquip1 = 27648;
			break;

		case 11698:
			itemDef.maleEquip1 = 27651;
			itemDef.femaleEquip1 = 27651;
			break;

		case 11700:
			itemDef.maleEquip1 = 27653;
			itemDef.femaleEquip1 = 27653;
			break;

		case 15567:
			itemDef.name = "Barrelchest Jr";
			itemDef.modelID = 22790;
			itemDef.rotationX = 0;
			itemDef.rotationY = 0;
			itemDef.zoom = 7500;
			itemDef.options = new String[] { null, null, null, null, "Drop" };
			break;
		case 11188:
			ItemDefinitions smoke = forID(12000);
			itemDef.name = "Mystic smoke staff";
			itemDef.description = "It's a slightly magical stick.";
			itemDef.femaleEquip1 = smoke.femaleEquip1;
			itemDef.maleEquip1 = smoke.maleEquip1;
			itemDef.modelID = smoke.modelID;
			itemDef.rotationX = 360;
			itemDef.rotationY = 1550;
			itemDef.zoom = 2905;
			itemDef.offsetY = -1;
			itemDef.offsetX = -4;
			itemDef.options = new String[5];
			itemDef.options[1] = "Wield";
			break;
		case 11144:
			ItemDefinitions occult = forID(12002);
			itemDef.name = "Occult necklace";
			itemDef.description = "A satanic evil embodies this amulet.";
			itemDef.femaleEquip1 = occult.femaleEquip1;
			itemDef.maleEquip1 = occult.maleEquip1;
			itemDef.modelID = occult.modelID;
			itemDef.zoom = occult.zoom;
			itemDef.rotationX = occult.rotationX;
			itemDef.rotationY = occult.rotationY;
			itemDef.offsetX = occult.offsetX;
			itemDef.offsetY = occult.offsetY;
			itemDef.options = new String[5];
			itemDef.options[1] = "Wield";
			break;
		case 15568:
			itemDef.name = "Chaos Elemental Jr";
			itemDef.modelID = 11216;
			itemDef.rotationX = 0;
			itemDef.rotationY = 0;
			itemDef.zoom = 7500;
			itemDef.options = new String[] { null, null, null, null, "Drop" };
			break;
		case 15573:
			itemDef.name = "Vet'ion Jr";
			itemDef.modelID = 28299;
			itemDef.rotationX = 0;
			itemDef.rotationY = 0;
			itemDef.zoom = 5000;
			itemDef.options = new String[] { null, null, null, null, "Drop" };
			break;
		case 8135:
			itemDef.name = "Venenatis Jr";
			itemDef.options = new String[] { null, null, null, null, "Drop" };
			break;
		case 15571:
			itemDef.name = "Mole Jr";
			itemDef.modelID = 12076;
			itemDef.rotationX = 0;
			itemDef.rotationY = 0;
			itemDef.zoom = 4400;
			itemDef.offsetX = 0;
			itemDef.offsetY = 0;
			itemDef.options = new String[] { null, null, null, null, "Drop" };
			break;
		case 15572:
			itemDef.name = "Callisto Jr";
			itemDef.modelID = 28298;
			itemDef.rotationX = 0;
			itemDef.rotationY = 0;
			itemDef.zoom = 4400;
			itemDef.offsetX = 0;
			itemDef.offsetY = 0;
			itemDef.options = new String[] { null, null, null, null, "Drop" };
			break;
		case 12409:
			itemDef.name = "Resource Area Teleport";
			itemDef.description = "Teleports you directly to the Resource Area gate (deep wilderness).";
			break;
		case 12410:
			itemDef.name = "Mage Bank Teleport";
			itemDef.description = "Teleports you directly into Mage Bank.";
			break;
		case 12408:
			itemDef.name = "Callisto Teleport";
			itemDef.description = "Teleports you directly to Callisto.";
			break;
		case 12411:
			itemDef.name = "KBD Lair Teleport";
			itemDef.description = "Teleports you directly into KBD's lair.";
			break;
		case 12407:
			itemDef.name = "Pirate's Hut Teleport";
			itemDef.description = "Teleports you directly to the Pirate's Hut (deep wild).";
			break;
		case 12773:
			itemDef.maleEquip1 = 5409;
			itemDef.femaleEquip1 = 5409;
			break;

		case 12954:
			itemDef.modelID = 10422;
			itemDef.name = "Dragon defender";
			itemDef.description = "It's a Dragon defender";
			itemDef.zoom = 589;
			itemDef.rotationX = 498;
			itemDef.rotationY = 256;
			itemDef.offsetX = 8;
			itemDef.offsetY = 8;
			itemDef.value = 68007;
			itemDef.maleEquip1 = 10420;
			itemDef.femaleEquip1 = 10421;
			itemDef.femaleEquipOffset = 6;
			itemDef.options = new String[] { null, "Wield", null, null, "Drop" };
			itemDef.groundOptions = new String[] { null, null, "Take", null,
					null };
			itemDef.rotationZ = 2047;
			break;
		}
	}

	public static void nullLoader() {
		mruNodes2 = null;
		mruNodes1 = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public boolean method192(int j) {
		int k = maleChatHead;
		int l = maleChatHeadHat;
		if (j == 1) {
			k = femaleChatHead;
			l = femaleChatHeadHat;
		}
		if (k == -1) {
			return true;
		}
		boolean flag = true;
		if (!Model.method463(k)) {
			flag = false;
		}
		if (l != -1 && !Model.method463(l)) {
			flag = false;
		}
		return flag;
	}

	public Model method194(int j) {
		int k = maleChatHead;
		int l = maleChatHeadHat;
		if (j == 1) {
			k = femaleChatHead;
			l = femaleChatHeadHat;
		}
		if (k == -1) {
			return null;
		}
		Model model = Model.method462(k);
		if (l != -1) {
			Model model_1 = Model.method462(l);
			Model aclass30_sub2_sub4_sub6s[] = { model, model_1 };
			model = new Model(2, aclass30_sub2_sub4_sub6s);
		}
		if (originalColors != null) {
			for (int i1 = 0; i1 < originalColors.length; i1++) {
				model.replaceColor(originalColors[i1], newColors[i1]);
			}

		}
		return model;
	}

	public boolean method195(int j) {
		int k = maleEquip1;
		int l = maleEquip2;
		int i1 = maleEmblem;
		if (j == 1) {
			k = femaleEquip1;
			l = femaleEquip2;
			i1 = femaleEmblem;
		}
		if (k == -1) {
			return true;
		}
		boolean flag = true;
		if (!Model.method463(k)) {
			flag = false;
		}
		if (l != -1 && !Model.method463(l)) {
			flag = false;
		}
		if (i1 != -1 && !Model.method463(i1)) {
			flag = false;
		}
		return flag;
	}

	public Model method196(int i) {
		int j = maleEquip1;
		int k = maleEquip2;
		int l = maleEmblem;
		if (i == 1) {
			j = femaleEquip1;
			k = femaleEquip2;
			l = femaleEmblem;
		}
		if (j == -1) {
			return null;
		}
		Model model = Model.method462(j);
		if (k != -1) {
			if (l != -1) {
				Model model_1 = Model.method462(k);
				Model model_3 = Model.method462(l);
				Model aclass30_sub2_sub4_sub6_1s[] = { model, model_1, model_3 };
				model = new Model(3, aclass30_sub2_sub4_sub6_1s);
			} else {
				Model model_2 = Model.method462(k);
				Model aclass30_sub2_sub4_sub6s[] = { model, model_2 };
				model = new Model(2, aclass30_sub2_sub4_sub6s);
			}
		}
		if (i == 0 && maleEquipOffset != 0) {
			model.method475(0, maleEquipOffset, 0);
		}
		if (i == 1 && femaleEquipOffset != 0) {
			model.method475(0, femaleEquipOffset, 0);
		}
		if (originalColors != null) {
			for (int i1 = 0; i1 < originalColors.length; i1++) {
				model.replaceColor(originalColors[i1], newColors[i1]);
			}

		}
		return model;
	}

	private void setDefaults() {
		modelID = 0;
		name = null;
		description = null;
		originalColors = null;
		newColors = null;
		zoom = 2000;
		rotationX = 0;
		rotationY = 0;
		rotationZ = 0;
		offsetX = 0;
		offsetY = 0;
		stackable = false;
		value = 1;
		membersObject = false;
		groundOptions = null;
		options = null;
		maleEquip1 = -1;
		maleEquip2 = -1;
		maleEquipOffset = 0;
		femaleEquip1 = -1;
		femaleEquip2 = -1;
		femaleEquipOffset = 0;
		maleEmblem = -1;
		femaleEmblem = -1;
		maleChatHead = -1;
		maleChatHeadHat = -1;
		femaleChatHead = -1;
		femaleChatHeadHat = -1;
		stacks = null;
		stackAmounts = null;
		note = -1;
		noteTemplate = -1;
		width = 128;
		height = 128;
		depth = 128;
		brightness = 0;
		contrast = 0;
		team = 0;
		searchableItem = false;
	}

	public static String itemModels(int itemID) {
		int inv = forID(itemID).modelID;
		int male = forID(itemID).maleEquip1;
		int female = forID(itemID).femaleEquip1;
		String name = forID(itemID).name;
		return "<col=225>" + name + "</col> (<col=800000000>" + itemID + "</col>) - [inv: <col=800000000>" + inv + "</col>] - [male: <col=800000000>" + male + "</col>] - [female: <col=800000000>" + female + "</col>]";
	}

	private void toNote() {
		ItemDefinitions itemDef = forID(noteTemplate);
		modelID = itemDef.modelID;
		zoom = itemDef.zoom;
		rotationX = itemDef.rotationX;
		rotationY = itemDef.rotationY;

		rotationZ = itemDef.rotationZ;
		offsetX = itemDef.offsetX;
		offsetY = itemDef.offsetY;
		originalColors = itemDef.originalColors;
		newColors = itemDef.newColors;
		ItemDefinitions itemDef_1 = forID(note);
		name = itemDef_1.name;
		membersObject = itemDef_1.membersObject;
		value = itemDef_1.value;
		String s = "a";
		char c = itemDef_1.name.charAt(0);
		if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
			s = "an";
		}
		description = ("Swap this note at any bank for " + s + " " + itemDef_1.name + ".");
		stackable = true;
	}

	public static Sprite getSprite(int i, int j, int k) {
		if (k == 0) {
			Sprite sprite = (Sprite) mruNodes1.insertFromCache(i);
			if (sprite != null && sprite.maxHeight != j && sprite.maxHeight != -1) {
				sprite.unlink();
				sprite = null;
			}
			if (sprite != null) {
				return sprite;
			}
		}
		ItemDefinitions itemDef = forID(i);
		if (itemDef.stacks == null) {
			j = -1;
		}
		if (j > 1) {
			int i1 = -1;
			for (int j1 = 0; j1 < 10; j1++) {
				if (j >= itemDef.stackAmounts[j1] && itemDef.stackAmounts[j1] != 0) {
					i1 = itemDef.stacks[j1];
				}
			}

			if (i1 != -1) {
				itemDef = forID(i1);
			}
		}
		Model model = itemDef.method201(1);
		if (model == null) {
			return null;
		}
		Sprite sprite = null;
		if (itemDef.noteTemplate != -1) {
			sprite = getSprite(itemDef.note, 10, -1);
			if (sprite == null) {
				return null;
			}
		}
		Sprite enabledSprite = new Sprite(32, 32);
		int k1 = Texture.textureInt1;
		int l1 = Texture.textureInt2;
		int ai[] = Texture.anIntArray1472;
		int ai1[] = DrawingArea.pixels;
		int i2 = DrawingArea.width;
		int j2 = DrawingArea.height;
		int k2 = DrawingArea.topX;
		int l2 = DrawingArea.bottomX;
		int i3 = DrawingArea.topY;
		int j3 = DrawingArea.bottomY;
		Texture.aBoolean1464 = false;
		DrawingArea.initDrawingArea(32, 32, enabledSprite.myPixels);
		DrawingArea.method336(32, 0, 0, 0, 32);
		Texture.method364();
		int k3 = itemDef.zoom;
		if (k == -1) {
			k3 = (int) (k3 * 1.5D);
		}
		if (k > 0) {
			k3 = (int) (k3 * 1.04D);
		}
		int l3 = Texture.anIntArray1470[itemDef.rotationX] * k3 >> 16;
		int i4 = Texture.anIntArray1471[itemDef.rotationX] * k3 >> 16;
		model.method482(itemDef.rotationY, itemDef.rotationZ, itemDef.rotationX, itemDef.offsetX, l3 + model.modelHeight / 2 + itemDef.offsetY, i4 + itemDef.offsetY);
		for (int i5 = 31; i5 >= 0; i5--) {
			for (int j4 = 31; j4 >= 0; j4--) {
				if (enabledSprite.myPixels[i5 + j4 * 32] == 0) {
					if (i5 > 0 && enabledSprite.myPixels[i5 - 1 + j4 * 32] > 1) {
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					} else if (j4 > 0 && enabledSprite.myPixels[i5 + (j4 - 1) * 32] > 1) {
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					} else if (i5 < 31 && enabledSprite.myPixels[i5 + 1 + j4 * 32] > 1) {
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					} else if (j4 < 31 && enabledSprite.myPixels[i5 + (j4 + 1) * 32] > 1) {
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					}
				}
			}

		}

		if (k > 0) {
			for (int j5 = 31; j5 >= 0; j5--) {
				for (int k4 = 31; k4 >= 0; k4--) {
					if (enabledSprite.myPixels[j5 + k4 * 32] == 0) {
						if (j5 > 0 && enabledSprite.myPixels[j5 - 1 + k4 * 32] == 1) {
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						} else if (k4 > 0 && enabledSprite.myPixels[j5 + (k4 - 1) * 32] == 1) {
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						} else if (j5 < 31 && enabledSprite.myPixels[j5 + 1 + k4 * 32] == 1) {
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						} else if (k4 < 31 && enabledSprite.myPixels[j5 + (k4 + 1) * 32] == 1) {
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						}
					}
				}

			}

		} else if (k == 0) {
			for (int k5 = 31; k5 >= 0; k5--) {
				for (int l4 = 31; l4 >= 0; l4--) {
					if (enabledSprite.myPixels[k5 + l4 * 32] == 0 && k5 > 0 && l4 > 0 && enabledSprite.myPixels[k5 - 1 + (l4 - 1) * 32] > 0) {
						enabledSprite.myPixels[k5 + l4 * 32] = 0x302020;
					}
				}

			}

		}
		if (itemDef.noteTemplate != -1) {
			int l5 = sprite.maxWidth;
			int j6 = sprite.maxHeight;
			sprite.maxWidth = 32;
			sprite.maxHeight = 32;
			sprite.drawSprite(0, 0);
			sprite.maxWidth = l5;
			sprite.maxHeight = j6;
		}
		if (k == 0) {
			mruNodes1.removeFromCache(enabledSprite, i);
		}
		DrawingArea.initDrawingArea(j2, i2, ai1);
		DrawingArea.setDrawingArea(j3, k2, l2, i3);
		Texture.textureInt1 = k1;
		Texture.textureInt2 = l1;
		Texture.anIntArray1472 = ai;
		Texture.aBoolean1464 = true;
		if (itemDef.stackable) {
			enabledSprite.maxWidth = 33;
		} else {
			enabledSprite.maxWidth = 32;
		}
		enabledSprite.maxHeight = j;
		return enabledSprite;
	}
	
	public static Sprite getSmallSprite(int itemId) {
		ItemDefinitions itemDef = forID(itemId);
		Model model = itemDef.method201(1);
		if (model == null) {
			return null;
		}
		Sprite sprite1 = null;
		if (itemDef.noteTemplate != -1) {
			sprite1 = getSprite(itemDef.note, 10, -1);
			if (sprite1 == null) {
				return null;
			}
		}
		Sprite enabledSprite = new Sprite(18, 18);
		int k1 = Texture.textureInt1;
		int l1 = Texture.textureInt2;
		int ai[] = Texture.anIntArray1472;
		int ai1[] = DrawingArea.pixels;
		int i2 = DrawingArea.width;
		int j2 = DrawingArea.height;
		int k2 = DrawingArea.topX;
		int l2 = DrawingArea.bottomX;
		int i3 = DrawingArea.topY;
		int j3 = DrawingArea.bottomY;
		Texture.aBoolean1464 = false;
		DrawingArea.initDrawingArea(18, 18, enabledSprite.myPixels);
		DrawingArea.method336(18, 0, 0, 0, 18);
		Texture.method364();
		int k3 = (int) (itemDef.zoom * 1.6D);
		int l3 = Texture.anIntArray1470[itemDef.rotationX] * k3 >> 16;
		int i4 = Texture.anIntArray1471[itemDef.rotationX] * k3 >> 16;
		model.method482(itemDef.rotationY, itemDef.rotationZ, itemDef.rotationX, itemDef.offsetX, l3 + model.modelHeight / 2 + itemDef.offsetY, i4 + itemDef.offsetY);
		if (itemDef.noteTemplate != -1) {
			int l5 = sprite1.maxWidth;
			int j6 = sprite1.maxHeight;
			sprite1.maxWidth = 18;
			sprite1.maxHeight = 18;
			sprite1.drawSprite(0, 0);
			sprite1.maxWidth = l5;
			sprite1.maxHeight = j6;
		}
		DrawingArea.initDrawingArea(j2, i2, ai1);
		DrawingArea.setDrawingArea(j3, k2, l2, i3);
		Texture.textureInt1 = k1;
		Texture.textureInt2 = l1;
		Texture.anIntArray1472 = ai;
		Texture.aBoolean1464 = true;
		
		enabledSprite.maxWidth = 18;
		enabledSprite.maxHeight = 18;
		
		return enabledSprite;
	}

	public Model method201(int i) {
		if (stacks != null && i > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++) {
				if (i >= stackAmounts[k] && stackAmounts[k] != 0) {
					j = stacks[k];
				}
			}

			if (j != -1) {
				return forID(j).method201(1);
			}
		}
		Model model = (Model) mruNodes2.insertFromCache(id);
		if (model != null) {
			return model;
		}
		model = Model.method462(modelID);
		if (model == null) {
			return null;
		}
		if (width != 128 || height != 128 || depth != 128) {
			model.method478(width, depth, height);
		}
		if (originalColors != null) {
			for (int l = 0; l < originalColors.length; l++) {
				model.replaceColor(originalColors[l], newColors[l]);
			}

		}
		model.method479(64 + brightness, 768 + contrast, -50, -10, -50, true);
		model.aBoolean1659 = true;
		mruNodes2.removeFromCache(model, id);
		return model;
	}

	public Model method202(int i) {
		if (stacks != null && i > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++) {
				if (i >= stackAmounts[k] && stackAmounts[k] != 0) {
					j = stacks[k];
				}
			}

			if (j != -1) {
				return forID(j).method202(1);
			}
		}
		Model model = Model.method462(modelID);
		if (model == null) {
			return null;
		}
		if (originalColors != null) {
			for (int l = 0; l < originalColors.length; l++) {
				model.replaceColor(originalColors[l], newColors[l]);
			}

		}
		return model;
	}
	
	public static void dumpList() {
		try {
			File file = new File("OSRS Item List.txt");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for (int i = 0; i < totalItems; i++) {
					ItemDefinitions definition = forID(i);
					if (definition != null) {
						writer.write(definition.id + "\t\t" + definition.name + "\t\t" + definition.description  + "\t\t" + definition.note);
						writer.newLine();
					}
				}
			}
			
			System.out.println("Finished dumping items definitions.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void dumpStackableList() {
		try {
			File file = new File("stackables.dat");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for (int i = 0; i < totalItems; i++) {
					ItemDefinitions definition = forID(i);
					if (definition != null) {
						writer.write(definition.id + "\t" + definition.stackable);
						writer.newLine();
					} else {
						writer.write(i + "\tfalse");
						writer.newLine();
					}
				}
			}
			
			System.out.println("Finished dumping noted items definitions.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void dumpNotableList() {
		try {
			File file = new File("note_id.dat");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for (int i = 0; i < totalItems; i++) {
					ItemDefinitions definition = forID(i);
					if (definition != null) {
						writer.write(definition.id + "\t" + definition.note);
						writer.newLine();
					} else {
						writer.write(i + "\t-1");
						writer.newLine();
					}
				}
			}
			
			System.out.println("Finished dumping noted items definitions.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ItemDefinitions() {
		id = -1;
	}

	public byte femaleEquipOffset;
	public int value;// anInt155
	public int[] originalColors;// newModelColor
	public int id;// anInt157
	public static MRUNodes mruNodes1 = new MRUNodes(100);
	public static MRUNodes mruNodes2 = new MRUNodes(50);
	public int[] newColors;
	public boolean membersObject;// aBoolean161
	public int femaleEmblem;
	public int noteTemplate;
	public int femaleEquip2;// femArmModel
	public int maleEquip1;// maleWieldModel
	public int maleChatHeadHat;
	public int width;
	public String groundOptions[];
	public int offsetX;
	public String name;// itemName
	public static ItemDefinitions[] cache;
	public int femaleChatHeadHat;
	public int modelID;// dropModel
	public int maleChatHead;
	public boolean stackable;// itemStackable
	public String description;// itemExamine
	public int note;
	public static int cacheIndex;
	public int zoom;
	public static boolean isMembers = true;
	public static Stream stream;
	public int contrast;
	public int maleEmblem;
	public int maleEquip2;// maleArmModel
	public String options[];// itemMenuOption
	public int rotationX;// modelRotateUp
	public int depth;
	public int height;
	public int[] stacks;// modelStack
	public int offsetY;//
	public static int[] streamIndices;
	public int brightness;
	public int femaleChatHead;
	public int rotationY;// modelRotateRight
	public int femaleEquip1;// femWieldModel
	public int[] stackAmounts;// itemAmount
	public int team;
	public static int totalItems;
	public int rotationZ;// modelPositionUp
	public byte maleEquipOffset;
	public boolean searchableItem;

}
