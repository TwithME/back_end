package com.example.twithme.controller.user;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.exception.ServerErrorException;
import com.example.twithme.common.model.ApiResponse;
import com.example.twithme.model.dto.user.UserReq;
import com.example.twithme.model.dto.user.UserRes;
import com.example.twithme.service.user.UserService;
import com.google.gson.JsonElement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Api(tags={"01.User"})
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;


    @ApiOperation(value = "서버용(프론트 사용x)",
            notes = "")
    @GetMapping("/oauth/kakao")
    public ApiResponse<UserReq.KakaoLogInDto> getAccessTokenKakao(@RequestParam String code) {
        //String accessTokenFromSocial = userService.getKakaoAccessToken(code);


        String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate=new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        System.out.println("code = "+code);
        params.put("code", code);
        params.put("client_id", "0f8b7cf617336b262bd00ba6ed4f7805");
        params.put("redirect_uri", "http://semtle.catholic.ac.kr:8081/oauth/kakao");
        params.put("grant_type", "authorization_code");
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(KAKAO_TOKEN_REQUEST_URL, params, String.class);
        System.out.println(stringResponseEntity);



        UserReq.KakaoLogInDto kakaoLogInDto = null;
                //userService.createAndLoginKakaoUser(accessTokenFromSocial);

        return new ApiResponse<>(kakaoLogInDto);
    }


    @ApiOperation(value = "카카오 추가 회원가입", notes = "카카오 회원가입 후 추가 정보를 등록하는 API 입니다.\n" +
            "/user/login/kakao을 통해 받은 accessToken을 입력해야만 해당 API 호출이 가능합니다.")
    @PostMapping("/user/signup/kakao")
    public ApiResponse<String> kakaoSignUp(@RequestBody UserReq.KakaoSignUpDto kakaoSignUpDto,
                                           HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        userService.doKakaoAdditionalSignUp(userId, kakaoSignUpDto);
        return new ApiResponse<>("카카오 회원가입이 완료되었습니다.");
    }


    @ApiOperation(value = "카카오 로그인", notes = "카카오 계정으로 로그인을 합니다.")
    @PostMapping("/user/login/kakao")
    public ApiResponse<UserRes.UserInfoWithToken> doKakaoLogIn(@RequestBody UserReq.KakaoLogInDto kakaoLogInDto) {
//        Base64.Decoder decoder = Base64.getUrlDecoder();
//        final String payload = new String(decoder.decode(kakaoLogInDto.getSnsId().split("\\.")[1]));
//        JsonParser jsonParser = new BasicJsonParser();
//        Map<String, Object> jsonArray;
//        try {
//            jsonArray = jsonParser.parseMap(payload);
//        }
//        catch(RuntimeException e) {
//            throw new BadRequestException("Token 값을 parsing 하지 못하였습니다. 관리자에게 문의해주세요.");
//        }
//        if (!jsonArray.containsKey("sub")) {
//            throw new ServerErrorException();
//        }
//        String snsId = jsonArray.get("sub").toString();


        //카카오 유저 생성
        boolean needsAdditionalSignUp = false;
        if(!userService.existsSnsUser("kakao", kakaoLogInDto.getSnsId())) {
            userService.doKakaoSignUp(kakaoLogInDto);
            needsAdditionalSignUp = true;
        }

        //통합 소셜 로그인
        UserRes.UserInfoWithToken userInfoWithToken = userService.doSocialLogIn("kakao", kakaoLogInDto.getSnsId());
        userInfoWithToken.setNeedsAdditionalSignUp(needsAdditionalSignUp);


        //트윗미 서비스 액세스 토큰 발급
        if(userInfoWithToken.getId().equals(0L)) {
            throw new BadRequestException("회원가입을 먼저 진행해주세요.");
        }

        return new ApiResponse<>(userInfoWithToken);
    }


}
