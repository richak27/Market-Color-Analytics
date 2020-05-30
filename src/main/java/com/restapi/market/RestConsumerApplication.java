package com.restapi.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableScheduling
public class RestConsumerApplication {
<<<<<<< HEAD
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}	
			
	
=======

>>>>>>> 7b0c427d5480e1cf6dad3f28d8a64e2a0ee1559f
	public static void main(String[] args) {
		SpringApplication.run(RestConsumerApplication.class, args);
	}

}
