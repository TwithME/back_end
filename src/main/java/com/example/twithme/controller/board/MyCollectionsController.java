package com.example.twithme.controller.board;

import com.example.twithme.common.exception.dto.HttpRes;
import com.example.twithme.model.dto.board.ReviewRes;
import com.example.twithme.model.dto.board.TripylerRes;
import com.example.twithme.service.board.ReviewService;
import com.example.twithme.service.board.TripylerService;
import com.example.twithme.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags={"09.My Collections"})
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/my-collections", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyCollectionsController {
    private final TripylerService tripylerService;
    private final ReviewService reviewService;
    private final UserService userService;

    @ApiOperation(value = "내가 신청한 Tripyler", notes = "내가 신청한 Tripyler 목록을 조회합니다.")
    @GetMapping("/tripyler-apply-list")
    HttpRes<List<TripylerRes.MyTripylerApplyListDto>> getMyTriplerApplyList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new HttpRes<>(tripylerService.findTripylerApplyByApplicantId(userId));
    }

    @ApiOperation(value = "내가 찜한 Tripyler", notes = "내가 찜한 Tripyler 목록을 조회합니다.")
    @GetMapping("/tripyler-like-list")
    HttpRes<List<TripylerRes.MyTripylerApplyListDto>> getMyTriplerLikeList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new HttpRes<>(tripylerService.findTripylerByLike(userId));
    }

    @ApiOperation(value = "내가 찜한 Triplog", notes = "내가 찜한 Triplog 목록을 조회합니다.")
    @GetMapping("/review-like-list")
    HttpRes<List<TripylerRes.MyTripylerApplyListDto>> getMyReviewLikeList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new HttpRes<>(reviewService.findReviewByLike(userId));
    }

    @ApiOperation(value = "My Tripyler들", notes = "내가 작성한 Tripyler 목록을 조회합니다.")
    @GetMapping("/my-tripylers")
    HttpRes<List<TripylerRes.MyTripylerListDto>> getMyTripylerList(@RequestParam int year,
            HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new HttpRes<>(tripylerService.myTripylerWithYear(year, userId));
    }

    @ApiOperation(value = "My 여행후기들", notes = "내가 작성한 여행후기 목록을 조회합니다.")
    @GetMapping("/my-reviews")
    HttpRes<List<ReviewRes.MyReviewListDto>> getMyReviewList(@RequestParam int year,
                                                             HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new HttpRes<>(reviewService.myReviewWithYear(year, userId));
    }

    @ApiOperation(value = "My Tripyler들 제목 리스트(작성한 것, 참여한 것 전부)", notes = "My Tripyler들(작성한 것, 참여한 것 전부)")
    @GetMapping("/my-all-tripylers")
    HttpRes<List<TripylerRes.MyTripylerTitleDto>> getAllMyTripylerList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new HttpRes<>(tripylerService.myAllTripylers(userId));
    }


}
