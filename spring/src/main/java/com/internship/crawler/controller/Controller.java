
package com.internship.crawler.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.internship.crawler.model.Category;
import com.internship.crawler.model.Product;
import com.internship.crawler.model.SubCategory;
import com.internship.crawler.service.ScraperService;
import com.internship.crawler.service.CategoryService;
import com.internship.crawler.service.ProductService;

@RestController
@RequestMapping("/api")
public class Controller {
	private final CategoryService categoryService;
	private final ProductService productService;
	private final ScraperService scraperService;

	public Controller(CategoryService categoryService, ProductService productService, ScraperService scraperService) {
		this.categoryService = categoryService;
		this.productService = productService;
		this.scraperService = scraperService;
	}

	@PostMapping("/scrape/categories")
	public ResponseEntity<List<Category>> scrapeCategories() {
		List<Category> categories = new ArrayList<Category>();
		// categories.addAll(scraperService.scrapeCategoriesFromMigros());
		// categories.addAll(scraperService.scrapeCategoriesFromTrendyol());
		categories.addAll(scraperService.scrapeCategoriesFromA101());
		List<Category> savedCategories = categoryService.addCategories(categories);
		return new ResponseEntity<>(savedCategories, HttpStatus.CREATED);
	}

	@PostMapping("/scrape/sub-categories")
	public ResponseEntity<List<SubCategory>> scrapeSubCategories() {
		List<SubCategory> subCategories = new ArrayList<SubCategory>();
		for (Category category : categoryService.getAllCategories()) {
			int market = category.getMarketId();
			switch (market) {
			case 1: {
				subCategories.addAll(scraperService.scrapeSubCategoriesFromA101(category));
				break;
			}
			case 2: {
				// subCategories.addAll(scraperService.scrapeSubCategoriesFromMigros(category));
			}
			case 3: {
				// subCategories.addAll(scraperService.scrapeSubCategoriesFromTrendyol(category));
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + category.getMarketId());
			}
		}
		List<SubCategory> savedSubCategories = categoryService.addSubCategories(subCategories);
		return new ResponseEntity<>(savedSubCategories, HttpStatus.CREATED);
	}

	@PostMapping("/scrape/products")
	public ResponseEntity<List<Product>> scrapeProducts() {
		List<Product> products = new ArrayList<Product>();
		List<Product> savedProducts = new ArrayList<Product>();
		for (SubCategory subCategory : categoryService.getAllSubCategories()) {
			products.addAll(scraperService.scrapeProductsFromA101(subCategory));
			// products.addAll(scraperService.scrapeProductsFromMigros(subCategory));
			// products.addAll(scraperService.scrapeProductsFromTrendyol(subCategory));
			savedProducts.addAll(productService.addProducts(products, subCategory));
		}
		return new ResponseEntity<>(savedProducts, HttpStatus.CREATED);
	}

	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> categories = categoryService.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@GetMapping("/sub-categories")
	public ResponseEntity<List<SubCategory>> getAllSubCategories() {
		List<SubCategory> subCategories = categoryService.getAllSubCategories();
		return new ResponseEntity<>(subCategories, HttpStatus.OK);
	}

	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> products = productService.getProducts();
		return new ResponseEntity<>(products, HttpStatus.OK);
	}
}