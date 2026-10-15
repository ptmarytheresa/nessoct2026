package com.stackroute.summarywebreactive.dto1;
 

public record MailDraftResponse(

        Long userId,

        String emailDraft,

        String generatedAt
) {
}