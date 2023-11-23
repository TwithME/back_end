package com.example.twithme.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

public class SmsDto {

    @ToString
    @Data
    @Builder
    public static class IncomingNumDto {
        @JsonProperty("to")
        private String to;
    }

    @ToString
    @Data
    @Builder
    public static class SmsRequestDto {
        @JsonProperty("type")
        private String type;

        @JsonProperty("contentType")
        private String contentType;

        @JsonProperty("countryCode")
        private String countryCode;

        @JsonProperty("from")
        private String from;

        @JsonProperty("content")
        private String content;

        @JsonProperty("messages")
        private List<IncomingNumDto> incomingNums;
    }

    @ToString
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SmsResponseDto {
        @JsonProperty("requestId")
        private String requestId;

        @JsonProperty("requestTime")
        private String requestTime;

        @JsonProperty("statusCode")
        private int statusCode;

        @JsonProperty("statusName")
        private String statusName;
    }

    public class PhoneNumDto {
    }

//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class PhoneNumDto {
//        @NotBlank(message = "휴대폰 번호를 입력해주세요.")
//        private String phone;
//    }
}
