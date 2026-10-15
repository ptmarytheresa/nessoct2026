package com.stackroute.summarywebreactive.dto1;

public record SummaryResponse(

        Long userId,

        String summary,

        String generatedAt
) {
}