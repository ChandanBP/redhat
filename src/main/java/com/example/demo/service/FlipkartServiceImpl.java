package com.example.demo.service;

import com.example.demo.Product;
import com.example.demo.ServerProduct;
import com.example.demo.repo.ProductRepository;
import com.example.demo.repo.ServerProductRepository;
import com.opencsv.CSVReader;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public class FlipkartServiceImpl implements FlipkartService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ServerProductRepository serverProductRepository;

    @Override
    public Product getProductById(String id) {
        return productRepository.findById(id).get();
    }

    @Override
    public void createIndex() {

        try {
            BufferedReader br = new BufferedReader(new FileReader("/Users/administrator/Downloads/flipkart_com-ecommerce_sample.csv"));

            CSVReader csvReader = new CSVReader(new FileReader("/Users/administrator/Downloads/flipkart_com-ecommerce_sample.csv"));
            String[] data;
            boolean first = true;
            while ((data = csvReader.readNext()) != null) {
                if(first) {
                    first = false;
                    csvReader.readNext();
                    continue;
                }

                if (data.length<14) continue;
                System.out.println("test");

                Product product = new Product();

                if(data[5].length()>0)
                product.setId(data[5]);

                if(data[3].length()>0)
                product.setTitle(data[3]);

                if(data[10].length()>0)
                product.setDescription(data[10]);
                try {
                    if (data[6].length() > 0)
                        product.setSalePrice(Long.parseLong(data[6]));
                }catch (NumberFormatException numberException) {
                    product.setSalePrice(1000L);
                }
                try {
                    if(data[7].length()>0)
                        product.setDiscountPrice(Long.parseLong(data[7]));
                } catch (NumberFormatException numberException) {
                    product.setSalePrice(800L);
                }

                if(data[4].length()>0)
                product.setCategory(data[4]);
                product.setStock(true);

                if(data[2].length()>0)
                product.setProductURL(data[2]);
                product.setSite("FLIPKART");
                productRepository.save(product);

                ServerProduct serverProduct = new ServerProduct();
                serverProduct.setId(product.getId());
                serverProduct.setTitle(product.getTitle());
                serverProduct.setCategory(product.getCategory());
                serverProduct.setDescription(product.getDescription());
                serverProduct.setDiscountPrice(product.getDiscountPrice());
                serverProduct.setImage(product.getImage());
                serverProduct.setProductURL(product.getProductURL());
                serverProduct.setStock(true);
                serverProduct.setSalePrice(product.getSalePrice());
                serverProduct.setDiscountPrice(product.getDiscountPrice());
                serverProduct.setSite("FLIPKART");
                serverProductRepository.save(serverProduct);
            }
        } catch (IOException ioexception) {
            // handle the exception
        }
    }

    @Override
    public Iterable<Product> searchProducts(String query) {
        SearchRequest searchRequest = new SearchRequest("products");
        searchRequest.types("product");
        return (query!=null && query.length()>0)?productRepository.search(queryStringQuery(query)):
                getAllProducts();
    }

    @Override
    public Iterable<Product> getAllProducts() {
        Pageable paging = PageRequest.of(0,1000);
        return productRepository.findAll(paging);
        //return productRepository.findAll();
    }
}
