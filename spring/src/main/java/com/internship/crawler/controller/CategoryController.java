package com.internship.crawler.controller;

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
import com.internship.crawler.service.CategoryScraperService;
import com.internship.crawler.service.CategoryService;
import com.internship.crawler.service.ProductService;

@RestController
@RequestMapping("/api")
public class CategoryController {
	private final CategoryService categoryService;
	private final ProductService productService;
	private final CategoryScraperService categoryScraperService;

	public CategoryController(CategoryService categoryService, ProductService productService, CategoryScraperService categoryScraperService) {
		this.categoryService = categoryService;
		this.productService = productService;
		this.categoryScraperService = categoryScraperService;
	}

	@PostMapping("/add")
	public ResponseEntity<Category> addCategory(@RequestBody Category category) {
		Category newCategory = categoryService.addCategory(category);
		return new ResponseEntity<Category>(newCategory, HttpStatus.CREATED);
		
	}
	
	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getAllCategories(){
		categoryService.addCategories(categoryScraperService.scrapeCategoriesFromTrendyol());
		//categoryService.addCategories(categoryScraperService.scrapeCategoriesFromMigros());
		categoryService.addCategories(categoryScraperService.scrapeCategoriesFromA101());
		List<Category> categories = categoryService.getAllCategories();
		return new ResponseEntity<List<Category>>(categories, HttpStatus.OK);
	}
	
	@GetMapping("/sub-categories")
	public ResponseEntity<List<SubCategory>> getAllSubCategories(){
		categoryService.addSubCategories(categoryScraperService.scrapeSubCategoriesFromTrendyol());
		categoryService.addSubCategories(categoryScraperService.scrapeSubCategoriesFromA101());
		List<SubCategory> subCategories = categoryService.getAllSubCategories();
		return new ResponseEntity<List<SubCategory>>(subCategories, HttpStatus.OK);
	}
	
	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts(){
		productService.addProducts(categoryScraperService.scrapeProductsFromTrendyol());
		//productService.addProducts(categoryScraperService.scrapeProductsFromA101());
		List<Product> products = productService.getProducts();
		return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
	}
}
