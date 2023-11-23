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
