package com.example.demo.service;

import com.example.demo.Product;
import com.example.demo.ServerProduct;
import com.example.demo.repo.ProductRepository;
import com.example.demo.repo.ServerProductRepository;
import com.opencsv.CSVReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ServerProductRepository serverProductRepository;

    @Override
    public void createProducts() {

        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get("/Users/administrator/Downloads/DatafinitiElectronicsProductsPricingData.csv"));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader("id","prices.amountMax","prices.amountMin","prices.availability","prices.condition",
                    "prices.currency","prices.dateSeen","prices.isSale","prices.merchant","prices.shipping","prices.sourceURLs","asins","brand","categories","dateAdded","dateUpdated","ean",
                    "imageURLs","keys","manufacturer","manufacturerNumber","name","primaryCategories","sourceURLs","upc","weight"));

            int count = 1;
            for(CSVRecord csvRecord: csvParser){
                Product product = new Product();

                if (count == 982) {
                    System.out.println(count);
                }

                // Product id
                if(csvRecord.get("asins").contains(",")) {
                    product.setId(csvRecord.get("asins").split(",")[0]);
                } else {
                    product.setId(csvRecord.get("asins"));
                }

                // Product title
                product.setTitle(csvRecord.get("name"));

                // Product price
                long l=200L;
                try {
                    l = (long) (Float.parseFloat(csvRecord.get("prices.amountMax")) * 70);
                    product.setSalePrice(l);
                    product.setDiscountPrice(l - 50);
                } catch (NumberFormatException exception) {
                    product.setSalePrice(l);
                    product.setDiscountPrice(l-50);
                }

                product.setStock(true);
                product.setCategory(csvRecord.get("categories"));

                String pURL = csvRecord.get("prices.sourceURLs");
                if(pURL.contains("walmart")) {
                    product.setSite("WALMART");
                } else if (pURL.contains("ebay")) {
                    product.setSite("EBAY");
                } else if (pURL.contains("amazon")) {
                    product.setSite("AMAZON");
                } else if (pURL.contains("bestbuy")) {
                    product.setSite("BESTBUY");
                }
                product.setProductUrl(pURL);
                product.setProductURL(pURL);
                productRepository.save(product);

                ServerProduct serverProduct = new ServerProduct();
                serverProduct.setId(product.getId());
                serverProduct.setTitle(product.getTitle());
                serverProduct.setCategory(product.getCategory());
                serverProduct.setDescription(product.getDescription());
                serverProduct.setDiscountPrice(product.getDiscountPrice());
                serverProduct.setImage(product.getImage());
                serverProduct.setProductURL(product.getProductURL());
                serverProduct.setProductUrl(product.getProductUrl());
                serverProduct.setStock(true);
                serverProduct.setSalePrice(product.getSalePrice());
                serverProduct.setDiscountPrice(product.getDiscountPrice());
                serverProduct.setSite(product.getSite());
                serverProductRepository.save(serverProduct);

                ++count;
            }

        } catch (IOException ioexception) {

        }
    }
}
