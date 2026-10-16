package com.stackroute.summaryapp.controller;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.stackroute.summaryapp.dto.AIResponse;
import com.stackroute.summaryapp.dto.MeetingRequest;
import com.stackroute.summaryapp.service.AIService;
import com.stackroute.summaryapp.service.AuditPublisher;
import com.stackroute.summaryapp.util.SecurityUtil;

@RestController
@RequestMapping("/api")
public class MeetingController {

    private final AIService aiService;
    
    @Autowired
    AuditPublisher auditpublisher;

    public MeetingController(
            AIService aiService) {

        this.aiService = aiService;
    }

    @PostMapping("/summary/basic")
    public AIResponse basicSummary(
            @RequestBody MeetingRequest request) {
    
    	String response= aiService.basicSummary(request.meetingNotes());

        String user = SecurityUtil.getCurrentUsername();
    	auditpublisher.publish("SUMMARY_REQUEST",  user, "/summarize", "SUCCESS");


    	return new AIResponse(response);
        
        
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