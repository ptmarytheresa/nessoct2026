
package com.stackroute.summarywebreactive.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.stackroute.summarywebreactive.dto1.MailDraftResponse;
import com.stackroute.summarywebreactive.dto1.StructuredSummaryResponse;
import com.stackroute.summarywebreactive.dto1.SummaryResponse;
import com.stackroute.summarywebreactive.dto1.UserHistoryResponse;
import com.stackroute.summarywebreactive.dto1.WorkshopRequest;
import com.stackroute.summarywebreactive.service.WebclientAIService;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v2")
public class WebclientController {

    private final WebclientAIService service;

    public WebclientController(WebclientAIService service) {
        this.service = service;
    }

    @PostMapping("/summarize")
    public Mono<SummaryResponse> summarize(
            @RequestBody WorkshopRequest request) {

        return service.summarize(
                request.userId(),
                request.content()
        ).map(response ->
                new SummaryResponse(
                        request.userId(),
                        response,
                        LocalDateTime.now().toString()
                )
        );
    }

    @PostMapping("/mail-draft")
    public Mono<MailDraftResponse> mailDraft(
            @RequestBody WorkshopRequest request) {

        return service.generateMailDraft(
                request.userId(),
                request.content()
        ).map(response ->
                new MailDraftResponse(
                        request.userId(),
                        response,
                        LocalDateTime.now().toString()
                )
        );
    }

    @GetMapping("/user-history/{userId}")
    public UserHistoryResponse history(
            @PathVariable Long userId) {

        return new UserHistoryResponse(
                userId,
                service.getHistory(userId)
        );
    }

    @PostMapping(
    	    value = "/structured-summary/{userId}",
    	    consumes = MediaType.APPLICATION_JSON_VALUE,
    	    produces = MediaType.APPLICATION_JSON_VALUE
    	)
    	public Mono<StructuredSummaryResponse> summarizeJson(
    	        @PathVariable Long userId,
    	        @RequestBody WorkshopRequest request) {

    	    return service.generateStructuredSummary(
    	            userId,
    	            request.content()
    	    );
    	}
    
    
}
