package com.example.twithme.user.service;


import com.example.dango.global.exception.ServerErrorException;
import com.example.dango.global.jwt.TokenProvider;
import com.example.dango.user.dto.UserReq;
import com.example.dango.user.entity.Authority;
import com.example.dango.user.entity.User;
import com.example.dango.user.repository.AuthorityRepository;
import com.example.dango.user.repository.UserRepository;
import com.example.twithme.global.exception.BadRequestException;
import com.example.twithme.global.exception.ServerErrorException;
import com.example.twithme.global.jwt.TokenProvider;
import com.example.twithme.user.dto.GenerateToken;
import com.example.twithme.user.dto.TokenRes;
import com.example.twithme.user.dto.UserReq;
import com.example.twithme.user.dto.UserRes;
import com.example.twithme.user.entity.Authority;
import com.example.twithme.user.entity.User;
import com.example.twithme.user.repository.AuthorityRepository;
import com.example.twithme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthorityRepository authorityRepository;



    @SneakyThrows
    public User findNowLoginUser(){

        String username = SecurityUtil.getCurrentUsername().orElse(null);

        if(username == null){
            throw new ServerErrorException("accessToken이 없습니다");
        }

        return userRepository.findUserByKakaoId(Long.valueOf(username)).get();
    }


    @Transactional
    public UserRes.UserDetailDto signup(UserReq.SignupUserDto signupUserDto) {
        if (userRepository.existsByKakaoId(signupUserDto.getUsername())) {
            throw new BadRequestException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(signupUserDto.getUsername())
                .password(passwordEncoder.encode(signupUserDto.getPassword()))
                .name(signupUserDto.getName())
                .phone(signupUserDto.getPhone())
                .imageUrl(signupUserDto.getImageUrl())
                .authorities(Collections.singletonList(authority))
                .build();

        userRepository.save(user);

        return UserRes.UserDetailDto.toDto(user);
    }

    //전화번호 양식 체크
    public boolean validationEmail(String email){
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$");
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    // 회원정보 수정
    public void userInfoEdit(UserReq.UserInfoEditReq userInfoEditReq){
        User user = findNowLoginUser();

        if (userInfoEditReq.getName() != null){
            user.setName(userInfoEditReq.getName());
        }
        if (userInfoEditReq.getPhone() != null){
            user.setPhone(userInfoEditReq.getPhone());
        }
        userRepository.save(user);
    }



    @Transactional
    public TokenRes login(UserReq.LoginUserDto loginUserDto){
        Optional<User> optionalUser = userRepository.findUserByKakaoId(loginUserDto.getUsername());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {

                //인증토큰 생성
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(loginUserDto.getUsername(), loginUserDto.getPassword());

                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication); //SecurityContext에 저장


                User loginUser = optionalUser.get();
                Long userId = loginUser.getId();
                GenerateToken generateToken = tokenProvider.createAllToken(userId);

                return TokenRes.builder()
                        .kakaoId(loginUser.getKakaoId())
                        .accessToken(generateToken.getAccessToken())
                        .refreshToken(generateToken.getRefreshToken())
                        .build();
            } else{
                throw new BadRequestException("비밀번호를 다시 입력해주세요");
            }
        } else{
            throw new BadRequestException("존재하지 않는 회원입니다");
        }
    }


    public User toSocialLoginUser(Long kakao_id, String social, String name, String profileUrl) {
        Authority authority = authorityRepository.findByAuthorityName("ROLE_USER").get();
        User user = User.builder()
            .kakaoId(kakao_id)
            .name(name)
            .password("")  //소셜로그인은 비밀번호x
            .imageUrl(profileUrl)
            .authorities(Collections.singletonList(authority))
            .social(social)
            .build();
        return user;
    }


    public String withdrawal() {
        User user = findNowLoginUser();
        
        userRepository.save(user);

        return "회원 탈퇴가 완료되었습니다";
    }
}
