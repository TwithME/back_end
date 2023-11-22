package com.example.twithme.user.dto;

import com.example.dango.user.entity.User;
import lombok.*;

import java.util.List;

public class UserRes {


    @Data
    @Builder
    public static class RoleDto {
        private Long id;
        private String password;
        private List<String> roles;
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class SignupUserRes {
        private String username;
        private String name;
    }




    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class UserDetailDto {
        private String username;
        private String name;
        private String phone;
        private String imageUrl;
        private String social;
        private boolean firstLogin;

        public static UserDetailDto toDto(User user){
            return UserDetailDto.builder()
                    // 일단 kako 로그인만 구현하므로 막음
//                    .username(user.getKakaoId())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .imageUrl(user.getImageUrl())
                    .social(user.getSocial())
                    .build();
        }
    }



    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class EmailAuthRes {
        int checkCode;
    }


}
