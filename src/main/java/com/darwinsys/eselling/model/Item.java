package com.darwinsys.eselling.model;

import jakarta.persistence.*;
import org.wildfly.common.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.darwinsys.eselling.model.Constants.Condition;

@Entity
public class Item {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id = 0L;

	@NotNull
	private String name = "";

	@NotNull
	@Column(length = 4096)
	private String description = "";
	@Enumerated(EnumType.STRING)
	private Condition condition;
	List<String> urls;
	private Double askingPrice = 0d;
	private Double soldPrice = 0d;
	String category;
    String conditionQualification;
    int quantity = 1;
    List<String> photos;

	@SuppressWarnings("unused") // JPA
	private Long getId() {
		return id;
	}
	@SuppressWarnings("unused") // JPA
	private void setId(Long id) {
		this.id = id;
	}

	public Item(Long id, String name, String description,
				List<String> urls,
				Double askingPrice, Double soldPrice) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.urls = urls;
		this.askingPrice = askingPrice;
		this.soldPrice = soldPrice;
	}

	@SuppressWarnings("unused") // JPA
	public Item() {
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

	public Double getSoldPrice() {
		return soldPrice;
	}
	public void setSoldPrice(Double soldPrice) {
		this.soldPrice = soldPrice;
	}

	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
    }

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
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

}
