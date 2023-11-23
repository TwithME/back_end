package com.example.twithme.model.dto.hashtag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class HashtagReq {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HashtagNameDto {
        String name;
    }
}
