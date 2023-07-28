package com.summerproject.spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sub_categories")
public class SubCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long subCategoryId;
	long parentCategoryId;
	String subCategoryName;
	String subCategoryLink;
	int pages;
	int marketId;
	
	public SubCategory() {
		
	}
	
	public SubCategory(long subCategoryId, long parentCategoryId, String subCategoryName, String subCategoryLink,int pages, int marketId) {
		super();
		this.subCategoryId = subCategoryId;
		this.parentCategoryId = parentCategoryId;
		this.subCategoryName = subCategoryName;
		this.subCategoryLink = subCategoryLink;
		this.pages = pages;
		this.marketId = marketId;
	}

	public long getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(long subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public long getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public String getSubCategoryLink() {
		return subCategoryLink;
	}

	public void setSubCategoryLink(String subCategoryLink) {
		this.subCategoryLink = subCategoryLink;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}
}