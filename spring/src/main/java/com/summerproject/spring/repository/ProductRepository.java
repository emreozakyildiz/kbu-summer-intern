package com.summerproject.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.summerproject.spring.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByProductNameAndMarketId(String productName, int marketId);
}