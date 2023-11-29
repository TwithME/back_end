package com.example.twithme.model.dto.board;

import com.example.twithme.model.dto.hashtag.HashtagRes;
import com.example.twithme.model.entity.board.Board;
import com.example.twithme.model.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BoardRes {

    // 4개의 DTO가 형식은 모두 같지만 compareTo를 다르게 override 해야하기 때문에 따로 존재
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardListOrderByRegDateTime implements Comparable<BoardListOrderByRegDateTime> {
        // 검색 조건에 맞는 게시물들 보여주는 dto
        private Long boardId;
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
        public int compareTo(BoardListOrderByRegDateTime boardListOrderByRegDateTime) {
            return boardListOrderByRegDateTime.getRegDateTime().compareTo(this.regDateTime);
        }

        public static BoardListOrderByRegDateTime toDto(Board board, int likes, int comments, int age, List<String> hashtagList) {
            String nationName;
            try {
                nationName = board.getNation().getName();
            }
            catch(NullPointerException e) {
                nationName = null;
            }

            String regionName;
            try {
                regionName = board.getRegion().getName();
            }
            catch(NullPointerException e) {
                regionName = null;
            }
            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }

            return BoardListOrderByRegDateTime.builder()
                    .boardId(board.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .title(board.getTitle())
                    .content(board.getContent())
                    .recruitPeopleNum(board.getRecruitPeopleNum())
                    .totalPeopleNum(board.getTotalPeopleNum())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .nickname(board.getWriter().getNickname())
                    .profileUrl(board.getWriter().getProfileUrl())
                    .age(age)
                    .regDateTime(board.getRegDateTime())
                    .likes(likes)
                    .comments(comments)
                    .hits(board.getHits())
                    .imageUrl(imageUrl)
                    .gender(board.getWriter().getGender())
                    .hashtag(hashtagList)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardListOrderByLikes implements Comparable<BoardListOrderByLikes> {
        private Long boardId;
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
        public int compareTo(BoardListOrderByLikes boardListOrderByLikes) {
            if(this.likes > boardListOrderByLikes.getLikes()) {
                return -1;
            }
            else if(this.likes < boardListOrderByLikes.getLikes()) {
                return 1;
            }
            return 0;
        }

        public static BoardListOrderByLikes toDto(Board board, int likes, int comments, int age, List<String> hashtagList) {
            String nationName;
            try {
                nationName = board.getNation().getName();
            }
            catch(NullPointerException e) {
                nationName = null;
            }

            String regionName;
            try {
                regionName = board.getRegion().getName();
            }
            catch(NullPointerException e) {
                regionName = null;
            }
            String imageUrl = regionName == null ? null : board.getRegion().getImageUrl();

            return BoardListOrderByLikes.builder()
                    .boardId(board.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .title(board.getTitle())
                    .content(board.getContent())
                    .recruitPeopleNum(board.getRecruitPeopleNum())
                    .totalPeopleNum(board.getTotalPeopleNum())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .nickname(board.getWriter().getNickname())
                    .profileUrl(board.getWriter().getProfileUrl())
                    .age(age)
                    .regDateTime(board.getRegDateTime())
                    .likes(likes)
                    .comments(comments)
                    .hits(board.getHits())
                    .imageUrl(imageUrl)
                    .gender(board.getWriter().getGender())
                    .hashtag(hashtagList)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardListOrderByComments implements Comparable<BoardListOrderByComments> {
        private Long boardId;
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
        public int compareTo(BoardListOrderByComments boardListOrderByComments) {
            if(this.comments > boardListOrderByComments.getComments()) {
                return -1;
            }
            else if(this.comments < boardListOrderByComments.getComments()) {
                return 1;
            }
            return 0;
        }

        public static BoardListOrderByComments toDto(Board board, int likes, int comments, int age, List<String> hashtagList) {
            String nationName;
            try {
                nationName = board.getNation().getName();
            }
            catch(NullPointerException e) {
                nationName = null;
            }

            String regionName;
            try {
                regionName = board.getRegion().getName();
            }
            catch(NullPointerException e) {
                regionName = null;
            }
            String imageUrl = regionName == null ? null : board.getRegion().getImageUrl();

            return BoardListOrderByComments.builder()
                    .boardId(board.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .title(board.getTitle())
                    .content(board.getContent())
                    .recruitPeopleNum(board.getRecruitPeopleNum())
                    .totalPeopleNum(board.getTotalPeopleNum())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .nickname(board.getWriter().getNickname())
                    .profileUrl(board.getWriter().getProfileUrl())
                    .age(age)
                    .regDateTime(board.getRegDateTime())
                    .likes(likes)
                    .comments(comments)
                    .hits(board.getHits())
                    .imageUrl(imageUrl)
                    .gender(board.getWriter().getGender())
                    .hashtag(hashtagList)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardListOrderByHits implements Comparable<BoardListOrderByHits> {
        private Long boardId;
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
        public int compareTo(BoardListOrderByHits boardListOrderByHits) {
            if(this.hits > boardListOrderByHits.getHits()) {
                return -1;
            }
            else if(this.hits < boardListOrderByHits.getHits()) {
                return 1;
            }
            return 0;
        }

        public static BoardListOrderByHits toDto(Board board, int likes, int comments, int age, List<String> hashtagList) {
            String nationName;
            try {
                nationName = board.getNation().getName();
            }
            catch(NullPointerException e) {
                nationName = null;
            }

            String regionName;
            try {
                regionName = board.getRegion().getName();
            }
            catch(NullPointerException e) {
                regionName = null;
            }
            String imageUrl = regionName == null ? null : board.getRegion().getImageUrl();

            return BoardListOrderByHits.builder()
                    .boardId(board.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .title(board.getTitle())
                    .content(board.getContent())
                    .recruitPeopleNum(board.getRecruitPeopleNum())
                    .totalPeopleNum(board.getTotalPeopleNum())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .nickname(board.getWriter().getNickname())
                    .profileUrl(board.getWriter().getProfileUrl())
                    .age(age)
                    .regDateTime(board.getRegDateTime())
                    .likes(likes)
                    .comments(comments)
                    .hits(board.getHits())
                    .imageUrl(imageUrl)
                    .gender(board.getWriter().getGender())
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
        private Long boardId;
        private String title;
        private String content;
        private String image;
        private String nationName;
        private String regionName;
        private String boardImage;

        private int estimatedPrice;
        private Integer recruitPeopleNum;
        private Integer totalPeopleNum;
        private LocalDate startDate;
        private LocalDate endDate;


        private Integer hits; //조회수
        private Integer likes; //좋아요
        private Integer commentsCnt; //댓글수

        //내가 작성한 트리플러인지
        private boolean isMyBoard;

        //해시태그 리스트
        private List<HashtagRes.HashtagDto> hashtagList;

        private LocalDateTime regDateTime; //게시글 등록 날짜, 시간


        //동행 리스트
        List<ReviewRes.BoardWith> withList;


        //작성자 정보
        private Long userId;
        private String nickname;
        private String profileUrl;
        private int age;
        private String gender;

        //유저 좋아요 여부
        private boolean tokenUserLiked;



        //이전 다음 게시글
        private Long previousBoardId;
        private String previousTitle;
        private Long nextBoardId;
        private String nextTitle;



        public static BoardDetailRes toDto(Board t){
            int age;
            User user = t.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }
            return BoardDetailRes.builder()
                    .boardId(t.getId())
                    .title(t.getTitle())
                    .content(t.getContent())
                    .image(t.getImage())
                    .nationName(null)
                    .regionName(null)
                    .boardImage(t.getImage())
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
                    .previousBoardId(null)
                    .previousTitle(null)
                    .nextBoardId(null)
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
        private Long boardApplyId;
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
    public static class MyBoardApplyListDto {
        private Long userId;
        private Long boardId;
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
    public static class MyBoardListDto {
        private Long boardId;
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
    public static class MyBoardTitleDto implements Comparable<MyBoardTitleDto>{
        private Long boardId;
        private String title;

        //여행지, 여행일정, 동행 트리플러 목록도
        private String nationName;
        private String regionName;
        private LocalDate startDate;
        private LocalDate endDate;

        List<ReviewRes.BoardWith> withList;

        private LocalDateTime regDateTime; //등록시간


        @Override
        public int compareTo(MyBoardTitleDto myBoardTitleDto) {
              return myBoardTitleDto.getRegDateTime().compareTo(this.regDateTime);
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
