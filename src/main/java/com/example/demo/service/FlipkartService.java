package com.example.demo.service;

import com.example.demo.Product;

public interface FlipkartService {
    void createIndex();
    Product getProductById(String id);
    Iterable<Product> searchProducts(String query);
    Iterable<Product> getAllProducts();
}
