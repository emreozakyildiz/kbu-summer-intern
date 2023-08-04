package com.internship.summer.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class CrawlerUtility extends WebCrawler {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp4|zip|gz))$");
	private final File storageFolder;

	public CrawlerUtility() {
		String path = "data/a101";
		storageFolder = new File(path);
		if (!storageFolder.exists()) {
			storageFolder.mkdirs();
		}
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && (href.startsWith("https://www.a101.com.tr")
				|| href.startsWith("https://www.trendyol.com") || href.startsWith("https://www.amazon.com.tr"));
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();

			try {
				String path = url.substring("https://".length());
				String[] pathSegments = path.split("/");

				File currentFolder = storageFolder;
				for (String segment : pathSegments) {
					File newFolder = new File(currentFolder, segment);
					if (!newFolder.exists()) {
						newFolder.mkdirs();
					}
					currentFolder = newFolder;
				}

				File file = new File(currentFolder, String.valueOf(url.hashCode()) + ".html");
				FileWriter writer = new FileWriter(file);
				writer.write(html);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
