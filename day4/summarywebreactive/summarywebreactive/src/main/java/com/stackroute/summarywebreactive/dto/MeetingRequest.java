package com.stackroute.summarywebreactive.dto;


public record MeetingRequest(
        Long userId,
        String meetingNotes
) {
}