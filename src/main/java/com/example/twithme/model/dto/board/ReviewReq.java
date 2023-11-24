package com.example.twithme.model.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReviewReq {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewCreateDto { //리뷰 생성
        private Long boardId;
        private String title;
        private String content;
        private String oneLine;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewCommentDto {
        private Long reviewId;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewLikeDto {
        private Long reviewId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewOptionDto{
        private Long continentId;
        private Long nationId;
        private Long regionId;
        private int startMonth;
        private int endMonth;
        private int totalPeopleNum;
        private String keyWord;
    }
}
