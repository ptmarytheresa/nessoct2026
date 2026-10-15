package com.stackroute.summaryapp.controller;

 
import org.springframework.web.bind.annotation.*;

import com.stackroute.summaryapp.dto.AIResponse;
import com.stackroute.summaryapp.dto.MeetingRequest;
import com.stackroute.summaryapp.service.AIService;

@RestController
@RequestMapping("/api")
public class MeetingController {

    private final AIService aiService;

    public MeetingController(
            AIService aiService) {

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
    public AIResponse executiveSummary(
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
}