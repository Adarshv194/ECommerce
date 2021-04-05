package com.ShopOnline.Buy.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BuyOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyOnlineApplication.class, args);
	}

}
