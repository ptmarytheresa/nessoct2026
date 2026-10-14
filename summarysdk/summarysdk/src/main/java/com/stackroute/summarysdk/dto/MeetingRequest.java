package com.stackroute.summarysdk.dto;


public record MeetingRequest(
        Long userId,
        String meetingNotes
) {
}