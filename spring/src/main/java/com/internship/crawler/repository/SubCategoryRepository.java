package com.internship.crawler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.internship.crawler.model.SubCategory;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
	SubCategory findBySubCategoryNameAndMarketId(String subCategoryName, int marketId);

	List<SubCategory> findAllByMarketId(int marketId);
}
