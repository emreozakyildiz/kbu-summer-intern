package com.internship.crawler.service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector.SelectorParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.internship.crawler.model.Category;
import com.internship.crawler.model.Product;
import com.internship.crawler.model.SubCategory;

import jakarta.persistence.NonUniqueResultException;

@Service
public class ScraperService {
	private final CategoryService categoryService;
	private final ProductService productService;

	public ScraperService(CategoryService categoryService, ProductService productService) {
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
			e.printStackTrace();
		}

		return categories;
	}

	public List<SubCategory> scrapeSubCategoriesFromA101(Category category) {
		final String baseUrl = "https://www.a101.com.tr";
		final int marketId = 1;
		List<SubCategory> subCategories = new ArrayList<SubCategory>();

		String url = category.getCategoryLink();

		try {
			Document document = Jsoup.connect(url).get();
			Element paginationElement = document.selectFirst("nav.pagination");
			Elements pageLinks = paginationElement.select("ul > li.page-item");
			Elements subCategoryElements = document.select("li.derin > a");
			int maxPageNumber = 1;

			try {
				maxPageNumber = Integer
						.parseInt(pageLinks.get(pageLinks.size() - 2).select("a.page-link").attr("title").trim());
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
				subCategory.setCategory(category);
				subCategory.setPages(maxPageNumber);
				subCategory.setMarketId(marketId);

				subCategories.add(subCategory);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return subCategories;
	}

	public List<Product> scrapeProductsFromA101(SubCategory subCategory) {
		final String baseUrl = "https://www.a101.com.tr";
		int marketId = 1;
		List<Product> products = new ArrayList<>();

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
					long marketProductId = Long.parseLong(
							productElement.select("article.product-card").attr("data-sku").replaceAll("[^0-9]", ""));
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
					product.setCategory(subCategory.getCategory());
					product.setSubCategory(subCategory);
					product.setMarketProductId(marketProductId);
					product.setProductName(productName);
					product.setProductPrice(productPrice);
					product.setImageUrl(imageUrl);
					product.setProductUrl(productUrl);
					product.setMarketId(marketId);

					products.add(product);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NonUniqueResultException e) {
				System.out.println("NonUniqueResultException occurred!");
			}
		}

		return products;
	}

	public List<Category> scrapeCategoriesFromMigros() {
		final String url = "https://www.migros.com.tr";
		final int marketId = 2;

		List<Category> categories = new ArrayList<Category>();

		try {
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments("-headless");
			WebDriver webDriver = new FirefoxDriver(firefoxOptions);

			Actions actions = new Actions(webDriver);
			webDriver.get(url);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));

			WebElement popUpButton = wait
					.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.popover fa-icon")));
			popUpButton.click();
			WebElement policyButton = wait
					.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.mat-caption:nth-child(1)")));
			policyButton.click();

			WebElement categoriesIcon = wait.until(
					ExpectedConditions.elementToBeClickable(By.cssSelector(".categories-icon > span:nth-child(1)")));
			actions.moveToElement(categoriesIcon).perform();

			String pageSource = webDriver.getPageSource();

			webDriver.close();

			Document document = Jsoup.parse(pageSource);

			Elements categoryElements = document.select(".categories-sub-categories-wrapper .categories");

			for (Element categoryElement : categoryElements) {
				String categoryName = categoryElement.text().trim();
				String categoryLink = categoryElement.attr("href");

				Category category = new Category();
				category.setCategoryName(categoryName);
				category.setCategoryLink(url + categoryLink);
				category.setMarketId(marketId);

				categories.add(category);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return categories;
	}

	public List<SubCategory> scrapeSubCategoriesFromMigros(Category category) {
		final String baseUrl = "https://www.migros.com.tr";
		final int marketId = 2;
		int maxPageNumber = 1;

		List<SubCategory> subCategories = new ArrayList<SubCategory>();

		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.addArguments("-headless");
		WebDriver webDriver = new FirefoxDriver(firefoxOptions);
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));
		Actions actions = new Actions(webDriver);

		try {
			webDriver.get(category.getCategoryLink());

			WebElement subCategoryMenu = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector(".filter__subcategories > div:nth-child(2)")));
			actions.moveToElement(subCategoryMenu).perform();

			Document document = Jsoup.parse(webDriver.getPageSource());

			WebElement lastPageButton = webDriver.findElement(By.id("pagination-button-last"));

			if (lastPageButton != null) {
				lastPageButton.click();

				wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));
				wait.until(ExpectedConditions.urlContains("?sayfa="));

				String currentPageUrl = webDriver.getCurrentUrl();

				int startIndex = currentPageUrl.indexOf("?sayfa=") + 7;
				int endIndex = currentPageUrl.indexOf("&", startIndex);

				if (endIndex == -1) {
					endIndex = currentPageUrl.indexOf("=", startIndex);
				}

				if (startIndex != -1 && endIndex != -1) {
					String pageNumberStr = currentPageUrl.substring(startIndex, endIndex);
					maxPageNumber = Integer.parseInt(pageNumberStr);
				}
				System.out.println("PageURL: " + currentPageUrl + " , number : " + maxPageNumber);
			}

			webDriver.close();

			Elements subCategoryElements = document.select(".items a.text-color-black.mat-body-2.ng-star-inserted");

			for (Element subCategoryElement : subCategoryElements) {
				String subCategoryName = subCategoryElement.text().replaceAll("\\s*\\(\\d+\\)\\s*", "");
				String subCategoryLink = subCategoryElement.attr("href");

				if (categoryService.subCategoryExists(subCategoryName, marketId))
					continue;

				SubCategory subCategory = new SubCategory();
				subCategory.setSubCategoryName(subCategoryName);
				subCategory.setSubCategoryLink(baseUrl + subCategoryLink);
				subCategory.setCategory(category);
				subCategory.setPages(maxPageNumber);
				subCategory.setMarketId(marketId);

				subCategories.add(subCategory);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return subCategories;
	}

	public List<Product> scrapeProductsFromMigros(SubCategory subCategory) {
		final String baseUrl = "https://www.migros.com.tr";
		int marketId = 2;
		int pages = subCategory.getPages();
		long defaultMarketProductId = 0;
		List<Product> products = new ArrayList<>();

		String url = subCategory.getSubCategoryLink();

		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.addArguments("-headless");
		WebDriver webDriver = new FirefoxDriver(firefoxOptions);

		webDriver.get(url);

		try {
			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
			//wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
			//		By.cssSelector("sm-list-page-item.mdc-layout-grid__cell--span-2-desktop:nth-child")));
			wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

			Document document = Jsoup.parse(webDriver.getPageSource());
			webDriver.quit();

			// Elements productElements = document
			// 		.select("sm-list-page-item.mdc-layout-grid__cell--span-2-desktop:nth-child");
			Elements productElements = document.select("sm-list-page-item.mdc-layout-grid__cell--span-2-desktop");
			for (Element productElement : productElements) {
				String productName = productElement.select(".mat-caption.text-color-black.product-name").text();
				System.out.println("Product Name:" + productName);
				// Double productPrice = Double
				// 		.parseDouble(productElement.select(".price-new.subtitle-1.price-new-only .amount").text()
				// 				.replace(",", ".").replace("TL", ""));
				// Double productPrice = Double.parseDouble(productElement.select(".amount").text().replace(",", ".").replace("TL", ""));
				Double productPrice = Double.parseDouble(productElement.select("div.price-new span.amount").text().replace(",", ".").replace("TL", ""));
				
				System.out.println("Product Price: " + productPrice);
				String imageUrl = productElement.select(".fe-product-image.image img").attr("src");
				System.out.println("Image URL: " + imageUrl);
				String productUrl = baseUrl
						+ productElement.select(".mat-caption.text-color-black.product-name").attr("href");
				System.out.println("Product URL: " + productUrl);

				if (productService.productExists(productName, marketId)) {
					System.out.println("Exists!");
					continue;
				}
				Product product = new Product();
				product.setCategory(subCategory.getCategory());
				product.setSubCategory(subCategory);
				product.setMarketProductId(defaultMarketProductId);
				product.setProductName(productName);
				product.setProductPrice(productPrice);
				product.setImageUrl(imageUrl);
				product.setProductUrl(productUrl);
				product.setMarketId(marketId);

				products.add(product);
			}
		} catch (SelectorParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return products;
	}

	public List<Category> scrapeCategoriesFromTrendyol() {

		final String url = "https://www.trendyol.com";
		final int marketId = 3;
		List<Category> categories = new ArrayList<Category>();

		try {
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments("-headless");
			WebDriver webDriver = new FirefoxDriver(firefoxOptions);
			webDriver.get(url);

			Document document = Jsoup.parse(webDriver.getPageSource());
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
			e.printStackTrace();
		}

		return categories;
	}

	public List<SubCategory> scrapeSubCategoriesFromTrendyol() {
		final String baseUrl = "https://www.trendyol.com";
		final int marketId = 3;
		int maxPageNumber = 1;

		List<Category> categories = categoryService.getAllCategoriesByMarket(marketId);
		List<SubCategory> subCategories = new ArrayList<SubCategory>();

		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.addArguments("-headless");
		WebDriver webDriver = new FirefoxDriver(firefoxOptions);
		webDriver.get(categories.get(0).getCategoryLink());

		Document document = Jsoup.parse(webDriver.getPageSource());
		Elements subCategoryElements = document.select("div.sub-nav a.sub-category-header");
		webDriver.close();

		try {
			for (Element subCategoryElement : subCategoryElements) {
				String subCategoryName = subCategoryElement.text();
				String subCategoryLink = subCategoryElement.attr("href");

				if (categoryService.subCategoryExists(subCategoryName, marketId))
					continue;

				SubCategory subCategory = new SubCategory();
				subCategory.setSubCategoryName(subCategoryName);
				subCategory.setSubCategoryLink(baseUrl + subCategoryLink);
				subCategory.setPages(maxPageNumber);
				subCategory.setMarketId(marketId);

				subCategories.add(subCategory);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return subCategories;
	}

	public List<SubCategory> oldScrapeSubCategoriesFromMigros() {
		final String baseUrl = "https://www.migros.com.tr";
		final int marketId = 2;
		int maxPageNumber = 1;

		List<Category> categories = categoryService.getAllCategoriesByMarket(marketId);
		List<SubCategory> subCategories = new ArrayList<SubCategory>();

		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.addArguments("-headless");
		WebDriver webDriver = new FirefoxDriver(firefoxOptions);
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));
		Actions actions = new Actions(webDriver);

		try {
			for (Category category : categories) {
				webDriver.get(category.getCategoryLink());

				WebElement subCategoryMenu = wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.cssSelector(".filter__subcategories > div:nth-child(2)")));
				actions.moveToElement(subCategoryMenu).perform();

				Document document = Jsoup.parse(webDriver.getPageSource());
				Elements subCategoryElements = document.select(".items a.text-color-black.mat-body-2.ng-star-inserted");

				for (Element subCategoryElement : subCategoryElements) {
					String subCategoryName = subCategoryElement.text();
					String subCategoryLink = subCategoryElement.attr("href");

					if (categoryService.subCategoryExists(subCategoryName, marketId))
						continue;

					SubCategory subCategory = new SubCategory();
					subCategory.setSubCategoryName(subCategoryName);
					subCategory.setSubCategoryLink(baseUrl + subCategoryLink);
					subCategory.setPages(maxPageNumber);
					subCategory.setMarketId(marketId);

					subCategories.add(subCategory);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return subCategories;
	}

	public List<Product> oldScrapeProductsFromMigros() {
		final String baseUrl = "https://www.migros.com.tr";
		int marketId = 2;
		long defaultMarketProductId = 0;
		List<SubCategory> subCategories = categoryService.getAllSubCategoriesByMarket(marketId);
		List<Product> products = new ArrayList<>();

		for (SubCategory subCategory : subCategories) {
			String url = subCategory.getSubCategoryLink();

			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments("-headless");
			WebDriver webDriver = new FirefoxDriver(firefoxOptions);
			webDriver.get(url);

			try {
				WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
						By.cssSelector("sm-list-page-item.mdc-layout-grid__cell--span-2-desktop:nth-child")));

				Document document = Jsoup.parse(webDriver.getPageSource());
				webDriver.close();

				Elements productElements = document
						.select("sm-list-page-item.mdc-layout-grid__cell--span-2-desktop:nth-child");
				System.out.println("Product Elements Length: " + productElements.size());
				for (Element productElement : productElements) {
					String productName = productElement.select(".mat-caption.text-color-black.product-name").text();
					System.out.println("Product Name:" + productName);
					Double productPrice = Double
							.parseDouble(productElement.select(".price-new.subtitle-1.price-new-only .amount").text()
									.replace(",", ".").replace("TL", ""));
					System.out.println("Product Price: " + productPrice);
					String imageUrl = productElement.select(".fe-product-image.image img").attr("src");
					System.out.println("Image URL: " + imageUrl);
					String productUrl = baseUrl
							+ productElement.select(".mat-caption.text-color-black.product-name").attr("href");
					System.out.println("Product URL: " + productUrl);

					if (productService.productExists(productName, marketId)) {
						System.out.println("Exists!");
						continue;
					}
					Product product = new Product();
					// product.setSubCategoryId(subCategory.getSubCategoryId());
					// product.setCategoryId(subCategory.getParentCategoryId());
					product.setMarketProductId(defaultMarketProductId);
					product.setProductName(productName);
					product.setProductPrice(productPrice);
					product.setImageUrl(imageUrl);
					product.setProductUrl(productUrl);
					product.setMarketId(marketId);

					products.add(product);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return products;
	}

	public List<Product> scrapeProductsFromTrendyol() {
		final String baseUrl = "https://www.trendyol.com";
		int marketId = 3;
		long defaultMarketProductId = 0;
		List<SubCategory> subCategories = categoryService.getAllSubCategoriesByMarket(marketId);
		List<Product> products = new ArrayList<>();

		for (SubCategory subCategory : subCategories) {
			String url = subCategory.getSubCategoryLink();

			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments("-headless");
			WebDriver webDriver = new FirefoxDriver(firefoxOptions);
			webDriver.get(url);

			JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
			while (true) {
				long currentHeight = (long) jsExecutor.executeScript(
						"return Math.max( document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight );");
				jsExecutor.executeScript("window.scrollTo(0, " + currentHeight + ");");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long newHeight = (long) jsExecutor.executeScript(
						"return Math.max( document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight );");
				if (newHeight == currentHeight) {
					break;
				}
			}

			try {
				Document document = Jsoup.parse(webDriver.getPageSource());
				webDriver.close();

				Elements productElements = document.select(".p-card-chldrn-cntnr");

				for (Element productElement : productElements) {
					String productName = productElement.select(".prdct-desc-cntnr-name").attr("title");
					System.out.println("Product Name : " + productName);
					System.out
							.println("Product Price: " + productElement.select(".prc-box-dscntd").text().split(" ")[0]);
					double productPrice = Double.parseDouble(
							productElement.select(".prc-box-dscntd").text().split(" ")[0].replace(",", "."));

					String imageUrl = productElement.select(".p-card-img").attr("src");
					System.out.println("Product Image : " + imageUrl);
					String productUrl = baseUrl + productElement.select("a").attr("href").split("\\?advertItems")[0];
					System.out.println("Product Url : " + productUrl);
					System.out.println("-----------");

					if (productService.productExists(productName, marketId)) {
						System.out.println("Exists!");
						continue;
					}
					Product product = new Product();
					// product.setSubCategoryId(subCategory.getSubCategoryId());
					// product.setCategoryId(subCategory.getParentCategoryId());
					product.setMarketProductId(defaultMarketProductId);
					product.setProductName(productName);
					product.setProductPrice(productPrice);
					product.setImageUrl(imageUrl);
					product.setProductUrl(productUrl);
					product.setMarketId(marketId);

					products.add(product);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return products;
	}

}
