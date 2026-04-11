package com.api_nivra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.api_nivra.config.DotenvLoader;

@SpringBootApplication
public class ApiNivraApplication {

	public static void main(String[] args) {

		DotenvLoader.init();

		SpringApplication.run(ApiNivraApplication.class, args);
	}

}
