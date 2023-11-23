package com.example.twithme.controller;


import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.model.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Api(tags={"00. Controller 예시"})
@RequestMapping("/test")
@RestController
public class TestController {


    @ApiOperation(value = "컨트롤러 예시", notes = "null 입력하면 400오류 발생, 입력하지 않으면 200 성공")
    @GetMapping("")
    public ApiResponse<String> test(@RequestParam(required = false) String s){

        if("null".equals(s))
            throw new BadRequestException("예외 처리한 경우");

        return new ApiResponse<>("test 성공");
    }



}
