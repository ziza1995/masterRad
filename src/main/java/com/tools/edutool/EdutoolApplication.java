package com.tools.edutool;

import com.tools.edutool.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Import(SwaggerConfiguration.class)
public class EdutoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdutoolApplication.class, args);
	}

}
