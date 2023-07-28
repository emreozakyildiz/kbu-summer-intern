package com.summerproject.spring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.summerproject.spring.model.Product;
import com.summerproject.spring.repository.ProductRepository;

@Service
public class ProductService {
	ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	public Product addProduct(Product product) {
		return productRepository.save(product);
	}
	
	public List<Product> addProducts (List<Product> products){
		return productRepository.saveAll(products);
	}
	
	public List<Product> getProducts(){
		return productRepository.findAll();
	}
	
	public boolean productExists(String productName, int marketId) {
		Optional<Product> existingProduct = productRepository.findByProductNameAndMarketId(productName, marketId);
		return existingProduct.isPresent();
	}
}
