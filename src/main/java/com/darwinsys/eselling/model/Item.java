package com.darwinsys.eselling.model;

import com.darwinsys.eselling.listing.MarketName;
import jakarta.persistence.*;
import org.wildfly.common.annotation.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.darwinsys.eselling.model.Condition;

@Entity
public class Item {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id = 0L;

	@NotNull
	private String name = "";

	@NotNull
	@Column(length = 4096)
	private String description = "";
	private boolean active = true;
	@Enumerated(EnumType.STRING)
	private Condition condition;
	List<String> urls = new ArrayList<>();
	private Double askingPrice = 0d;
	@Enumerated(EnumType.STRING)
	Category category;
    String conditionQualification;
    int quantity = 1;
    List<String> photos;

	// Fields specific to EBAY
	private String ebaySku; // Stores the eBay Inventory Item SKU
	private String ebayOfferId; // Stores the eBay Offer ID
	private String ebayListingId; // Stores the final eBay Listing ID (returned after publishing the offer)


	@SuppressWarnings("unused") // JPA
    public Long getId() {
		return id;
	}
	@SuppressWarnings("unused") // JPA
	private void setId(Long id) {
		this.id = id;
	}

	public Item(Long id, String name, String description,
				List<String> urls,
				Double askingPrice) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.urls = urls;
		this.askingPrice = askingPrice;
	}

	public Item(long id, String name, String description, double price,
				Category category,
				Condition condition, String imageURL) {
		this(id, name, description, null, price);
		this.category = category;
		this.condition = condition;
		urls = urlsEmpty();
	}

	@SuppressWarnings("unused") // JPA
	public Item() {
		urls = urlsEmpty();
	}

	private List<String> urlsEmpty() {
		List<String> urls = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			urls.add("");
		}
		return urls;
	}

	@Transient
	public String getUrl(MarketName market) {
		int i = market.ordinal();
		if (i > urls.size() - 1) {
			throw new IndexOutOfBoundsException();
		}
		return urls.get(i);
	}
	public void setUrl(MarketName market, String url) {
		urls.set(market.ordinal(), url);
	}

	@Transient
	public boolean isListed() {
		for (String url : urls) {
			if (url != null && !url.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	public void setListed(boolean useless) {
		throw new UnsupportedOperationException("isListed is a derived value");
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Double getAskingPrice() {
		return askingPrice;
	}
	public void setAskingPrice(Double askingPrice) {
		this.askingPrice = askingPrice;
	}

	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
    }

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getConditionQualification() {
		return conditionQualification;
	}

	public void setConditionQualification(String conditionQualification) {
		this.conditionQualification = conditionQualification;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}

    public Boolean getActive() {
		return active;
    }
	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getEbaySku() {
		return ebaySku;
	}

	public void setEbaySku(String ebaySku) {
		this.ebaySku = ebaySku;
	}

	public String getEbayOfferId() {
		return ebayOfferId;
	}

	public void setEbayOfferId(String ebayOfferId) {
		this.ebayOfferId = ebayOfferId;
	}

	public String getEbayListingId() {
		return ebayListingId;
	}

	public void setEbayListingId(String ebayListingId) {
		this.ebayListingId = ebayListingId;
	}
}
