package com.internship.summer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.internship.summer.service.CrawlerService;

@SpringBootApplication
public class Crawler4jSummerApplication {
	private CrawlerService crawlerService;

	public static void main(String[] args) {
		SpringApplication.run(Crawler4jSummerApplication.class, args);
	}

}
