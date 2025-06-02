package com.darwinsys.eselling.model;

/// This enum is stored as strings in the db so it's OK to keep alphabetical when adding.
///
public enum Category {
	Antiques,
	Artwork,
	Automotive,
	Books,
	Camping,
	ComputersElectronics,
	Furniture,
	Household,
	MusicalInstruments,
	Photography,
	SportingGoods,
	Tools;

	/** Convert names like SportingGoods to printable "Sporting Goods" */
	public String toString() {
		return name().replaceAll("([a-z])([A-Z])", "$1 $2");
	}
}
