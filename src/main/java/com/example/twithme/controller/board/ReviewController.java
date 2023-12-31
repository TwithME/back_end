package com.example.twithme.controller.board;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.model.ApiResponse;
import com.example.twithme.model.dto.board.ReviewReq;
import com.example.twithme.model.dto.board.ReviewRes;
import com.example.twithme.model.entity.board.Review;
import com.example.twithme.service.board.ReviewService;
import com.example.twithme.service.board.BoardService;
import com.example.twithme.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags={"08.Review"})
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/review", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReviewController {
    private final BoardService boardService;
    private final UserService userService;

    private final ReviewService reviewService;

    @ApiOperation(value = "리뷰 작성", notes = "")
    @PostMapping("")
    public ApiResponse<Long> createReview(@RequestBody ReviewReq.ReviewCreateDto reviewCreateDto,
                                          HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        Long reviewId = reviewService.createReview(userId, reviewCreateDto);
        return new ApiResponse<>(reviewId);
    }

    @ApiOperation(value = "리뷰 사진 업로드", notes = "리뷰의 사진을 업로드합니다.")
    @PostMapping("/{reviewId}/picture")
    public ApiResponse<String> uploadProfileImage(@PathVariable Long reviewId,
                                                  @RequestPart(name = "images", required = true) MultipartFile multipartFile,
                                                  HttpServletRequest httpServletRequest) {
        reviewService.uploadReviewImage(reviewId, multipartFile);
        return new ApiResponse<>("리뷰 사진 등록이 성공적으로 완료되었습니다.");
    }

    @ApiOperation(value = "리뷰 댓글 리스트 반환", notes = "리뷰 댓글 리스트 반환")
    @GetMapping("/{reviewId}/comment/list")
    public ApiResponse<List<ReviewRes.CommentRes>> getComments(@PathVariable Long reviewId, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        List<ReviewRes.CommentRes> result = reviewService.getReviewComments(reviewId);
        return new ApiResponse<>(result);
    }

    @ApiOperation(value = "리뷰 한 개 조회", notes = "리뷰 한 개 조회")
    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewRes.ReviewDetailRes> getBoardDetail(@PathVariable Long reviewId, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        ReviewRes.ReviewDetailRes result = reviewService.getReviewDetail(userId, reviewId);
        return new ApiResponse<>(result);
    }


    @ApiOperation(value = "리뷰 댓글 기능", notes = "리뷰 게시물에 댓글을 작성합니다.")
    @PostMapping("/comment")
    public ApiResponse<String> createComment(@RequestBody ReviewReq.ReviewCommentDto reviewCommentDto, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        reviewService.comment(reviewCommentDto.getReviewId(), userId, reviewCommentDto.getContent());
        return new ApiResponse<>("댓글 등록이 성공적으로 완료되었습니다.");
    }


    @ApiOperation(value = "리뷰 좋아요 기능", notes = "리뷰 게시물에 좋아요를 누릅니다.")
    @PostMapping("/like")
    public ApiResponse<String> createLike(@RequestBody ReviewReq.ReviewLikeDto reviewLikeDto, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        reviewService.like(reviewLikeDto.getReviewId(), userId);
        return new ApiResponse<>("좋아요 등록이 성공적으로 완료되었습니다.");
    }

    @ApiOperation(value = "여행 후기 필터링", notes = "선택한 옵션에 대해 해당하는 후기를 보여줍니다.\n" +
            "최신순(1), 좋아요순(2), 댓글순(3), 조회순(4)")
    @PostMapping("/list")
    public Object getReviewList(@RequestBody ReviewReq.ReviewOptionDto reviewOptionDto,
                                                                @RequestParam(name = "option") int option){
        List<Review> reviews = reviewService.getReviewList(reviewOptionDto);

        //최신순(1), 좋아요순(2), 댓글순(3), 조회순(4)
        if (option == 1) {
            return new ApiResponse<>(reviewService.getReviewListOrderByRegDateTime(reviews));
        }
        else if (option == 2) {
            return new ApiResponse<>(reviewService.getReviewListOrderByLikes(reviews));
        }
        else if (option == 3) {
            return new ApiResponse<>(reviewService.getReviewListOrderByComments(reviews));
        }
        else if (option == 4) {
            return new ApiResponse<>(reviewService.getReviewListOrderByHits(reviews));
        }

        throw new BadRequestException("isRecruiting 또는 option의 값이 유효하지 않습니다.");
    }

    @ApiOperation(value = "ID로 회원 검색", notes = "ID 값으로 회원의 DB index 값을 받습니다.\n" +
            "리턴 값이 0이라면 존재하지 않는 회원,\n" +
            "0이 아니라면 해당 user의 id 값입니다.\n")
    @GetMapping("/find-user")
    public ApiResponse<Long> searchUser(@RequestParam String username) {
        return new ApiResponse<>(userService.findUserIdByUsername(username));
    }
}
