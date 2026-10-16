package com.stackroute.geminiai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeminiaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeminiaiApplication.class, args);
	}

}

// post method
// http://localhost:8086/chat/ask
// {
// "userInput":"help me understand, what is big data"
// }