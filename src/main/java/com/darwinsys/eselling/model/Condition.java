package com.darwinsys.eselling.model;

public enum Condition {
	NEW("New"),
	LIKE_NEW("Like new"),
	USED("Used, serviceable"),
	FOR_PARTS("For Parts Only");

	private final String displayName;

	Condition(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

	public static Condition getDefault() {
		return USED;
	}
}
