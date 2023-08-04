package com.internship.crawler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.internship.crawler.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByProductNameAndMarketId(String productName, int marketId);
	List<Product> findAllByMarketId(int marketId);
}