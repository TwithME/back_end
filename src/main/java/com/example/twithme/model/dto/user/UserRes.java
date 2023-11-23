package com.example.twithme.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserRes {

    @Data
    @Builder
    public static class RoleDto {
        private Long id;
        private String password;
        private List<String> roles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfoWithToken {
        private Long id;
        private String nickname;
        private int alarmNum;
        private String accessToken;
        private boolean needsAdditionalSignUp;
        private boolean isFirstLogin;
        private String userRole;
    }
    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class NaverAccessTokenResponseDto {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private String expiresIn;

        @JsonProperty("error")
        private String error;

        @JsonProperty("error_description")
        private String errorDescription;

        @Builder
        private NaverAccessTokenResponseDto(String accessToken, String refreshToken, String tokenType, String expiresIn,
                                      String error, String errorDescription) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
            this.error = error;
            this.errorDescription = errorDescription;
        }
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NaverProfileResponseDto {
        @JsonProperty("resultcode")
        private String resultCode;

        @JsonProperty("message")
        private String message;

        @JsonProperty("response")
        private NaverUserProfileDto naverUserProfileDto;
    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    @Builder
    public static class NaverUserProfileDto {
        @JsonProperty("id")
        private String snsId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("mobile")
        private String phone;

        @JsonProperty("email")
        private String email;

        @JsonProperty("birthyear")
        private String birthYear;

        @JsonProperty("birthday")
        private String birthDay;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class findUserByName {
        private String username;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class findUserByUsername {
        private String username;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageDto {
        private String content;
        private String senderName;
        private String recipientName;
        private LocalDateTime sendTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileDto {
        private String name;
        private String username;
        private int age;
        private String instagram;
        private String phone;
        private String gender;
        private String firstBio;
        private String secondBio;
        private String thirdBio;
        private String mbti;
        private String firstTripStyle;
        private String secondTripStyle;
        private String thirdTripStyle;
        private String profileUrl;
        private boolean isNamePrivate;
        private boolean isMbtiPrivate;
        private boolean isInstagramPrivate;
        private boolean isPhonePrivate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MbtiDto {
        private Long id;
        private String name;
    }
}
