package com.summerproject.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.summerproject.spring.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
	Category findByCategoryNameAndMarketId(String categoryName, int marketId);

}
