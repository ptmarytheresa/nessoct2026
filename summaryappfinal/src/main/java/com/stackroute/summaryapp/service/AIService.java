package com.stackroute.summaryapp.service;

 

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.summaryapp.dto.AuditEvent;
import com.stackroute.summaryapp.repo.AuditEventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIService {

	@Autowired
    private   AuditEventRepository auditRepo;
	 
    private final RestTemplate restTemplate;

    @Value("${genai.api.key}")
    private String apiKey;

    @Value("${genai.api.url}")
    private String openAiUrl;

    @Value("${genai.model}")
    private String model;

    public AIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String basicSummary(String notes) {
    	
    	String result="";
    	AuditEvent e = new AuditEvent();
    	try {
    	
    	
        e.setActionName("SUMMARIZE_REQUEST");
        e.setUsername("mary");
        e.setEndpoint("/summarize");
        e.setStatus("SUCCESS");
  
        
        System.out.println("Record saved");

        result=callOpenAIWithoutSystemPrompt(
                "Summarize the following meeting notes:\n"
                        + notes);
    	}
    	catch(Exception excep)
    	{
            e.setStatus("Failed" + excep.getMessage());

             
            result= excep.getMessage();
    	}
    	
        auditRepo.save(e);
        return result;
    }

    public String summaryWithSystemPrompt(String notes) {
    	String result;
    	AuditEvent e = new AuditEvent();
        e.setActionName("SUMMARIZE_SYSTEM_REQUEST");
        e.setUsername("mary");
        e.setEndpoint("/summarize with sytem prompt");
       

        String systemPrompt = """
                You are an expert meeting assistant.

                Generate concise summaries.

                Focus on:
                - Key decisions
                - Action items
                - Important discussions
                """;
        
        try
        {
            e.setStatus("SUCCESS");

        result= callOpenAI(systemPrompt, notes);
        }
        catch(Exception excep)
    	{
            e.setStatus("Failed" + excep.getMessage());

             
            result= excep.getMessage();
    	}
    	
        auditRepo.save(e);
        return result;
    }

    public String executiveSummary(String notes) {

        String systemPrompt = """
                You are a senior executive.

                Summarize the meeting notes.

                Tone:
                Professional
                Executive
                Business-focused

                Maximum 5 bullet points.
                """;

        return callOpenAI(systemPrompt, notes);
    }

    public String highlights(String notes) {

        String systemPrompt = """
                You are a business analyst.

                Extract:
                - Key Decisions
                - Risks
                - Opportunities
                - Next Steps

                Return as bullet points.
                """;

        return callOpenAI(systemPrompt, notes);
    }

    private String callOpenAI(
            String systemPrompt,
            String userPrompt) {

        Map<String,Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role","system",
                                "content",systemPrompt
                        ),
                        Map.of(
                                "role","user",
                                "content",userPrompt
                        )
                )
        );

        return executeRequest(body);
    }

    private String callOpenAIWithoutSystemPrompt(
            String userPrompt) {

        Map<String,Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role","user",
                                "content",userPrompt
                        )
                )
        );

        return executeRequest(body);
    }

    private String executeRequest(
            Map<String,Object> body) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON);

        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String,Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        openAiUrl,
                        request,
                        Map.class);

        List<Map<String,Object>> choices =
                (List<Map<String,Object>>)
                        response.getBody()
                                .get("choices");

        Map<String,Object> message =
                (Map<String,Object>)
                        choices.get(0)
                                .get("message");

        return message.get("content").toString();
    }
}