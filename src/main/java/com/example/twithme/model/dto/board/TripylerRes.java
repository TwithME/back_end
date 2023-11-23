package com.example.twithme.model.dto.board;

import com.example.twithme.model.dto.hashtag.HashtagRes;
import com.example.twithme.model.entity.board.Tripyler;
import com.example.twithme.model.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TripylerRes {

    // 4개의 DTO가 형식은 모두 같지만 compareTo를 다르게 override 해야하기 때문에 따로 존재
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripylerListOrderByRegDateTime implements Comparable<TripylerListOrderByRegDateTime> {
        // 검색 조건에 맞는 게시물들 보여주는 dto
        private Long tripylerId;
        private String nationName;
        private String regionName;
        private Integer recruitPeopleNum;
        private Integer totalPeopleNum;
        private LocalDate startDate;
        private LocalDate endDate;
        private String title;
        private String content;
        private String nickname;
        private String profileUrl;
        private int age;
        private LocalDateTime regDateTime;
        private int likes;
        private int comments;
        private int hits;


        private String imageUrl;
        private String gender;
        private List<String> hashtag;

        @Override
        public int compareTo(TripylerListOrderByRegDateTime tripylerListOrderByRegDateTime) {
            return tripylerListOrderByRegDateTime.getRegDateTime().compareTo(this.regDateTime);
        }

        public static TripylerListOrderByRegDateTime toDto(Tripyler tripyler, int likes, int comments, int age, List<String> hashtagList) {
            String nationName;
            try {
                nationName = tripyler.getNation().getName();
            }
            catch(NullPointerException e) {
                nationName = null;
            }

            String regionName;
            try {
                regionName = tripyler.getRegion().getName();
            }
            catch(NullPointerException e) {
                regionName = null;
            }
            String imageUrl;
            if(tripyler.getImage() == null) {
                imageUrl = regionName == null ? null : tripyler.getRegion().getImageUrl();
            }
            else {
                imageUrl = tripyler.getImage();
            }

            return TripylerListOrderByRegDateTime.builder()
                    .tripylerId(tripyler.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .title(tripyler.getTitle())
                    .content(tripyler.getContent())
                    .recruitPeopleNum(tripyler.getRecruitPeopleNum())
                    .totalPeopleNum(tripyler.getTotalPeopleNum())
                    .startDate(tripyler.getStartDate())
                    .endDate(tripyler.getEndDate())
                    .nickname(tripyler.getWriter().getNickname())
                    .profileUrl(tripyler.getWriter().getProfileUrl())
                    .age(age)
                    .regDateTime(tripyler.getRegDateTime())
                    .likes(likes)
                    .comments(comments)
                    .hits(tripyler.getHits())
                    .imageUrl(imageUrl)
                    .gender(tripyler.getWriter().getGender())
                    .hashtag(hashtagList)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripylerListOrderByLikes implements Comparable<TripylerListOrderByLikes> {
        private Long tripylerId;
        private String nationName;
        private String regionName;
        private Integer recruitPeopleNum;
        private Integer totalPeopleNum;
        private LocalDate startDate;
        private LocalDate endDate;
        private String title;
        private String content;
        private String nickname;
        private String profileUrl;
        private int age;
        private LocalDateTime regDateTime;
        private int likes;
        private int comments;
        private int hits;

        private String imageUrl;
        private String gender;
        private List<String> hashtag;

        @Override
        public int compareTo(TripylerListOrderByLikes tripylerListOrderByLikes) {
            if(this.likes > tripylerListOrderByLikes.getLikes()) {
                return -1;
            }
            else if(this.likes < tripylerListOrderByLikes.getLikes()) {
                return 1;
            }
            return 0;
        }

        public static TripylerListOrderByLikes toDto(Tripyler tripyler, int likes, int comments, int age, List<String> hashtagList) {
            String nationName;
            try {
                nationName = tripyler.getNation().getName();
            }
            catch(NullPointerException e) {
                nationName = null;
            }

            String regionName;
            try {
                regionName = tripyler.getRegion().getName();
            }
            catch(NullPointerException e) {
                regionName = null;
            }
            String imageUrl = regionName == null ? null : tripyler.getRegion().getImageUrl();

            return TripylerListOrderByLikes.builder()
                    .tripylerId(tripyler.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .title(tripyler.getTitle())
                    .content(tripyler.getContent())
                    .recruitPeopleNum(tripyler.getRecruitPeopleNum())
                    .totalPeopleNum(tripyler.getTotalPeopleNum())
                    .startDate(tripyler.getStartDate())
                    .endDate(tripyler.getEndDate())
                    .nickname(tripyler.getWriter().getNickname())
                    .profileUrl(tripyler.getWriter().getProfileUrl())
                    .age(age)
                    .regDateTime(tripyler.getRegDateTime())
                    .likes(likes)
                    .comments(comments)
                    .hits(tripyler.getHits())
                    .imageUrl(imageUrl)
                    .gender(tripyler.getWriter().getGender())
                    .hashtag(hashtagList)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripylerListOrderByComments implements Comparable<TripylerListOrderByComments> {
        private Long tripylerId;
        private String nationName;
        private String regionName;
        private Integer recruitPeopleNum;
        private Integer totalPeopleNum;
        private LocalDate startDate;
        private LocalDate endDate;
        private String title;
        private String content;
        private String nickname;
        private String profileUrl;
        private int age;
        private LocalDateTime regDateTime;
        private int likes;
        private int comments;
        private int hits;

        private String imageUrl;
        private String gender;
        private List<String> hashtag;

        @Override
        public int compareTo(TripylerListOrderByComments tripylerListOrderByComments) {
            if(this.comments > tripylerListOrderByComments.getComments()) {
                return -1;
            }
            else if(this.comments < tripylerListOrderByComments.getComments()) {
                return 1;
            }
            return 0;
        }

        public static TripylerListOrderByComments toDto(Tripyler tripyler, int likes, int comments, int age, List<String> hashtagList) {
            String nationName;
            try {
                nationName = tripyler.getNation().getName();
            }
            catch(NullPointerException e) {
                nationName = null;
            }

            String regionName;
            try {
                regionName = tripyler.getRegion().getName();
            }
            catch(NullPointerException e) {
                regionName = null;
            }
            String imageUrl = regionName == null ? null : tripyler.getRegion().getImageUrl();

            return TripylerListOrderByComments.builder()
                    .tripylerId(tripyler.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .title(tripyler.getTitle())
                    .content(tripyler.getContent())
                    .recruitPeopleNum(tripyler.getRecruitPeopleNum())
                    .totalPeopleNum(tripyler.getTotalPeopleNum())
                    .startDate(tripyler.getStartDate())
                    .endDate(tripyler.getEndDate())
                    .nickname(tripyler.getWriter().getNickname())
                    .profileUrl(tripyler.getWriter().getProfileUrl())
                    .age(age)
                    .regDateTime(tripyler.getRegDateTime())
                    .likes(likes)
                    .comments(comments)
                    .hits(tripyler.getHits())
                    .imageUrl(imageUrl)
                    .gender(tripyler.getWriter().getGender())
                    .hashtag(hashtagList)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripylerListOrderByHits implements Comparable<TripylerListOrderByHits> {
        private Long tripylerId;
        private String nationName;
        private String regionName;
        private Integer recruitPeopleNum;
        private Integer totalPeopleNum;
        private LocalDate startDate;
        private LocalDate endDate;
        private String title;
        private String content;
        private String nickname;
        private String profileUrl;
        private int age;
        private LocalDateTime regDateTime;
        private int likes;
        private int comments;
        private int hits;

        private String imageUrl;
        private String gender;
        private List<String> hashtag;

        @Override
        public int compareTo(TripylerListOrderByHits tripylerListOrderByHits) {
            if(this.hits > tripylerListOrderByHits.getHits()) {
                return -1;
            }
            else if(this.hits < tripylerListOrderByHits.getHits()) {
                return 1;
            }
            return 0;
        }

        public static TripylerListOrderByHits toDto(Tripyler tripyler, int likes, int comments, int age, List<String> hashtagList) {
            String nationName;
            try {
                nationName = tripyler.getNation().getName();
            }
            catch(NullPointerException e) {
                nationName = null;
            }

            String regionName;
            try {
                regionName = tripyler.getRegion().getName();
            }
            catch(NullPointerException e) {
                regionName = null;
            }
            String imageUrl = regionName == null ? null : tripyler.getRegion().getImageUrl();

            return TripylerListOrderByHits.builder()
                    .tripylerId(tripyler.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .title(tripyler.getTitle())
                    .content(tripyler.getContent())
                    .recruitPeopleNum(tripyler.getRecruitPeopleNum())
                    .totalPeopleNum(tripyler.getTotalPeopleNum())
                    .startDate(tripyler.getStartDate())
                    .endDate(tripyler.getEndDate())
                    .nickname(tripyler.getWriter().getNickname())
                    .profileUrl(tripyler.getWriter().getProfileUrl())
                    .age(age)
                    .regDateTime(tripyler.getRegDateTime())
                    .likes(likes)
                    .comments(comments)
                    .hits(tripyler.getHits())
                    .imageUrl(imageUrl)
                    .gender(tripyler.getWriter().getGender())
                    .hashtag(hashtagList)
                    .build();
        }
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardDetailRes {

        //트리플러 정보
        private Long tripylerId;
        private String title;
        private String content;
        private String image;
        private String nationName;
        private String regionName;
        private String tripylerImage;

        private int estimatedPrice;
        private Integer recruitPeopleNum;
        private Integer totalPeopleNum;
        private LocalDate startDate;
        private LocalDate endDate;


        private Integer hits; //조회수
        private Integer likes; //좋아요
        private Integer commentsCnt; //댓글수

        //내가 작성한 트리플러인지
        private boolean isMyTripyler;

        //해시태그 리스트
        private List<HashtagRes.HashtagDto> hashtagList;

        private LocalDateTime regDateTime; //게시글 등록 날짜, 시간


        //동행 리스트
        List<ReviewRes.TripylerWith> tripylerWithList;


        //작성자 정보
        private Long userId;
        private String nickname;
        private String profileUrl;
        private int age;
        private String gender;

        //유저 좋아요 여부
        private boolean tokenUserLiked;



        //이전 다음 게시글
        private Long previousTripylerId;
        private String previousTitle;
        private Long nextTripylerId;
        private String nextTitle;



        public static BoardDetailRes toDto(Tripyler t){
            int age;
            User user = t.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }
            return BoardDetailRes.builder()
                    .tripylerId(t.getId())
                    .title(t.getTitle())
                    .content(t.getContent())
                    .image(t.getImage())
                    .nationName(null)
                    .regionName(null)
                    .tripylerImage(t.getImage())
                    .recruitPeopleNum(t.getRecruitPeopleNum())
                    .totalPeopleNum(t.getTotalPeopleNum())
                    .startDate(t.getStartDate())
                    .endDate(t.getEndDate())
                    .estimatedPrice(t.getEstimatedPrice())
                    //
                    .hits(t.getHits())
                    .likes(null)
                    .commentsCnt(null)
                    //
                    .hashtagList(null)
                    //
                    .regDateTime(t.getRegDateTime())
                    //작성자 정보
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileUrl(user.getProfileUrl())
                    .age(age)
                    .gender(user.getGender())
                    //이전 다음 게시글
                    .previousTripylerId(null)
                    .previousTitle(null)
                    .nextTripylerId(null)
                    .nextTitle(null)

                    .tokenUserLiked(false)

                    .build();

        }


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppliedListDto{
        private Long applicantId;
        private String nickname;
        private String profileUrl;
        private int age;
        private String gender;
        private List<String> hashtag;
        private Long tripylerApplyId;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentRes{
        private Long userId;
        private String nickname;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppliedDetailDto{
        private String title;
        private Long applicantId;
        private String nickname;
        private String profileUrl;
        private List<String> hashtag;
        private String content;
        private String gender;
        private int age;
        private int accepted;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyTripylerApplyListDto {
        private Long userId;
        private Long tripylerId;
        private String nationName;
        private String regionName;
        private LocalDate startDate;
        private LocalDate endDate;
        private int totalPeopleNum;
        private int likes;
        private int comments;
        private String hashtag1;
        private String hashtag2;
        private String hashtag3;
        private String hashtag4;
        private String hashtag5;
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyTripylerListDto {
        private Long tripylerId;
        private String nationName;
        private String regionName;
        private String title;
        private int recruitPeopleNum;
        private int totalPeopleNum;
        private int likes;
        private int comments;
        private int hits;
        private LocalDateTime regDateTime;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyTripylerTitleDto implements Comparable<MyTripylerTitleDto>{
        private Long tripylerId;
        private String title;

        //여행지, 여행일정, 동행 트리플러 목록도
        private String nationName;
        private String regionName;
        private LocalDate startDate;
        private LocalDate endDate;

        List<ReviewRes.TripylerWith> tripylerWithList;

        private LocalDateTime regDateTime; //등록시간


        @Override
        public int compareTo(MyTripylerTitleDto tripylerTitleDto) {
              return tripylerTitleDto.getRegDateTime().compareTo(this.regDateTime);
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class searchDestinationDto{
        private Long continentId;
        private Long nationId;
        private Long regionId;
        private String nationName;
        private String regionName;
    }

}
