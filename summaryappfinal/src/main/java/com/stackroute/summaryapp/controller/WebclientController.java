package com.stackroute.summaryapp.controller;

 

import com.stackroute.summaryapp.dto.*;
import com.stackroute.summaryapp.dto1.MailDraftResponse;
import com.stackroute.summaryapp.dto1.SummaryResponse;
import com.stackroute.summaryapp.dto1.UserHistoryResponse;
import com.stackroute.summaryapp.dto1.WorkshopRequest;
import com.stackroute.summaryapp.service.AuditPublisher;
import com.stackroute.summaryapp.service.WebclientAIService;
import com.stackroute.summaryapp.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v2")
public class WebclientController {
	
	  
    @Autowired
    AuditPublisher auditpublisher;
       WebclientAIService service;

    public WebclientController(
    		WebclientAIService service) {

        this.service = service;
    }

    @PostMapping("/summarize")
    public SummaryResponse summarize(
            @RequestBody WorkshopRequest request) {
    	
    	  String user = SecurityUtil.getCurrentUsername();
      	auditpublisher.publish("SUMMARY_REQUEST",  user, "/webclient", "SUCCESS");


        return new SummaryResponse(

                request.userId(),

                service.summarize(
                        request.userId(),
                        request.content()),

                LocalDateTime.now().toString()
        );
    }

    @PostMapping("/mail-draft")
    public MailDraftResponse mailDraft(
            @RequestBody WorkshopRequest request) {

        return new MailDraftResponse(

                request.userId(),

                service.generateMailDraft(
                        request.userId(),
                        request.content()),

                LocalDateTime.now().toString()
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
}