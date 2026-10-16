package com.stackroute.summaryapp.dto1;

public record SummaryResponse(

        Long userId,

        String summary,

        String generatedAt
) {
}