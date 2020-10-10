package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration;

import java.util.Arrays;

@SpringBootApplication

public class DemoApplication {

	public static void main(String[] args) {
		//SpringApplication.run(DemoApplication.class, args);

		int arr[] = {2,3,5};
		int target = 10;

		Arrays.sort(arr);
		int possible=(int)Math.round(1.0*target/arr.length);
		System.out.println(possible);
		int low=0;
		while(possible>arr[low]){
			target-=arr[low];
			if(++low==arr.length)break;
			possible=(int)Math.round((1.0*target/(arr.length-low)-.1));
		}
		System.out.println(possible);
	}

}
