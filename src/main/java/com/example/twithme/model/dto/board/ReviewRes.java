package com.example.twithme.model.dto.board;

import com.example.twithme.model.entity.board.Review;
import com.example.twithme.model.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewRes {

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
    public static class TripylerWith{
        private Long userId;
        private String nickname;
        private String profileUrl;
        private int age;
        private String gender;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewDetailRes {

        //트리플러 정보
        private Long tripylerId;
        private String tripylerTitle;
        private String tripylerImage;
        private String nationName;
        private String regionName;

        private Integer recruitPeopleNum;
        private Integer totalPeopleNum;
        private LocalDate startDate;
        private LocalDate endDate;



        //작성자 정보
        private Long userId;
        private String nickname;
        private String profileUrl;
        private int age;
        private String gender;


        //유저 좋아요 여부
        private boolean tokenUserLiked;



        //같이 간 사람 리스트
        List<TripylerWith> tripylerWithList;


        //트리플러 해시태그 5개
        private String hashtag1;
        private String hashtag2;
        private String hashtag3;
        private String hashtag4;
        private String hashtag5;


        //리뷰 정보
        private Long reviewId;
        private LocalDateTime regDateTime;
        private String reviewTitle;
        private String reviewContent;
        private String reviewOneLine;
        List<String> reviewImageList;


        //이전 게시글, 다음 게시글 id, title
        private Long previousReviewId;
        private String previousTitle;
        private Long nextReviewId;
        private String nextTitle;




        //좋아요, 댓글 수, 조회수
        private Integer hits; //조회수
        private Integer likes; //좋아요
        private Integer commentsCnt; //댓글수

        private boolean isMyReview;




        public static ReviewDetailRes toDto(Review r){
            int age;
            User user = r.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }
            return ReviewDetailRes.builder()
                    //트리플러 정보
                    .tripylerId(r.getTripyler().getId())
                    .tripylerTitle(r.getTripyler().getTitle())
                    .tripylerImage(r.getTripyler().getImage())
//                    .nationName(r.getTripyler().getNation().getName())
//                    .regionName(r.getTripyler().getRegion().getName())
                    .recruitPeopleNum(r.getTripyler().getRecruitPeopleNum())
                    .totalPeopleNum(r.getTripyler().getTotalPeopleNum())
                    .startDate(r.getTripyler().getStartDate())
                    .endDate(r.getTripyler().getEndDate())

                    //리뷰 정보
                    .reviewId(r.getId())
                    .regDateTime(r.getRegDateTime())
                    .reviewTitle(r.getTitle())
                    .reviewContent(r.getContent())
                    .reviewOneLine(r.getOneLine())
                    .reviewImageList(null)


                    //작성자 정보
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileUrl(user.getProfileUrl())
                    .age(age)
                    .gender(user.getGender())

                    .tokenUserLiked(false)

                    //같이 간 사람 리스트
                    .tripylerWithList(null)

                    //트리플러 해시태그 5개
                    .hashtag1(null)
                    .hashtag2(null)
                    .hashtag3(null)
                    .hashtag4(null)
                    .hashtag5(null)



                    //이전 다음 게시글
                    .previousReviewId(null)
                    .previousTitle(null)
                    .nextReviewId(null)
                    .nextTitle(null)


                    //좋아요, 댓글 수, 조회수
                    .hits(r.getHits())
                    .likes(null)
                    .commentsCnt(null)


                    .build();

        }


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyReviewListDto {
        private Long reviewId;
        private String nationName;
        private String regionName;
        private String tripylerTitle;
        private String reviewTitle;
        private int likes;
        private int comments;
        private int hits;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> imageUrls;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewListDtoOrderByRegDateTime implements Comparable<ReviewListDtoOrderByRegDateTime> {
        private Long reviewId;
        private String nationName;
        private String regionName;
        private List<String> hashtags;
        private String title;
        private String content;
        private String image;
        private int likes;
        private int comments;
        private int hits;
        private LocalDateTime regDateTime; //등록시간

        private String userProfileUrl;
        private String username;
        private int age;
        private String gender;
        private LocalDate startDate;
        private LocalDate endDate;

        @Override
        public int compareTo(ReviewListDtoOrderByRegDateTime reviewListDtoOrderByRegDateTime) {
            return reviewListDtoOrderByRegDateTime.getRegDateTime().compareTo(this.regDateTime);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewListDtoOrderByLikes implements Comparable<ReviewListDtoOrderByLikes> {
        private Long reviewId;
        private String nationName;
        private String regionName;
        private List<String> hashtags;
        private String title;
        private String content;
        private String image;
        private int likes;
        private int comments;
        private int hits;
        private LocalDateTime regDateTime; //등록시간

        private String userProfileUrl;
        private String username;
        private int age;
        private String gender;
        private LocalDate startDate;
        private LocalDate endDate;

        @Override
        public int compareTo(ReviewListDtoOrderByLikes reviewListDtoOrderByLikes) {
            if(this.likes > reviewListDtoOrderByLikes.getLikes()) {
                return -1;
            }
            else if(this.likes < reviewListDtoOrderByLikes.getLikes()) {
                return 1;
            }
            return 0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewListDtoOrderByComments implements Comparable<ReviewListDtoOrderByComments> {
        private Long reviewId;
        private String nationName;
        private String regionName;
        private List<String> hashtags;
        private String title;
        private String content;
        private String image;
        private int likes;
        private int comments;
        private int hits;
        private LocalDateTime regDateTime; //등록시간

        private String userProfileUrl;
        private String username;
        private int age;
        private String gender;
        private LocalDate startDate;
        private LocalDate endDate;

        @Override
        public int compareTo(ReviewListDtoOrderByComments reviewListDtoOrderByComments) {
            if (this.comments > reviewListDtoOrderByComments.getComments()) {
                return -1;
            } else if (this.comments < reviewListDtoOrderByComments.getComments()) {
                return 1;
            }
            return 0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewListDtoOrderByHits implements Comparable<ReviewListDtoOrderByHits> {
        private Long reviewId;
        private String nationName;
        private String regionName;
        private List<String> hashtags;
        private String title;
        private String content;
        private String image;
        private int likes;
        private int comments;
        private int hits;
        private LocalDateTime regDateTime; //등록시간

        private String userProfileUrl;
        private String username;
        private int age;
        private String gender;
        private LocalDate startDate;
        private LocalDate endDate;

        @Override
        public int compareTo(ReviewListDtoOrderByHits reviewListDtoOrderByHits) {
            if(this.hits > reviewListDtoOrderByHits.getHits()) {
                return -1;
            }
            else if(this.hits < reviewListDtoOrderByHits.getHits()) {
                return 1;
            }
            return 0;
        }
    }

}
