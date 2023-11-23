package com.example.twithme.model.dto.hashtag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class HashtagRes {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HashtagDto {
        private Long id;
        private String name;
//        int count;
    }
}
