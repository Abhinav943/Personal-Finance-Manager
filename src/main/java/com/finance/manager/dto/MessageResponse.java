package com.finance.manager.dto;

public class MessageResponse {
    private String message;
    private Long userId;

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(String message, Long userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}