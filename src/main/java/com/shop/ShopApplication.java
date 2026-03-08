package com.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ShopApplication {

	public static void main(String[] args) {
		//.env 파일을 읽고
		Dotenv dotenv = Dotenv.configure().load();
		// 파일 안의 key-value 쌍을 자바 시스템 변수로 등록
		dotenv.entries().forEach( entry -> { System.setProperty(entry.getKey(), entry.getValue());
		});

		SpringApplication.run(ShopApplication.class, args);
	}

}
