package com.stackroute.summaryapp.service;

 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.summaryapp.dto1.MeetingWorkflowResponse;
import com.stackroute.summaryapp.dto1.StructuredSummaryResponse;
import com.stackroute.summaryapp.dto1.UserConversation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebclientAIService {

        WebClient webClient;
        private final ObjectMapper objectMapper;
    @Value("${genai.api.key}")
    private String apiKey;

    @Value("${genai.api.url}")
    private String openAiUrl;

    @Value("${genai.model}")
    private String model;

    private final Map<Long,List<UserConversation>>
            userHistory = new ConcurrentHashMap<>();

    public WebclientAIService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;

    }

    public String summarize(
            Long userId,
            String content) {

        String systemPrompt = """
                You are an enterprise meeting assistant.

                Generate output in the format:

                Summary:
                Key Decisions:
                Action Items:
                Risks:
                """;

        String response =
                callOpenAI(systemPrompt, content);

        saveHistory(
                userId,
                "SUMMARY",
                content,
                response);

        return response;
    }

    public String generateMailDraft(
            Long userId,
            String content) {

        String systemPrompt = """
                You are a professional
                corporate communication expert.

                Create a follow-up email.

                Format:

                Subject:
                Greeting:
                Summary:
                Action Items:
                Closing:
                """;

        String response =
                callOpenAI(systemPrompt, content);

        saveHistory(
                userId,
                "MAIL_DRAFT",
                content,
                response);

        return response;
    }

    public List<UserConversation>
    getHistory(Long userId) {

        return userHistory.getOrDefault(
                userId,
                new ArrayList<>());
    }

    private void saveHistory(
            Long userId,
            String operation,
            String request,
            String response) {

        userHistory
                .computeIfAbsent(
                        userId,
                        k -> new ArrayList<>())
                .add(
                        new UserConversation(
                                userId,
                                operation,
                                request,
                                response,
                                LocalDateTime.now()
                        )
                );
    }

    private String callOpenAI(
            String systemPrompt,
            String userPrompt) {

        Map<String,Object> body =
                Map.of(
                        "model", model,
                        "messages",
                        List.of(
                                Map.of(
                                        "role","system",
                                        "content",
                                        systemPrompt
                                ),
                                Map.of(
                                        "role","user",
                                        "content",
                                        userPrompt
                                )
                        )
                );

        Map response =
                webClient.post()
                        .uri(openAiUrl)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + apiKey)
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

        List<Map<String,Object>> choices =
                (List<Map<String,Object>>)
                        response.get("choices");

        Map<String,Object> message =
                (Map<String,Object>)
                        choices.get(0)
                                .get("message");

        return message
                .get("content")
                .toString();
    }
    
    public StructuredSummaryResponse
    generateStructuredSummary(
            Long userId,
            String notes) {

        String systemPrompt = """
                You are an enterprise meeting assistant.

                Return ONLY valid JSON.

                {
                  "summary":"",
                  "keyDecisions":[],
                  "actionItems":[]
                }

                Do not return markdown.
                Do not return explanations.
                """;

        String response =
                callOpenAI(
                        systemPrompt,
                        notes);

        try {

            StructuredSummaryResponse result =
                    objectMapper.readValue(
                            response,
                            StructuredSummaryResponse.class);

            saveHistory(
                    userId,
                    "JSON_SUMMARY",
                    notes,
                    response);

            return result;

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Unable to parse AI response",
                    ex);
        }
    }
    
    public MeetingWorkflowResponse
    meetingWorkflow(
            Long userId,
            String notes) {

        String summary =
                summarize(userId, notes);

        String actionItems =
                generateActionItems(notes);

        String emailDraft =
                generateMailDraft(
                        userId,
                        notes);

        return new MeetingWorkflowResponse(
                summary,
                actionItems,
                emailDraft
        );
    }
    
    private String generateActionItems(
            String notes) {

        String systemPrompt = """
                You are a project manager.

                Generate action items from
                meeting notes.

                Return bullet points only.
                """;

        return callOpenAI(
                systemPrompt,
                notes);
    }
}