package com.example.demo.service;

import com.example.demo.Product;
import com.example.demo.ServerProduct;
import com.example.demo.repo.ProductRepository;
import com.example.demo.repo.ServerProductRepository;
import com.opencsv.CSVReader;
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
            CSVReader csvReader = new CSVReader(new FileReader("/Users/administrator/Downloads/DatafinitiElectronicsProductsPricingData.csv"));
            BufferedReader reader = Files.newBufferedReader(Paths.get("/Users/administrator/Downloads/DatafinitiElectronicsProductsPricingData.csv"));
            String[] data;
            boolean first = true;
            while ((data = csvReader.readNext()) != null) {
                if (first) {
                    first = false;
                    csvReader.readNext();
                    continue;
                }

                if (data.length < 26) continue;

                Product product = new Product();

                if(data[11].length()>0) {
                    if (data[11].contains(",")) {
                        product.setId(data[11].split(",")[0]);
                    } else {
                        product.setId(data[11]);
                    }
                }

                if(data[21].length()>0)
                    product.setTitle(data[21]);

                long l=200L;
                try {
                    l = (long) (Float.parseFloat(data[1]) * 70);
                    product.setSalePrice(l);
                    product.setDiscountPrice(l - 50);
                }catch (NumberFormatException numberException) {
                    product.setSalePrice(l);
                    product.setDiscountPrice(l-50);
                }

                product.setStock(true);

                if(data[13].length()>0)
                    product.setCategory(data[13]);

                if (data[10].contains("walmart")) {
                    product.setSite("WALMART");
                } else if (data[10].contains("ebay")) {
                    product.setSite("EBAY");
                } else if (data[10].contains("amazon")){
                    product.setSite("AMAZON");
                }else {
                    product.setSite("BESTBUY");
                }
                product.setProductURL(data[10]);
                product.setProductUrl(data[10]);
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
            }
        } catch (IOException ioexception) {

        }
    }
}
