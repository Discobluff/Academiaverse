package com.academiaverse.dto.request;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private Long itemId;
    private Integer buyerId;
}