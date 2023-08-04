package com.internship.summer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.internship.summer.service.CrawlerService;

@SpringBootApplication
public class Crawler4jSummerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Crawler4jSummerApplication.class, args);
		
		CrawlerService crawlerService = context.getBean(CrawlerService.class);
	}

}
