package com.stackroute.summarywebreactive.dto1;
 

import java.time.LocalDateTime;

public record UserConversation(

        Long userId,

        String operation,

        String request,

        String response,

        LocalDateTime timestamp
) {
}