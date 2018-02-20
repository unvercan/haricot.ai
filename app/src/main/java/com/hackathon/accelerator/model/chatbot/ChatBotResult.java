package com.hackathon.accelerator.model.chatbot;

public enum ChatBotResult {

    ALLERGIC("allergic"),
    NON_ALLERGIC("non-allergic"),
    NOT_FOUND("food-not-found");

    private String message;

    ChatBotResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
