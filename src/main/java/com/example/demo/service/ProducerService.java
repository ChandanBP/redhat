package com.example.demo.service;

import java.util.Properties;

public interface ProducerService {
    //void produce(Properties props) throws Exception;
    void startBatch() throws Exception;
    void pushEmails() throws Exception;
    void pushEmail(String productId,String email);
}
