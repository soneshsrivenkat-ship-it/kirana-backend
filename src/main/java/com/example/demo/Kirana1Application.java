package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.demo.reporting.repo")
public class Kirana1Application {

	public static void main(String[] args) {
		SpringApplication.run(Kirana1Application.class, args);
	}
}
