package com.internship.summer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.internship.summer.service.ScraperService;

@SpringBootApplication
public class JsoupCrawlerApplication {

	public static void main(String[] args) {
		final String url3 = "https://www.a101.com.tr";
		String url2 = "https://oguzhan.menemencioglu.info/";
		String url = "https://emreozakyildiz.com";
		
		SpringApplication.run(JsoupCrawlerApplication.class, args);
		ScraperService sc = new ScraperService();
		sc.crawl(url3);
		sc.printUrls();
	}

}
