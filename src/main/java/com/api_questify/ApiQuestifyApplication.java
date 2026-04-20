package com.api_questify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.api_questify.config.DotenvLoader;

@SpringBootApplication
@EnableScheduling
public class ApiQuestifyApplication {

	public static void main(String[] args) {

		DotenvLoader.init();
		SpringApplication.run(ApiQuestifyApplication.class, args);
	}

	

}
