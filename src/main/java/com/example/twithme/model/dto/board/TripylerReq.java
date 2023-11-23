package com.example.twithme.model.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class TripylerReq {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardOptionDto {
        private Long continentId;

        private Long nationId;

        private Long regionId;

        private LocalDate startDate;

        private LocalDate endDate;

        private Integer totalPeopleNum;

        private String keyWord;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripylerCreateDto {
        //지역
        private Long continentId;
        private Long nationId;
        private Long regionId;
        //여행일정
        private LocalDate startDate;
        private LocalDate endDate;
        //동행자 수
        private int totalPeopleNum;
        //해시태그 5개 id
        private Long firstTripStyleId;
        private Long secondTripStyleId;
        private Long thirdTripStyleId;
        private Long fourthTripStyleId;
        private Long fifthTripStyleId;

        private int estimatedPrice;

        //동행자 아이디만 문자열로 넣어주면 될 것 같아!
        List<String> tripylerWithList;

        String title;
        String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripylerLikeDto {
        Long tripylerId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripylerCommentDto {
        Long tripylerId;
        String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripylerApplyDto{
        Long tripylerId;
        String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class searchKeywordDto{
        private String regionName;
    }





}
