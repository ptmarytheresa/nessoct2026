package com.stackroute.summaryapp.dto1;
 

public record MailDraftResponse(

        Long userId,

        String emailDraft,

        String generatedAt
) {
}