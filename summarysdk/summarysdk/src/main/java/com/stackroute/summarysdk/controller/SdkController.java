package com.stackroute.summarysdk.controller;

 
 

import org.springframework.web.bind.annotation.*;

import com.stackroute.summarysdk.dto.AIResponse;
import com.stackroute.summarysdk.dto.MeetingRequest;
import com.stackroute.summarysdk.service.SdkService;

@RestController
@RequestMapping("/api/sdk")
public class SdkController {

    private final SdkService aiService;

    public SdkController(SdkService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/summary/basic")
    public AIResponse basicSummary(
            @RequestBody MeetingRequest request) {

        return new AIResponse(
                aiService.basicSummary(
                        request.meetingNotes()));
    }

    @PostMapping("/summary/system")
    public AIResponse systemSummary(
            @RequestBody MeetingRequest request) {

        return new AIResponse(
                aiService.summaryWithSystemPrompt(
                        request.meetingNotes()));
    }

    @PostMapping("/summary/tone")
    public AIResponse toneSummary(
            @RequestBody MeetingRequest request) {

        return new AIResponse(
                aiService.executiveSummary(
                        request.meetingNotes()));
    }

    @PostMapping("/summary/highlights")
    public AIResponse highlights(
            @RequestBody MeetingRequest request) {

        return new AIResponse(
                aiService.highlights(
                        request.meetingNotes()));
    }

    @PostMapping("/email-draft")
    public AIResponse emailDraft(
            @RequestBody MeetingRequest request) {

        return new AIResponse(
                aiService.emailDraft(
                        request.meetingNotes()));
    }
}