package com.internship.crawler.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.internship.crawler.model.Category;
import com.internship.crawler.model.SubCategory;
import com.internship.crawler.repository.CategoryRepository;
import com.internship.crawler.repository.SubCategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final SubCategoryRepository subCategoryRepository;

	public CategoryService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
		this.categoryRepository = categoryRepository;
		this.subCategoryRepository = subCategoryRepository;
	}

	public Category addCategory(Category category) {
		return categoryRepository.save(category);
	}

	public List<Category> addCategories(List<Category> categories) {
		return categoryRepository.saveAll(categories);
	}

	public List<SubCategory> addSubCategories(List<SubCategory> subCategories) {
		return subCategoryRepository.saveAll(subCategories);
	}

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public List<Category> getAllCategoriesByMarket(int marketId) {
		return categoryRepository.findAllByMarketId(marketId);
	}

	public List<SubCategory> getAllSubCategories() {
		return subCategoryRepository.findAll();
	}

	public List<SubCategory> getAllSubCategoriesByMarket(int marketId) {
		return subCategoryRepository.findAllByMarketId(marketId);
	}

	public boolean categoryExists(String categoryName, int marketId) {
		Category existingCategory = categoryRepository.findByCategoryNameAndMarketId(categoryName, marketId);
		return existingCategory != null;
	}

	public boolean subCategoryExists(String subCategoryName, int marketId) {
		SubCategory existingSubCategory = subCategoryRepository.findBySubCategoryNameAndMarketId(subCategoryName,
				marketId);
		return existingSubCategory != null;
	}
	
	/*
	 * public boolean canceledSubCategoryLinkExists(String subCategoryLink, int
	 * marketId) { SubCategory existingSubCategory =
	 * subCategoryRepository.findBySubCategoryLinkAndMarketId(subCategoryLink,
	 * marketId); return existingSubCategory != null; }
	 */
	
	public boolean subCategoryLinkExists(String subCategoryLink, int marketId) {
	    List<SubCategory> matchingSubCategories = subCategoryRepository.findBySubCategoryLinkAndMarketId(subCategoryLink, marketId);
	    return !matchingSubCategories.isEmpty();
	}

}
