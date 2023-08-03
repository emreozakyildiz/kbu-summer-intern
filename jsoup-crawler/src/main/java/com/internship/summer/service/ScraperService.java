package com.internship.summer.service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ScraperService {
	final String url = "https://www.a101.com.tr";
	final String url2 = "https://oguzhan.menemencioglu.info/";
	final String url3 = "https://emreozakyildiz.com";
	Set<String> visited;

	public ScraperService(){
		visited = new HashSet<String>();
		System.out.println("\n---\n");
		System.out.println("Source : " + url);
		System.out.println("\n---\n");
		System.out.println("Links :");
		
	}
	
	private static String getDomain(String url) {
	    try {
	        URI uri = new URI(url);
	        String domain = uri.getHost();
	        if (domain != null) {
	            return domain.startsWith("www.") ? domain.substring(4) : domain;
	        }
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	
	public void crawl(String url){
		try {
			// Bypass SSL certificate validation
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, new java.security.SecureRandom());
            Jsoup.connect(url)
                 .sslSocketFactory(sslContext.getSocketFactory())
                 .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                 .get();
			
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");
			String domain = getDomain(url);
			
			visited.add(url);
			
			for(Element link : links) {
				String linkUrl= link.attr("abs:href");
				String linkDomain = getDomain(linkUrl);
				
				if(linkDomain != null && linkDomain.equals(domain) && !visited.contains(linkUrl) && !linkUrl.equals(url + "#main-content")) {
					if(isLinkValid(linkUrl)) {
						System.out.println(url);
						crawl(linkUrl);
					}
					else {
						System.out.println(url + "[BROKEN-LINK]");
					}
				}
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isLinkValid(String linkUrl) {
        try {
            URL url = new URL(linkUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return responseCode >= 200 && responseCode < 400;
        } catch (Exception e) {
            return false;
        }
    }
	
	public void printUrls() {
		for(String visit : visited) {
			System.out.println(visit);
		}
	}
}
