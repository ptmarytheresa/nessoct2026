package com.stackroute.summaryapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class SummaryappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SummaryappApplication.class, args);
	}

	@PostConstruct
	public void printJavaInfo() {
	    System.out.println("JAVA HOME = " + System.getProperty("java.home"));
	}
}
