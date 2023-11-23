package com.example.twithme.controller.user;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.exception.ServerErrorException;
import com.example.twithme.common.exception.dto.HttpRes;
import com.example.twithme.common.exception.dto.SmsDto;
import com.example.twithme.model.dto.user.UserReq;
import com.example.twithme.model.dto.user.UserRes;
import com.example.twithme.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Base64;
import java.util.Map;


@Api(tags={"01.User"})
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;


    @ApiOperation(value = "카카오 회원가입", notes = "카카오 회원가입 후 추가 정보를 등록하는 API 입니다.\n" +
            "/login/kakao을 통해 받은 accessToken을 입력해야만 해당 API 호출이 가능합니다.")
    @PostMapping("/signup/kakao")
    public HttpRes<String> kakaoSignUp(@RequestBody UserReq.KakaoSignUpDto kakaoSignUpDto,
                                       HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        userService.doKakaoAdditionalSignUp(userId, kakaoSignUpDto);
        return new HttpRes<>("카카오 회원가입이 완료되었습니다.");
    }


    @ApiOperation(value = "카카오 로그인", notes = "카카오 계정으로 로그인을 합니다.")
    @PostMapping("/login/kakao")
    public HttpRes<UserRes.UserInfoWithToken> doKakaoLogIn(@RequestBody UserReq.KakaoLogInDto kakaoLogInDto) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        final String payload = new String(decoder.decode(kakaoLogInDto.getSnsId().split("\\.")[1]));
        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray;
        try {
            jsonArray = jsonParser.parseMap(payload);
        }
        catch(RuntimeException e) {
            throw new BadRequestException("Token 값을 parsing 하지 못하였습니다. 관리자에게 문의해주세요.");
        }
        if (!jsonArray.containsKey("sub")) {
            throw new ServerErrorException();
        }
        String snsId = jsonArray.get("sub").toString();
        boolean needsAdditionalSignUp = false;
        if(!userService.existsSnsUser("kakao", snsId)) {
            userService.doKakaoSignUp(snsId, kakaoLogInDto.getSnsToken());
            needsAdditionalSignUp = true;
        }
        UserRes.UserInfoWithToken userInfoWithToken = userService.doSocialLogIn("kakao", snsId);
        userInfoWithToken.setNeedsAdditionalSignUp(needsAdditionalSignUp);
        if(userInfoWithToken.getId().equals(0L)) {
            throw new BadRequestException("회원가입을 먼저 진행해주세요.");
        }
        return new HttpRes<>(userInfoWithToken);
    }


}
