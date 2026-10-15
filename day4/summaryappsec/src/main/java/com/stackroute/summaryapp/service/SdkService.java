package com.stackroute.summaryapp.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SdkService {

    @Value("${genai.api.key}")
    private String apiKey;

    private OpenAIClient client;

    @PostConstruct
    public void init() {

        client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public String basicSummary(String notes) {

        return callOpenAI(
                "Summarize the following meeting notes:\n"
                        + notes);
    }

    public String summaryWithSystemPrompt(String notes) {

        String prompt = """
                You are an expert meeting assistant.

                Generate concise summaries.

                Focus on:
                - Key decisions
                - Action items
                - Important discussions

                Meeting Notes:
                """
                + notes;

        return callOpenAI(prompt);
    }

    public String executiveSummary(String notes) {

        String prompt = """
                You are a senior executive.

                Summarize the meeting notes.

                Tone:
                Professional
                Executive
                Business-focused

                Maximum 5 bullet points.

                Meeting Notes:
                """
                + notes;

        return callOpenAI(prompt);
    }

    public String highlights(String notes) {

        String prompt = """
                You are a business analyst.

                Extract:

                - Key Decisions
                - Risks
                - Opportunities
                - Next Steps

                Meeting Notes:
                """
                + notes;

        return callOpenAI(prompt);
    }

    public String emailDraft(String notes) {

        String prompt = """
                Generate a professional
                follow-up email.

                Include:

                - Greeting
                - Meeting Summary
                - Action Items
                - Closing

                Meeting Notes:
                """
                + notes;

        return callOpenAI(prompt);
    }

    private String callOpenAI(String prompt) {

        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model("gpt-4o-mini")
                        .addUserMessage(prompt)
                        .build();

        ChatCompletion completion =
                client.chat()
                        .completions()
                        .create(params);

        return completion
                .choices()
                .get(0)
                .message()
                .content()
                .orElse("No Response");
    }
}