package com.internship.summer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

@Service
public class CrawlerService{
	private final LogService logService;
	String crawlStorageFolder = "data";
	//String crawlStorageFolder = System.getProperty("java.io.tmpdir") + File.separator + "crawl-data";

	private final int numberOfThreads = 4; 

	public CrawlerService(LogService logService) throws Exception{
		this.logService = logService;
		CrawlConfig config = new CrawlConfig();
		
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setPolitenessDelay(10000);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    	controller.addSeed("https://www.trendyol.com");
    	
    	// The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<CrawlerUtility> factory = CrawlerUtility::new;
        
        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfThreads);
        logService.closeLogStreams();
		
	}
}
