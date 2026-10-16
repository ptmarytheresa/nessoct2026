package com.stackroute.summaryapp.dto;


public record MeetingRequest(
        Long userId,
        String meetingNotes
) {
}