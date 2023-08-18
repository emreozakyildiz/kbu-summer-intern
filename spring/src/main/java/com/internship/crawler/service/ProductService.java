package com.internship.crawler.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.internship.crawler.model.Product;
import com.internship.crawler.model.SubCategory;
import com.internship.crawler.repository.ProductRepository;

@Service
public class ProductService {
	ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	public List<Product> addProducts(List<Product> products, SubCategory subCategory) {
		for (Product product : products) {
			product.setSubCategory(subCategory);
		}
		return productRepository.saveAll(products);
	}

	public List<Product> getProducts() {
		return productRepository.findAll();
	}

	public List<Product> getProductsByMarket(int marketId) {
		return productRepository.findAllByMarketId(marketId);
	}

	public boolean productExists(String productName, int marketId) {
		long count = productRepository.countByProductNameAndMarketId(productName, marketId);
	    return count > 0;
	}
}
