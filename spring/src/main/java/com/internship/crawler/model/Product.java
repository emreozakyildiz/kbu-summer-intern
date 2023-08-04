package com.internship.crawler.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long productId;
	long subCategoryId;
	long categoryId;
	long marketProductId;
	String productName;
	double productPrice;
	String imageUrl;
	String productUrl;
	int marketId;
	
	public Product() {
		
	}

	public Product(long productId,long subCategoryId, long categoryId, long marketProductId, String productName, double productPrice, String imageUrl,
			String productUrl, int marketId) {
		super();
		this.productId = productId;
		this.subCategoryId = subCategoryId;
		this.categoryId = categoryId;
		this.marketProductId = marketProductId;
		this.productName = productName;
		this.productPrice = productPrice;
		this.imageUrl = imageUrl;
		this.productUrl = productUrl;
		this.marketId = marketId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public long getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(long subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public long getMarketProductId() {
		return marketProductId;
	}

	public void setMarketProductId(long marketProductId) {
		this.marketProductId = marketProductId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}
	
	

}
