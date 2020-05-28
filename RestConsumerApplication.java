package com.example.consumer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableSwagger2
public class RestConsumerApplication {
	
		
	public static void main(String[] args) {
		SpringApplication.run(RestConsumerApplication.class, args);
	}

}
