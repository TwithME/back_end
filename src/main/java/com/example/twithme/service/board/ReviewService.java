package com.example.twithme.service.board;

import com.example.twithme.common.exception.NotFoundException;
import com.example.twithme.service.S3Service;
import com.example.twithme.model.dto.board.ReviewReq;
import com.example.twithme.model.dto.board.ReviewRes;
import com.example.twithme.model.dto.board.BoardRes;
import com.example.twithme.model.entity.board.*;
import com.example.twithme.model.entity.destination.Nation;
import com.example.twithme.model.entity.destination.Region;
import com.example.twithme.model.entity.hashtag.Hashtag;
import com.example.twithme.model.entity.user.User;
import com.example.twithme.repository.board.*;
import com.example.twithme.repository.destination.ContinentRepository;
import com.example.twithme.repository.destination.NationRepository;
import com.example.twithme.repository.destination.RegionRepository;
import com.example.twithme.repository.hashtag.HashtagRepository;
import com.example.twithme.repository.user.UserRepository;
import com.example.twithme.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final UserService userService;

    private final S3Service s3Service;

    private final UserRepository userRepository;

    private final ContinentRepository continentRepository;

    private final NationRepository nationRepository;

    private final RegionRepository regionRepository;

    private final HashtagRepository hashtagRepository;

    private final BoardRepository boardRepository;

    private final BoardLikeRepository boardLikeRepository;

    private final BoardCommentRepository boardCommentRepository;

    private final BoardHashtagRepository boardHashtagRepository;

    private final BoardApplyRepository boardApplyRepository;

    private final ReviewRepository reviewRepository;

    private final ReviewCommentRepository reviewCommentRepository;

    private final ReviewLikeRepository reviewLikeRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final BoardService boardService;


    public Review getReviewByReviewId(Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if(optionalReview.isEmpty()) {
            throw new NotFoundException("존재하지 않는 후기입니다.");
        }
        return optionalReview.get();
    }


    public Long createReview(Long userId, ReviewReq.ReviewCreateDto reviewCreateDto) {
        Board board = boardService.getTripylerByTripylerId(reviewCreateDto.getBoardId());
        User user = userService.getUserByUserId(userId);

        Review review = Review.builder()
                .board(board)
                .writer(user)
                .title(reviewCreateDto.getTitle())
                .content(reviewCreateDto.getContent())
                .oneLine(reviewCreateDto.getOneLine())
                .build();
        reviewRepository.save(review);
        return review.getId();
    }

    public void uploadReviewImage(Long reviewId, MultipartFile multipartFile) {
        Review review = getReviewByReviewId(reviewId);
        //String url = s3Service.uploadImage("review", review.getId().toString(), multipartFile);

        ReviewImage e = ReviewImage.builder()
                .review(review)
                .url("url")
                .build();

        reviewImageRepository.save(e);
    }



    public List<ReviewRes.CommentRes> getReviewComments(Long reviewId) {
        List<ReviewRes.CommentRes> result = new ArrayList<>();
        Review review = getReviewByReviewId(reviewId);
        List<ReviewComment> reviewComments = reviewCommentRepository.findAllByReviewOrderByRegDateTimeDesc(review);

        for(ReviewComment reviewComment : reviewComments){
            ReviewRes.CommentRes e = ReviewRes.CommentRes.builder()
                    .userId(reviewComment.getCommenter().getId())
                    .nickname(reviewComment.getCommenter().getNickname())
                    .content(reviewComment.getContent())
                    .build();
            result.add(e);
        }

        return result;
    }

    public void comment(Long reviewId, Long userId, String content) {
        User user = userService.getUserByUserId(userId);
        Review review = getReviewByReviewId(reviewId);
        ReviewComment reviewComment = ReviewComment.builder()
                .review(review)
                .commenter(user)
                .content(content)
                .build();
        reviewCommentRepository.save(reviewComment);
    }

    public void like(Long reviewId, Long userId) {
        User user = userService.getUserByUserId(userId);
        Review review = getReviewByReviewId(reviewId);
        Optional<ReviewLike> optionalReviewLike = Optional.ofNullable(reviewLikeRepository.findByReviewAndUser(review, user));

        if(optionalReviewLike.isPresent()) {
            ReviewLike reviewLike = optionalReviewLike.get();
            reviewLike.setDeleteYn(true);
        }
        else {
            ReviewLike reviewLike = ReviewLike.builder()
                    .review(review)
                    .user(user)
                    .build();
            reviewLikeRepository.save(reviewLike);
        }
    }

    public ReviewRes.ReviewDetailRes getReviewDetail(Long tokenUserId, Long reviewId) {
        Review review = getReviewByReviewId(reviewId);
        Board board = review.getBoard();
        List<BoardHashtag> boardHashtags = boardHashtagRepository.findByBoard(board);


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


        //좋아요, 댓글수, 같이 간 사람 리스트, 리뷰 이미지 리스트,
        int likes = reviewLikeRepository.countByReview(review);
        int commentsCnt = reviewCommentRepository.countByReview(review);

        List<String> reviewImageList = new ArrayList<>();
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReview(review);
        for(ReviewImage reviewImage : reviewImages){
            String url = reviewImage.getUrl();
            reviewImageList.add(url);
        }



        //동행한 사람 리스트
        List<ReviewRes.BoardWith> boardWithList = new ArrayList<>();
        List<BoardApply> boardApplyList = boardApplyRepository.findByBoard(board);

        for(BoardApply boardApply : boardApplyList){
            if(boardApply.getAccepted() == 1){
                int age;
                User applicant = boardApply.getApplicant();
                if(applicant.getBirthDate() == null || applicant.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                    age = 0;
                } else {
                    age = LocalDate.now().getYear() - applicant.getBirthDate().getYear() + 1;
                }

                ReviewRes.BoardWith e = ReviewRes.BoardWith.builder()
                        .userId(applicant.getId())
                        .profileUrl(applicant.getProfileUrl())
                        .nickname(applicant.getNickname())
                        .age(age)
                        .gender(applicant.getGender())
                        .build();

                boardWithList.add(e);
            }
        }


        //리턴 객체 생성(수동으로 set)
        ReviewRes.ReviewDetailRes result = ReviewRes.ReviewDetailRes.toDto(review);
        result.setNationName(nationName);
        result.setRegionName(regionName);
        result.setLikes(likes);
        result.setCommentsCnt(commentsCnt);
        result.setReviewImageList(reviewImageList);
        result.setBoardWithList(boardWithList);

        result.setMyReview(review.getWriter().getId().equals(tokenUserId));



        //토큰을 가지고 접근한 유저가 좋아요를 눌렀는지 안눌렀는지
        List<ReviewLike> reviewLikeList = reviewLikeRepository.findByReview(review);
        for(ReviewLike reviewLike : reviewLikeList){
            if(reviewLike.getUser().getId().equals(tokenUserId)){
                result.setTokenUserLiked(true);
                break;
            }
        }








        //해시태그 불러오기
        if(boardHashtags.size() == 5){
            result.setHashtag1(boardHashtags.get(0).getHashtag().getName());
            result.setHashtag2(boardHashtags.get(1).getHashtag().getName());
            result.setHashtag3(boardHashtags.get(2).getHashtag().getName());
            result.setHashtag4(boardHashtags.get(3).getHashtag().getName());
            result.setHashtag5(boardHashtags.get(4).getHashtag().getName());
        }
        else if(boardHashtags.size() == 4){
            result.setHashtag1(boardHashtags.get(0).getHashtag().getName());
            result.setHashtag2(boardHashtags.get(1).getHashtag().getName());
            result.setHashtag3(boardHashtags.get(2).getHashtag().getName());
            result.setHashtag4(boardHashtags.get(3).getHashtag().getName());
        }
        else if(boardHashtags.size() == 3){
            result.setHashtag1(boardHashtags.get(0).getHashtag().getName());
            result.setHashtag2(boardHashtags.get(1).getHashtag().getName());
            result.setHashtag3(boardHashtags.get(2).getHashtag().getName());
        }
        else if(boardHashtags.size() == 2){
            result.setHashtag1(boardHashtags.get(0).getHashtag().getName());
            result.setHashtag2(boardHashtags.get(1).getHashtag().getName());
        }
        else if(boardHashtags.size() == 1){
            result.setHashtag1(boardHashtags.get(0).getHashtag().getName());
        }




        //이전, 다음 게시글 정보
        List<Review> reviewList = reviewRepository.findAll();

        for(Review r : reviewList){
            int nowIdx = reviewList.indexOf(r);
            if(r.getId().equals(reviewId)){
                if(nowIdx == 0){
                    Review next = reviewList.get(nowIdx+1);

                    result.setNextReviewId(next.getId());
                    result.setNextTitle(next.getTitle());
                } else if(nowIdx == reviewList.size()-1){
                    Review previous = reviewList.get(nowIdx-1);

                    result.setPreviousReviewId(previous.getId());
                    result.setPreviousTitle(previous.getTitle());
                } else{
                    Review previous = reviewList.get(nowIdx-1);
                    Review next = reviewList.get(nowIdx+1);

                    result.setPreviousReviewId(previous.getId());
                    result.setPreviousTitle(previous.getTitle());
                    result.setNextReviewId(next.getId());
                    result.setNextTitle(next.getTitle());
                }
                break;
            }
        }






        //조회수 1상승
        reviewRepository.incrementHits(reviewId);

        return result;
    }

    public List<BoardRes.MyBoardApplyListDto> findReviewByLike(Long userId) {
        User user = userService.getUserByUserId(userId);
        List<ReviewLike> reviewLikes = reviewLikeRepository.findByUser(user);
        List<BoardRes.MyBoardApplyListDto> myBoardApplyListDtos = new ArrayList<>();

        for(ReviewLike reviewLike : reviewLikes) {
            Board board = reviewLike.getReview().getBoard();

            Nation nation = board.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = board.getRegion();
            String regionName = region != null ? region.getName() : null;

            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByBoard(board);

            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }

            myBoardApplyListDtos.add(BoardRes.MyBoardApplyListDto.builder()
                            .userId(board.getWriter().getId())
                            .boardId(reviewLike.getReview().getId())
                            .nationName(nationName)
                            .regionName(regionName)
                            .startDate(board.getStartDate())
                            .endDate(board.getEndDate())
                            .totalPeopleNum(board.getTotalPeopleNum())
                            .likes(boardLikeRepository.countByBoard(board))
                            .comments(boardCommentRepository.countByBoard(board))
                            .hashtag1(boardHashtags.get(0).getHashtag().getName())
                            .hashtag2(boardHashtags.get(1).getHashtag().getName())
                            .hashtag3(boardHashtags.get(2).getHashtag().getName())
                            .hashtag4(boardHashtags.get(3).getHashtag().getName())
                            .hashtag5(boardHashtags.get(4).getHashtag().getName())
                            .imageUrl(imageUrl)
                            .build());
        }
        return myBoardApplyListDtos;
    }

    public List<ReviewRes.MyReviewListDto> myReviewWithYear(int year, Long userId) {
        List<ReviewRes.MyReviewListDto> myReviewListDtos = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByYearAndUserId(year, userId);
        for(Review review : reviews) {
            Board board = review.getBoard();
            Nation nation = board.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = board.getRegion();
            String regionName = region != null ? region.getName() : null;

            List<ReviewImage> reviewImages = reviewImageRepository.findAllByReview(review);
            List<String> imageUrls = new ArrayList<>();
            for(ReviewImage reviewImage : reviewImages) {
                imageUrls.add(reviewImage.getUrl());
            }

            myReviewListDtos.add(ReviewRes.MyReviewListDto.builder()
                    .reviewId(review.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .boardTitle(board.getTitle())
                    .reviewTitle(review.getTitle())
                    .likes(boardLikeRepository.countByBoard(board))
                    .comments(boardCommentRepository.countByBoard(board))
                    .hits(review.getHits())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .imageUrls(imageUrls)
                    .build());
        }
        return myReviewListDtos;
    }

    public List<Review> getReviewList(ReviewReq.ReviewOptionDto reviewOptionDto) {
        Long continentId = reviewOptionDto.getContinentId();
        if(continentId.equals(0L)) {
            return reviewRepository.findAll();
        }

        List<Review> tempReviews = new ArrayList<>();
        List<Review> returnReviews = new ArrayList<>();

        List<Board> boards;
        List<Board> returnBoards = new ArrayList<>();

        Long nationId = null;
        if (reviewOptionDto.getNationId() != null) {
            Nation nation = nationRepository.findNationById(reviewOptionDto.getNationId());
            if (nation != null) {
                nationId = nation.getId();
            }
        }

        Long regionId = null;
        if (reviewOptionDto.getRegionId() != null) {
            Region region = regionRepository.findRegionById(reviewOptionDto.getRegionId());
            if (region != null) {
                regionId = region.getId();
            }
        }

        int startMonth = reviewOptionDto.getStartMonth();
        int endMonth = reviewOptionDto.getEndMonth();
        int totalNum = reviewOptionDto.getTotalPeopleNum();
        String keyword = reviewOptionDto.getKeyWord();

        List<Hashtag> hashtags = hashtagRepository.findByNameContains(keyword); // 키워드가 포함된 해시태그 객체를 불러옴

        List<Board> tripylersWithKeyword = new ArrayList<>();


        for (Hashtag hashtag : hashtags) {
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByHashtag(hashtag);
            for (BoardHashtag boardHashtag : boardHashtags) {
                Board board = boardHashtag.getBoard();
                tripylersWithKeyword.add(board);
            }
        }

        if (nationId != null && regionId != null) {
            boards = boardRepository.findByContinentIdAndNationIdAndRegionId(continentId, nationId, regionId);
        } else if (nationId != null) {
            boards = boardRepository.findByContinentIdAndNationId(continentId, nationId);
        } else {
            boards = boardRepository.findByContinentId(continentId);
        }

        for (Board board : boards) {
            int tripStartMonthValue = board.getStartDate().getMonthValue();
            int tripEndMonthValue = board.getEndDate().getMonthValue();
            if (tripStartMonthValue >= startMonth && tripEndMonthValue <= endMonth &&
                    board.getTotalPeopleNum() == totalNum) {
                returnBoards.add(board);
            }
        }


        for (Board board : returnBoards) {
            List<Review> reviewList = reviewRepository.findByBoard(board);
            tempReviews.addAll(reviewList);
        }

        for (Review review : tempReviews) {
            if (review.getTitle().contains(keyword) ||
                    review.getContent().contains(keyword)) {
                returnReviews.add(review);
            }
        }

        return returnReviews;
    }

    public List<ReviewRes.ReviewListDtoOrderByRegDateTime> getReviewListOrderByRegDateTime(List<Review> reviews) {
        List<ReviewRes.ReviewListDtoOrderByRegDateTime> reviewList = new ArrayList<>();
        for(Review review: reviews){
            Board board = boardRepository.findTripylerById(review.getBoard().getId());
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByBoard(board);
            List<String> hashtagList = new ArrayList<>();
            for (BoardHashtag boardHashtag : boardHashtags){
                hashtagList.add(boardHashtag.getHashtag().getName());
            }
            User writer = review.getWriter();
            int age;
            if(writer.getBirthDate() == null || writer.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - writer.getBirthDate().getYear() + 1;
            }

            String regionName = board.getRegion() != null ? board.getRegion().getName() : null;
            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }
            reviewList.add(ReviewRes.ReviewListDtoOrderByRegDateTime.builder()
                    .reviewId(review.getId())
                    .nationName(board.getNation().getName())
                    .regionName(regionName)
                    .title(review.getTitle())
                    .content(review.getContent())
                    .image(imageUrl)
                    .hits(review.getHits())
                    .hashtags(hashtagList)
                    .regDateTime(review.getRegDateTime())
                    .likes(reviewLikeRepository.countByReview(review))
                    .comments(reviewCommentRepository.countByReview(review))
                    .userProfileUrl(writer.getProfileUrl())
                    .username(writer.getUsername())
                    .age(age)
                    .gender(writer.getGender())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .build());
        }
        Collections.sort(reviewList);
        return reviewList;
    }

    public List<ReviewRes.ReviewListDtoOrderByLikes> getReviewListOrderByLikes(List<Review> reviews) {
        List<ReviewRes.ReviewListDtoOrderByLikes> reviewList = new ArrayList<>();
        for(Review review: reviews){
            Board board = boardRepository.findTripylerById(review.getBoard().getId());
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByBoard(board);
            List<String> hashtagList = new ArrayList<>();
            for (BoardHashtag boardHashtag : boardHashtags){
                hashtagList.add(boardHashtag.getHashtag().getName());
            }
            User writer = review.getWriter();
            int age;
            if(writer.getBirthDate() == null || writer.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - writer.getBirthDate().getYear() + 1;
            }
            String regionName = board.getRegion() != null ? board.getRegion().getName() : null;
            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }
            reviewList.add(ReviewRes.ReviewListDtoOrderByLikes.builder()
                    .reviewId(review.getId())
                    .nationName(board.getNation().getName())
                    .regionName(regionName)
                    .title(review.getTitle())
                    .content(review.getContent())
                    .image(imageUrl)
                    .hits(review.getHits())
                    .hashtags(hashtagList)
                    .regDateTime(review.getRegDateTime())
                    .likes(reviewLikeRepository.countByReview(review))
                    .comments(reviewCommentRepository.countByReview(review))
                    .userProfileUrl(writer.getProfileUrl())
                    .username(writer.getUsername())
                    .age(age)
                    .gender(writer.getGender())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .build());
        }
        Collections.sort(reviewList);
        return reviewList;
    }

    public List<ReviewRes.ReviewListDtoOrderByComments> getReviewListOrderByComments(List<Review> reviews) {
        List<ReviewRes.ReviewListDtoOrderByComments> reviewList = new ArrayList<>();
        for(Review review: reviews){
            Board board = boardRepository.findTripylerById(review.getBoard().getId());
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByBoard(board);
            List<String> hashtagList = new ArrayList<>();
            for (BoardHashtag boardHashtag : boardHashtags){
                hashtagList.add(boardHashtag.getHashtag().getName());
            }
            User writer = review.getWriter();
            int age;
            if(writer.getBirthDate() == null || writer.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - writer.getBirthDate().getYear() + 1;
            }
            String regionName = board.getRegion() != null ? board.getRegion().getName() : null;
            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }
            reviewList.add(ReviewRes.ReviewListDtoOrderByComments.builder()
                    .reviewId(review.getId())
                    .nationName(regionName)
                    .regionName(board.getRegion().getName())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .image(imageUrl)
                    .hits(review.getHits())
                    .hashtags(hashtagList)
                    .regDateTime(review.getRegDateTime())
                    .likes(reviewLikeRepository.countByReview(review))
                    .comments(reviewCommentRepository.countByReview(review))
                    .userProfileUrl(writer.getProfileUrl())
                    .username(writer.getUsername())
                    .age(age)
                    .gender(writer.getGender())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .build());
        }
        Collections.sort(reviewList);
        return reviewList;
    }

    public List<ReviewRes.ReviewListDtoOrderByHits> getReviewListOrderByHits(List<Review> reviews) {
        List<ReviewRes.ReviewListDtoOrderByHits> reviewList = new ArrayList<>();
        for(Review review: reviews){
            Board board = boardRepository.findTripylerById(review.getBoard().getId());
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByBoard(board);
            List<String> hashtagList = new ArrayList<>();
            for (BoardHashtag boardHashtag : boardHashtags){
                hashtagList.add(boardHashtag.getHashtag().getName());
            }
            User writer = review.getWriter();
            int age;
            if(writer.getBirthDate() == null || writer.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - writer.getBirthDate().getYear() + 1;
            }
            String regionName = board.getRegion() != null ? board.getRegion().getName() : null;
            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }
            reviewList.add(ReviewRes.ReviewListDtoOrderByHits.builder()
                    .reviewId(review.getId())
                    .nationName(board.getNation().getName())
                    .regionName(regionName)
                    .title(review.getTitle())
                    .content(review.getContent())
                    .image(imageUrl)
                    .hits(review.getHits())
                    .hashtags(hashtagList)
                    .regDateTime(review.getRegDateTime())
                    .likes(reviewLikeRepository.countByReview(review))
                    .comments(reviewCommentRepository.countByReview(review))
                    .userProfileUrl(writer.getProfileUrl())
                    .username(writer.getUsername())
                    .age(age)
                    .gender(writer.getGender())
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .build());
        }
        Collections.sort(reviewList);
        return reviewList;
    }
}
