package com.jiraws.library.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.jiraws.library.book.model")
@EnableJpaRepositories(basePackages = "com.jiraws.library.book.persistence")
@SpringBootApplication(scanBasePackages = {
		"com.jiraws.library", "com.example.demo", "com.jiraws.featurex"
})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}