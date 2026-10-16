package com.stackroute.summaryapp.dto1;

 

import java.util.List;

public record UserHistoryResponse(

        Long userId,

        List<UserConversation> history
) {
}