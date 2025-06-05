package com.example.demo;

import org.springframework.boot.SpringApplication;

public class TestMyApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(MyApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
