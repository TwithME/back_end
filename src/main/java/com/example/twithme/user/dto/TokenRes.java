package com.example.twithme.user.dto;


import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TokenRes {
    private Long userId;
    private Long kakaoId;
    private String accessToken;
    private String refreshToken;
}
