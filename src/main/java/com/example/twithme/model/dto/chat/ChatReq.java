package com.example.twithme.model.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ChatReq {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendChatDto {
        private String content;
        private Long recipientId;
    }
}
