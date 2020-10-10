package com.example.demo.service;

import com.example.demo.EmailSubscription;
import com.example.demo.ServerProduct;
import com.example.demo.repo.EmailSubscriptionRepository;
import com.example.demo.repo.ServerProductRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    EmailSubscriptionRepository emailSubscriptionRepository;

    @Autowired
    ServerProductRepository serverProductRepository;

    @Autowired
    JavaMailSender sender;

    @Async
    public void sendMail(String email,ServerProduct product)throws Exception {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setTo(email);
        helper.setText("Price of "+product.getTitle()+" has been dropped to "+product.getDiscountPrice()+" on "+product.getSite());
        helper.setSubject("Price Drop Alert!!!!!");
        sender.send(mimeMessage);
        
        
    }

    @Override
    public void consume()throws Exception {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "my-cluster-kafka-bootstrap-myproject.192.168.99.100.nip.io:443");
        props.put("group.id", "sample-consumer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("security.protocol", "SSL");
        props.put("ssl.keystore.location", "src/main/resources/keystore.jks");
        props.put("ssl.keystore.password", "password");
        props.put("ssl.truststore.location", "src/main/resources/keystore.jks");
        props.put("ssl.truststore.password", "password");

        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList("my-topic"));
            while (true) {
                final ConsumerRecords<String, String> records = consumer.poll(1000);
                for (final ConsumerRecord<String, String> record : records) {
                    System.out.println("*********Product key ********"+record.key());
                    ServerProduct serverProduct = serverProductRepository.findById(record.key()).get();
                    EmailSubscription emailSubscription = emailSubscriptionRepository.findById(serverProduct.getId()).get();
                    for(String email: emailSubscription.getEmails()) {
                        // Send email
                        sendMail(email,serverProduct);
                    }
                    //System.out.println("Receiving message: " + record.value());
                }
            }
        }
    }
}
