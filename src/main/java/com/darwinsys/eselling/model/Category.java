package com.darwinsys.eselling.model;

public record Category(String name, String fbCategory, int eBayCategory) {
	@Override
	public String toString() {
		return name;
	}
}

