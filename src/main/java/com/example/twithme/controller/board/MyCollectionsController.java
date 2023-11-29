package com.example.twithme.controller.board;

import com.example.twithme.common.model.ApiResponse;
import com.example.twithme.model.dto.board.ReviewRes;
import com.example.twithme.model.dto.board.BoardRes;
import com.example.twithme.service.board.ReviewService;
import com.example.twithme.service.board.BoardService;
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
    private final BoardService boardService;
    private final ReviewService reviewService;
    private final UserService userService;

    @ApiOperation(value = "내가 신청한 목록", notes = "내가 신청한 목록을 조회합니다.")
    @GetMapping("/board-apply-list")
    ApiResponse<List<BoardRes.MyBoardApplyListDto>> getMyBoardApplyList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new ApiResponse<>(boardService.findBoardApplyByApplicantId(userId));
    }

    @ApiOperation(value = "내가 찜한 board", notes = "내가 찜한 목록을 조회합니다.")
    @GetMapping("/board-like-list")
    ApiResponse<List<BoardRes.MyBoardApplyListDto>> getMyBoardLikeList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new ApiResponse<>(boardService.findBoardByLike(userId));
    }

    @ApiOperation(value = "내가 찜한 Triplog", notes = "내가 찜한 Triplog 목록을 조회합니다.")
    @GetMapping("/triplog-like-list")
    ApiResponse<List<BoardRes.MyBoardApplyListDto>> getMyReviewLikeList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new ApiResponse<>(reviewService.findReviewByLike(userId));
    }

    @ApiOperation(value = "내가 작성한 여행 동행자 모집글", notes = "내가 작성한 목록을 조회합니다.")
    @GetMapping("/my-boards")
    ApiResponse<List<BoardRes.MyBoardListDto>> getMyBoardList(@RequestParam int year,
                                                              HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new ApiResponse<>(boardService.myBoardWithYear(year, userId));
    }

    @ApiOperation(value = "My 여행후기들", notes = "내가 작성한 여행후기 목록을 조회합니다.")
    @GetMapping("/my-reviews")
    ApiResponse<List<ReviewRes.MyReviewListDto>> getMyReviewList(@RequestParam int year,
                                                                 HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new ApiResponse<>(reviewService.myReviewWithYear(year, userId));
    }

    @ApiOperation(value = "My boards 제목 리스트(작성한 것, 참여한 것 전부)", notes = "작성한 것, 참여한 것 전부")
    @GetMapping("/my-all-boards")
    ApiResponse<List<BoardRes.MyBoardTitleDto>> getAllMyBoardList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return new ApiResponse<>(boardService.myAllBoards(userId));
    }


}
