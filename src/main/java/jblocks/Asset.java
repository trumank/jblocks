package jblocks;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class Asset {
	String name;
	String hash;

	public Asset(JsonNode config) {

	}

	public String getName() {
		return name;
	}

	public String getHash() {
		return hash;
	}

	public void loadAsset() throws IOException {

	}
}
