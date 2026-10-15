
package com.stackroute.summarywebreactive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.stackroute.summarywebreactive.dto1.StructuredSummaryResponse;
import com.stackroute.summarywebreactive.dto1.UserConversation;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebclientAIService {

    private final WebClient webClient;
    
    @Autowired
    private final ObjectMapper objectMapper;

    @Value("${genai_api_key}")
    private String apiKey;

    @Value("${genai.api.url}")
    private String openAiUrl;

    @Value("${genai.model}")
    private String model;

    private final Map<Long, List<UserConversation>> userHistory =
            new ConcurrentHashMap<>();

    public WebclientAIService(WebClient webClient,ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper=objectMapper;
    }

    public Mono<String> summarize(Long userId, String content) {

        String systemPrompt = """
                You are an enterprise meeting assistant.

                Generate output in the format:

                Summary:
                Key Decisions:
                Action Items:
                Risks:
                """;

        return callOpenAI(systemPrompt, content)
                .doOnNext(response ->
                        saveHistory(
                                userId,
                                "SUMMARY",
                                content,
                                response
                        )
                );
    }

    public Mono<String> generateMailDraft(
            Long userId,
            String content) {

        String systemPrompt = """
                You are a professional corporate communication expert.

                Create a follow-up email.

                Format:

                Subject:
                Greeting:
                Summary:
                Action Items:
                Closing:
                """;

        return callOpenAI(systemPrompt, content)
                .doOnNext(response ->
                        saveHistory(
                                userId,
                                "MAIL_DRAFT",
                                content,
                                response
                        )
                );
    }

    public List<UserConversation> getHistory(Long userId) {

        return new ArrayList<>(
                userHistory.getOrDefault(
                        userId,
                        List.of()
                )
        );
    }

    private void saveHistory(
            Long userId,
            String operation,
            String request,
            String response) {

        userHistory
                .computeIfAbsent(
                        userId,
                        key -> new CopyOnWriteArrayList<>()
                )
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

    private Mono<String> callOpenAI(
            String systemPrompt,
            String userPrompt) 
    {

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", systemPrompt
                        ),
                        Map.of(
                                "role", "user",
                                "content", userPrompt
                        )
                )
        );

        return webClient.post()
                .uri(openAiUrl)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {

                    List<Map<String, Object>> choices =
                            (List<Map<String, Object>>)
                                    response.get("choices");

                    if (choices == null || choices.isEmpty()) {
                        throw new IllegalStateException(
                                "AI API returned no choices."
                        );
                    }

                    Map<String, Object> message =
                            (Map<String, Object>)
                                    choices.get(0).get("message");

                    if (message == null ||
                            message.get("content") == null) {
                        throw new IllegalStateException(
                                "AI API returned no message content."
                        );
                    }

                    return message.get("content").toString();
                });
    }
    

public Mono<StructuredSummaryResponse> generateStructuredSummary(
        Long userId,
        String notes) {

    String systemPrompt = """
            
                You are a meeting-minutes extraction engine.

            Read the meeting notes carefully. Extract information
            from the ENTIRE notes, not just the first paragraph.

            Return ONLY valid JSON in this exact format:
            {
              "summary": "string",
              "keyDecisions": ["string"],
              "actionItems": ["string"]
            }

            EXTRACTION RULES:

            SUMMARY:
            Summarize the main discussion.

            KEY DECISIONS:
            Extract decisions, commitments, agreements, targets,
            and plans explicitly agreed upon in the meeting.
            A target or agreed evaluation plan counts as a decision.
            Do not return an empty array if such information exists.

            ACTION ITEMS:
            Extract every task, follow-up, analysis, or deliverable
            mentioned in the notes.
            Include the responsible team and deadline when stated.
            Do not invent missing owners or deadlines.
            Do not return an empty array if tasks exist.

            IMPORTANT:
            - Read all paragraphs and numbered lists.
            - Do not confuse discussion topics with decisions.
            - Do not confuse decisions with assigned tasks.
            - Use only facts present in the meeting notes.
            - Return JSON only, without Markdown or explanations.
           
          
            """;

    return callOpenAI(systemPrompt, notes).doOnNext(response ->
    System.out.println("RAW AI RESPONSE: " + response))
            .map(response -> {
                try {
                	 
                    StructuredSummaryResponse result =
                            objectMapper.readValue(
                                    response,
                                    StructuredSummaryResponse.class
                            );

                    saveHistory(
                            userId,
                            "JSON_SUMMARY",
                            notes,
                            response
                    );

                    return result;

                } catch (Exception ex) {
                    throw new RuntimeException(
                            "Unable to parse AI response: " + response,
                            ex
                    );
                }
            });
}

    
}
