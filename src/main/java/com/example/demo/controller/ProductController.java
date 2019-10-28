package com.example.demo.controller;

import com.example.demo.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.FlipkartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/product")
@CrossOrigin
public class ProductController {

    @Autowired
    protected FlipkartService flipkartService;

    @Autowired
    protected ProductService productService;

    @PostMapping(value = "/flipkart")
    public void createIndex() {
        flipkartService.createIndex();
    }

    @GetMapping(value = "/{id}")
    public Product getProduct(@PathVariable String id) {
        return flipkartService.getProductById(id);
    }

    @PostMapping(value = "/general")
    public void createGeneralIndex() {
        productService.createProducts();
    }

    @GetMapping(value = "/search")
    public Iterable<Product> searchProducts(@RequestParam String query){
        return flipkartService.searchProducts(query);
    }

    @GetMapping(value = "/all")
    public Iterable<Product> fetchAllProducts() {
        return flipkartService.getAllProducts();
    }
}
