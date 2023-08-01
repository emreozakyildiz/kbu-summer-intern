package com.summerproject.spring.service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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

	public List<Category> scrapeCategoriesFromMigros() {
		final String url = "https://www.migros.com.tr";
		final int marketId = 2;

		List<Category> categories = new ArrayList<Category>();

		try {
			WebDriver webDriver = new FirefoxDriver();
			Actions actions = new Actions(webDriver);
			webDriver.get(url);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));

			WebElement popUpButton = wait
					.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.popover fa-icon")));
			popUpButton.click();

			WebElement policyButton = webDriver
					.findElement(By.cssSelector("action-buttons.button.map-caption.btn.settings"));
			// wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.map-caption.btn.settings")));
			// JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
			// jsExecutor.executeScript("argument[0].remove", policyButton);
			// policyButton.click();

			// WebElement element =
			// webDriver.findElement(By.cssSelector("div.tab.mat-caption.text-color-black"));
			WebElement element = webDriver.findElement(By.xpath("header-bottom"));
			WebElement parentElement = element.findElement(By.cssSelector(":first-child"));
			actions.moveToElement(parentElement).click();

			String pageSource = webDriver.getPageSource();

			Document document = Jsoup.parse(pageSource);
		} catch (Exception e) {
			// Handle exception appropriately
			e.printStackTrace();
		}

		return categories;
	}

	public List<Category> scrapeCategoriesFromTrendyol() {

		final String url = "https://www.trendyol.com";
		final int marketId = 3;
		List<Category> categories = new ArrayList<Category>();

		try {
			WebDriver webDriver = new FirefoxDriver();
			Actions actions = new Actions(webDriver);
			webDriver.get(url);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));

			Document document = Jsoup.parse(webDriver.getPageSource());
			// Document document = Jsoup.connect(url).get();
			Elements categoryElements = document.select(".main-nav > li");
			webDriver.close();

			for (Element categoryElement : categoryElements) {
				String categoryName = categoryElement.select("a.category-header").text();
				String categoryLink = categoryElement.select("a.category-header").attr("href");

				if (categoryService.categoryExists(categoryName, marketId))
					continue;

				Category category = new Category();
				category.setCategoryName(categoryName);
				category.setCategoryLink(url + categoryLink);
				category.setMarketId(marketId);

				categories.add(category);
			}

		} catch (Exception e) {
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
				Elements pageLinks = paginationElement.select("ul > li.page-item");
				Elements subCategoryElements = document.select("li.derin > a");
				int maxPageNumber = 1;

				try {
					maxPageNumber = Integer
							.parseInt(pageLinks.get(pageLinks.size() - 2).select("a.page-link").attr("title"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
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

			for (int page = 1; page <= pages; page++) {
				url = url + "?page=" + page;

				System.out.println(subCategory.getSubCategoryName() + " için Sayfa: " + page);

				try {
					Document document = Jsoup.connect(url).get();

					Elements productElements = document.select(
							"html > body > section > section:nth-of-type(3) > div:nth-of-type(3) > div:nth-of-type(3) > div > div:nth-of-type(2) > div:nth-of-type(2) > div > ul > li");

					for (Element productElement : productElements) {
						long marketProductId = Long.parseLong(productElement.select("article.product-card")
								.attr("data-sku").replaceAll("[^0-9]", ""));
						String productName = productElement.select("h3.name").text();
						double productPrice = Double
								.parseDouble((productElement.select("span.current").text()).replaceAll("[^\\d.]", ""));
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
