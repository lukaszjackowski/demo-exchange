package com.example.demo;

import org.springframework.boot.SpringApplication;

public class TestExchangeApplication {

	static void main(String[] args) {
		SpringApplication.from(ExchangeApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
