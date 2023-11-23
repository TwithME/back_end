package com.example.twithme.model.dto.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserReq {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeneralLogInDto {
        @NotBlank(message = "ID를 입력해주세요.")
        private String username;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KakaoLogInDto {
        @NotBlank(message = "SNS ID를 입력해주세요.")
        private String snsId;

        @NotBlank(message = "SNS Token을 입력해주세요.")
        private String snsToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NaverLogInDto {
        @NotBlank(message = "code를 입력해주세요.")
        private String code;

        @NotBlank(message = "state를 입력해주세요.")
        private String state;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KakaoSignUpDto {
        private String name;
        private LocalDate birthDate;
        private String gender;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserFormReq {

        @NotBlank(message="이름은 필수 입력 값입니다.")
        private String name;

        private String gender;

        private LocalDate birthDate;

        private String phone;

        private String username;

        private String email;

        private Long firstTripStyleId;
        private Long secondTripStyleId;
        private Long thirdTripStyleId;

        @NotBlank
        @Length(min = 2, max = 16)
        private String password;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FindUserByName {
        private String name;
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FindUserByUsername {
        private String username;
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChangePassword {
        private String username;
        private String newPassword;
        private String newPasswordCheck;
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
    public static class ProfileUpdateDto {
        private Long firstTripStyleId;
        private Long secondTripStyleId;
        private Long thirdTripStyleId;
        private String firstBio;
        private String secondBio;
        private String thirdBio;
        private String instagram;
        private Long mbtiId;
        private String phone;
    }
}
