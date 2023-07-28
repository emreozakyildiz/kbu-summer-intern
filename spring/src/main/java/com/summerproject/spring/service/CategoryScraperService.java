package com.summerproject.spring.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.summerproject.spring.model.Category;
import com.summerproject.spring.model.Product;
import com.summerproject.spring.model.SubCategory;

@Service
public class CategoryScraperService {
	private final CategoryService categoryService;
	private final ProductService productService;

	public CategoryScraperService(CategoryService categoryService, ProductService productService) {
		this.categoryService = categoryService;
		this.productService = productService;
	}

	public List<Category> scrapeCategoriesFromA101() {

		final String url = "https://www.a101.com.tr";
		final int marketId = 1;
		List<Category> categories = new ArrayList<Category>();

		try {
			Document document = Jsoup.connect(url).get();
			Elements categoryElements = document.select("ul.desktop-menu > li.js-navigation-item > a");

			for (Element categoryElement : categoryElements) {
				String categoryName = categoryElement.select("a").text();
				String categoryLink = categoryElement.select("a").attr("href");

				if (categoryService.categoryExists(categoryName, marketId))
					continue;

				Category category = new Category();
				category.setCategoryName(categoryName);
				category.setCategoryLink(url + categoryLink);
				category.setMarketId(marketId);

				categories.add(category);

			}
		} catch (IOException e) {
			// Handle exception appropriately
			e.printStackTrace();
		}

		return categories;
	}

	public List<SubCategory> scrapeSubCategoriesFromA101() {

		List<Category> categories = categoryService.getAllCategories();
		List<SubCategory> subCategories = new ArrayList<SubCategory>();
		final String baseUrl = "https://www.a101.com.tr";
		final int marketId = 1;
		
		for (Category category : categories) {
			String url = category.getCategoryLink();

			try {
				Document document = Jsoup.connect(url).get();
				Element paginationElement = document.selectFirst("nav.pagination");
				Elements pageLinks = paginationElement.select("ul > li.page-item > a.page-link");
				//Element lastPageLink = paginationElement.selectFirst("ul li.page-item:last-child > a.page-link");
				Elements subCategoryElements = document.select("li.derin > a");
				int maxPageNumber = 1;
				
				for(int i = pageLinks.size() -1; i > 0; i--) {
					Element pageLink = pageLinks.get(i);
					String pageNumberText = pageLink.text().trim();
					try {
						maxPageNumber = Integer.parseInt(pageNumberText);
						break;
					}
					catch (NumberFormatException e) {
						continue;
					}
				}

				for (Element subCategoryElement : subCategoryElements) {
					String subCategoryName = subCategoryElement.text();
					String subCategoryLink = subCategoryElement.attr("href");
					
					if (categoryService.subCategoryExists(subCategoryName, marketId))
						continue;

					SubCategory subCategory = new SubCategory();
					subCategory.setSubCategoryName(subCategoryName);
					subCategory.setSubCategoryLink(baseUrl + subCategoryLink);
					subCategory.setParentCategoryId(category.getCategoryId());
					subCategory.setPages(maxPageNumber);
					subCategory.setMarketId(marketId);
					

					subCategories.add(subCategory);

				}
			} catch (IOException e) {
				// Handle exception appropriately
				e.printStackTrace();
			}
		}
		return subCategories;
	}

	public List<Product> scrapeProductsFromA101() {
	    List<SubCategory> subCategories = categoryService.getAllSubCategories();
	    List<Product> products = new ArrayList<>();
		final String baseUrl = "https://www.a101.com.tr";
	    int marketId = 1;

	    for (SubCategory subCategory : subCategories) {
	        String url = subCategory.getSubCategoryLink();
	        int pages = subCategory.getPages();
	        
	        for(int page = 1; page <= pages; page++) {
	        	url= url + "?page=" + page;
	        	
	        	try {
					Document document = Jsoup.connect(url).get();
					
					Elements productElements = document.select("html > body > section > section:nth-of-type(3) > div:nth-of-type(3) > div:nth-of-type(3) > div > div:nth-of-type(2) > div:nth-of-type(2) > div > ul > li");

					for (Element productElement : productElements) {
						long marketProductId = Long.parseLong(productElement.select("article.product-card").attr("data-sku").replaceAll("[^0-9]",""));
						String productName = productElement.select("h3.name").text();
						double productPrice = Double.parseDouble((productElement.select("span.current").text()).replaceAll("[^\\d.]", ""));
						String imageUrl = productElement.select("figure.product-image > img").attr("src");
						String productUrl = baseUrl + productElement.select("a.name-price").attr("href");
						
						if (productService.productExists(productName, marketId)) {
							System.out.println("Exists!");
							continue;
						}

						Product product = new Product();
						product.setSubCategoryId(subCategory.getSubCategoryId());
						product.setCategoryId(subCategory.getParentCategoryId());
						product.setMarketProductId(marketProductId);
						product.setProductName(productName);
						product.setProductPrice(productPrice);
						product.setImageUrl(imageUrl);
						product.setProductUrl(productUrl);
						product.setMarketId(marketId);
						
						products.add(product);
					}
				} catch (IOException e) {
					// Handle exception appropriately
					e.printStackTrace();
				}
	        }
	    }
	    return products;
	}

}
