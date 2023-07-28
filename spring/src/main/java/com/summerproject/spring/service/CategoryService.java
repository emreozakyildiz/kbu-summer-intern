package com.summerproject.spring.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.summerproject.spring.model.Category;
import com.summerproject.spring.model.SubCategory;
import com.summerproject.spring.repository.CategoryRepository;
import com.summerproject.spring.repository.SubCategoryRepository;

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
	
	public List<Category> addCategories(List<Category> categories){
		return categoryRepository.saveAll(categories);
	}
	
	public List<SubCategory> addSubCategories(List<SubCategory> subCategories){
		return subCategoryRepository.saveAll(subCategories);
	}
	
	public List<Category> getAllCategories(){
		return categoryRepository.findAll();
	}
	
	public List<SubCategory> getAllSubCategories(){
		return subCategoryRepository.findAll();
	}
	
	public boolean categoryExists(String categoryName, int marketId) {
        Category existingCategory = categoryRepository.findByCategoryNameAndMarketId(categoryName, marketId);
        return existingCategory != null;
    }
	
	public boolean subCategoryExists(String subCategoryName, int marketId) {
		SubCategory existingSubCategory = subCategoryRepository.findBySubCategoryNameAndMarketId(subCategoryName, marketId);
		return existingSubCategory != null;
	}

}
