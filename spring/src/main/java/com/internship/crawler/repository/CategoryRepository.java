package com.internship.crawler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.internship.crawler.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category findByCategoryNameAndMarketId(String categoryName, int marketId);

	List<Category> findAllByMarketId(int marketId);

}
