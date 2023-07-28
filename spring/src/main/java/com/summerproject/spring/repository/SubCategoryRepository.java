package com.summerproject.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.summerproject.spring.model.SubCategory;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
	SubCategory findBySubCategoryNameAndMarketId(String subCategoryName, int marketId);
}
