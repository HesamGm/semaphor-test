package com.jiring.jiringexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class JiringExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(JiringExamApplication.class, args);
	}

}
