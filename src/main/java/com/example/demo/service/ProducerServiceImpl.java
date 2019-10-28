package com.example.demo.service;

import com.example.demo.EmailSubscription;
import com.example.demo.ServerProduct;
import com.example.demo.repo.EmailSubscriptionRepository;
import com.example.demo.repo.ProductRepository;
import com.example.demo.repo.ServerProductRepository;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProducerServiceImpl implements ProducerService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ServerProductRepository serverProductRepository;

    @Autowired
    EmailSubscriptionRepository emailSubscriptionRepository;

    @Autowired
    ElasticsearchClient client;

    final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(10L));

    @Override
    public void pushEmail(String productId,String email) {
        EmailSubscription subscription = emailSubscriptionRepository.findById(productId).get();
        List<String>emails = subscription.getEmails();
        if(emails!=null && !emails.contains(email)){
            emails.add(email);
        }
    }

    @Override
    public void pushEmails() throws Exception {

        SearchRequest searchRequest = new SearchRequest("products");
        searchRequest.types("product");
        searchRequest.scroll(scroll);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200,"http")));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        List<String>emails = new LinkedList<>();
        emails.add("cbp698@gmail.com");
        emails.add("chandu_ew@yahoo.co.in");
        while(searchHits != null && searchHits.length > 0) {
            for(SearchHit searchHit:searchHits){
                EmailSubscription emailSubscription = new EmailSubscription();
                emailSubscription.setId(searchHit.getId());
                emailSubscription.setEmails(emails);
                emailSubscriptionRepository.save(emailSubscription);
            }
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.searchScroll(scrollRequest,RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
    }

    @Override
    public void startBatch() throws Exception {

        SearchRequest searchRequest = new SearchRequest("products");
        searchRequest.types("product");
        searchRequest.scroll(scroll);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200,"http")));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        while(searchHits != null && searchHits.length > 0) {
            for(SearchHit searchHit:searchHits){
                Map<String,Object> map = searchHit.getSourceAsMap();

                String productId = searchHit.getId();
                Integer price = (Integer) map.get("discountPrice");
                ServerProduct serverProduct = serverProductRepository.findById(productId).get();
                if(serverProduct!=null && serverProduct.getDiscountPrice()!=null &&
                   serverProduct.getDiscountPrice()<price){
                    produce(getProps(),serverProduct);
                    break;
                }
            }
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.searchScroll(scrollRequest,RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
    }

    public Properties getProps() {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "my-cluster-kafka-bootstrap-myproject.192.168.99.100.nip.io:443");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("serializer.class", "kafka.serializer.DefaultEncoder");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("security.protocol", "SSL");
        props.put("ssl.keystore.location", "src/main/resources/keystore.jks");
        props.put("ssl.keystore.password", "password");
        props.put("ssl.truststore.location", "src/main/resources/keystore.jks");
        props.put("ssl.truststore.password", "password");
        return props;
    }

    //@Override
    public void produce(Properties props,ServerProduct serverProduct)throws Exception {

        final Producer<String, String> producer = new KafkaProducer<>(props);
        producer.send(new ProducerRecord<>("my-topic", serverProduct.getId(), serverProduct.getId()));
        //producer.send(new ProducerRecord<>("my-topic", serverProduct.getId(), serverProduct));

//        try (final Producer<String, String> producer = new KafkaProducer<>(props)) {
//            while (true) {
//                final String date = new Date().toString();
//                System.out.println("Sending message: " + date);
//                producer.send(new ProducerRecord<>("my-topic", "date", date));
//                Thread.sleep(2000);
//            }
//        }
    }
}
