package com.stackroute.summaryapp.dto1;

 

import java.util.List;

public record StructuredSummaryResponse(

        String summary,

        List<String> keyDecisions,

        List<String> actionItems
) {
}