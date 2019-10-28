package com.example.demo.repo;

import com.example.demo.ServerProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ServerProductRepository extends ElasticsearchRepository<ServerProduct, String> {

}
