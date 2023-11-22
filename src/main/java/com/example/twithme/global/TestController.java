package com.example.twithme.global;

import com.example.dango.global.entity.ApiResponse;
import com.example.dango.global.exception.BadRequestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags={"00.test"})
@RequestMapping("/test")
public class TestController {


    @ApiOperation(value = "API 응답 반환 형식 예", notes = "null이라고 입력하면 오류 반환, 다른 문자열 입력 시 정상 반환")
    @GetMapping("")
    public ApiResponse<String> test2(@RequestParam(required = false) String s){

        if("null".equals(s))
            throw new BadRequestException("오류 던지기");

        return new ApiResponse<>("데이터가 들어갈 자리(문자열, 객체 등");
    }

}
