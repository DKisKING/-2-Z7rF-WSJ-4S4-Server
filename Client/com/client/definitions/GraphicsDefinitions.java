package com.client.definitions;

import com.client.MRUNodes;
import com.client.Model;
import com.client.Stream;
import com.client.StreamLoader;

public final class GraphicsDefinitions {

	public static void unpackConfig(StreamLoader streamLoader) {
		Stream stream = new Stream(streamLoader.getDataForName("spotanim.dat"));
		int length = stream.readUnsignedWord();
		if (cache == null)
			cache = new GraphicsDefinitions[length + 1];
		System.out.println(length + " graphics loaded.");
		for (int j = 0; j < length; j++) {
			if (cache[j] == null)
				cache[j] = new GraphicsDefinitions();
			cache[j].index = j;
			cache[j].setDefault();
			cache[j].readValues(stream);
		}
		cache[1283] = new GraphicsDefinitions();
		cache[1283].index = 1283;
		cache[1283].modelId = 44811;
		cache[1283].anInt406 = 7155;
		cache[1283].aAnimation_407 = AnimationDefinitions.anims[cache[1283].anInt406];
	}

	private void readValues(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0) {
				return;
			}
			if (i == 1) {
				modelId = stream.readUnsignedWord();
			} else if (i == 2) {
				anInt406 = stream.readUnsignedWord();
				if (AnimationDefinitions.anims != null) {
					aAnimation_407 = AnimationDefinitions.anims[anInt406];
				}
			} else if (i == 4) {
				anInt410 = stream.readUnsignedWord();
			} else if (i == 5) {
				anInt411 = stream.readUnsignedWord();
			} else if (i == 6) {
				anInt412 = stream.readUnsignedWord();
			} else if (i == 7) {
				anInt413 = stream.readUnsignedWord();
			} else if (i == 8) {
				anInt414 = stream.readUnsignedWord();
			} else if (i == 40) {
				int j = stream.readUnsignedByte();
				for (int k = 0; k < j; k++) {
					anIntArray408[k] = stream.readUnsignedWord();
					anIntArray409[k] = stream.readUnsignedWord();
				}
			} else {
				System.out.println("Error unrecognised spotanim config code: " + i);
			}
		} while (true);
	}
	
	public static GraphicsDefinitions fetch(int modelId) {
		for (GraphicsDefinitions anim : cache) {
			if (anim == null) {
				continue;
			}
			if (anim.modelId == modelId) {
				return anim;
			}
		}
		return null;
	}

	public Model getModel() {
		Model model = (Model) aMRUNodes_415.insertFromCache(index);
		if (model != null)
			return model;
		model = Model.method462(modelId);
		if (model == null)
			return null;
		for (int i = 0; i < anIntArray408.length; i++)
			if (anIntArray408[0] != 0) //default frame id
				model.replaceColor(anIntArray408[i], anIntArray409[i]);

		aMRUNodes_415.removeFromCache(model, index);
		return model;
	}
	
	private void setDefault() {
		modelId = -1;
		anInt406 = -1;
		anIntArray408 = new int[6];
		anIntArray409 = new int[6];
		anInt410 = 128;
		anInt411 = 128;
		anInt412 = 0;
		anInt413 = 0;
		anInt414 = 0;
	}

	public GraphicsDefinitions() {
		anInt400 = 9;
		anInt406 = -1;
		anIntArray408 = new int[6];
		anIntArray409 = new int[6];
		anInt410 = 128;
		anInt411 = 128;
	}
	
	public int getModelId() {
		return modelId;
	}
	
	public int getIndex() {
		return index;
	}

	public final int anInt400;
	public static GraphicsDefinitions cache[];
	private int index;
	private int modelId;
	public int anInt406;
	public AnimationDefinitions aAnimation_407;
	public int[] anIntArray408;
	public int[] anIntArray409;
	public int anInt410;
	public int anInt411;
	public int anInt412;
	public int anInt413;
	public int anInt414;
	public static MRUNodes aMRUNodes_415 = new MRUNodes(30);

}
