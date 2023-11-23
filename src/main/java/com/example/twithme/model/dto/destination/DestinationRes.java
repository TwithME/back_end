package com.example.twithme.model.dto.destination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class DestinationRes {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DestinationDto {
        private Long id;
        private String name;
    }

}
