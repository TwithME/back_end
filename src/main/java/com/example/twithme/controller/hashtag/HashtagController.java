package com.example.twithme.controller.hashtag;

import com.example.twithme.common.exception.dto.HttpRes;
import com.example.twithme.model.dto.hashtag.HashtagRes;
import com.example.twithme.service.hashtag.HashtagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags={"03.Hashtag"})
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/hashtag", produces = MediaType.APPLICATION_JSON_VALUE)
public class HashtagController {

    private final HashtagService hashtagService;

    @ApiOperation(value = "해시태그 검색", notes = "해당 키워드를 포함하는 해시태그를 검색합니다.")
    @GetMapping("")
    public HttpRes<List<HashtagRes.HashtagDto>> getSearchList(@RequestParam String name) {
        return new HttpRes<>(hashtagService.readHashtagSearchList(name));
    }

    @ApiOperation(value = "해시태그 목록 조회", notes = "모든 해시태그들의 목록을 조회합니다.")
    @GetMapping("/list")
    public HttpRes<List<HashtagRes.HashtagDto>> getList() {
        return new HttpRes<>(hashtagService.readHashtagList());
    }

//    @ApiOperation(value = "신규 해시태그 등록", notes = "존재하지 않는 해시태그를 새로 등록합니다.")
//    @PostMapping("")
//    public HashtagRes.HashtagDto createHashtag(@RequestBody HashtagReq.HashtagNameDto hashtagNameDto) {
//        return hashtagService.createHashtag(hashtagNameDto.getName());
//    }
}
