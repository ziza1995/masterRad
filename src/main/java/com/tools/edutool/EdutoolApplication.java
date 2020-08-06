package com.tools.edutool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EdutoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdutoolApplication.class, args);
	}

}
