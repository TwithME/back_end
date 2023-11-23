package com.example.twithme.model.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRes {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatRoomDto {
        Long chatRoomId;
        Long recipientId;
        String name;
        String profileUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatDto {
        boolean isSender;
        String content;
        LocalDateTime sendTime;
    }
}
