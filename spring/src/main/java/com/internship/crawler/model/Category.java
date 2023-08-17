package com.internship.crawler.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long categoryId;
	String categoryName;
	String categoryLink;
	int marketId;
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<SubCategory> subCategories = new ArrayList<>();

	public Category() {

	}

	public Category(long categoryId, String categoryName, String categoryLink, int marketId) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.categoryLink = categoryLink;
		this.marketId = marketId;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryLink() {
		return categoryLink;
	}

	public void setCategoryLink(String categoryLink) {
		this.categoryLink = categoryLink;
	}

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}

}
