package com.example.demo.repo;

import com.example.demo.EmailSubscription;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EmailSubscriptionRepository extends ElasticsearchRepository<EmailSubscription, String> {
}
