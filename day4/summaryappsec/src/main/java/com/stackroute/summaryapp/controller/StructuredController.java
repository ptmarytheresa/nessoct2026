package com.stackroute.summaryapp.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.summaryapp.dto1.MeetingWorkflowResponse;
import com.stackroute.summaryapp.dto1.StructuredSummaryResponse;
import com.stackroute.summaryapp.dto1.WorkshopRequest;
import com.stackroute.summaryapp.service.WebclientAIService;

@RestController("/api/structure")
public class StructuredController
{
	WebclientAIService service;
	
@PostMapping("/summarize-json")
public StructuredSummaryResponse
summarizeJson(

        @RequestBody WorkshopRequest request) {

    return service.generateStructuredSummary(

                    request.userId(),

                    request.content());
}

@PostMapping("/meeting-workflow")
public MeetingWorkflowResponse
meetingWorkflow(

        @RequestBody WorkshopRequest request) {

    return service
            .meetingWorkflow(

                    request.userId(),

                    request.content());
}
}
