package com.internship.crawler.service;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Service;

import com.internship.crawler.model.Product;

@Service
public class OntologyAlignerService {
	Model rdfModel;

	public OntologyAlignerService() {
		rdfModel = ModelFactory.createDefaultModel();

	}

	public void alignProducts(List<Product> products) {
		for (Product product : products) {
			Resource productResource = createProductResource(product);

			for (Product alignedProduct : products) {
				if (!alignedProduct.equals(product)) {
					Resource alignedProductResource = createProductResource(alignedProduct);
					createAlignmentRelationship(productResource, alignedProductResource);
				}
			}
		}
	}

	private void createAlignmentRelationship(Resource product1, Resource product2) {
		Property alignsWith = rdfModel.createProperty("http://product-db.com/products#alignsWith");
		product1.addProperty(alignsWith, product2);
	}

	private Resource createProductResource(Product product) {
		Resource productResource = rdfModel.createResource(getProductURI(product));
		Property nameProperty = rdfModel.createProperty("http://product-db.com/products#name");
		productResource.addProperty(nameProperty, product.getProductName());
		return productResource;
	}

	private String getProductURI(Product product) {
		return "http://product-db.com/products#" + product.getProductId();
	}

}
