package com.darwinsys.eselling.listing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListResponse {

    String location;
    int successCount;
    List<String> messages;
    public StringBuilder stringBuilder = new StringBuilder();

    /// Provide constructor only to ensure that messages isn't null
    public ListResponse(String location, int successCount, List<String> messages) {
        this.location = location;
        this.successCount = successCount;
        this.messages = messages;
    }

    // Mainly for error branches
    public ListResponse(StringBuilder stringBuilder) {
        this();
        this.stringBuilder = stringBuilder;
    }

    public ListResponse() {
        this("Unknown", 0, List.of());
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ListResponse{" +
                "location='" + location + '\'' +
                ", successCount=" + successCount +
                ", messages=" + messages.size() +
                '}';
    }
}
