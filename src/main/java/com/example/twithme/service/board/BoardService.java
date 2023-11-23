package com.example.twithme.service.board;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.exception.NotFoundException;
import com.example.twithme.service.S3Service;
import com.example.twithme.model.dto.board.ReviewRes;
import com.example.twithme.model.dto.board.BoardReq;
import com.example.twithme.model.dto.board.BoardRes;
import com.example.twithme.model.dto.hashtag.HashtagRes;
import com.example.twithme.model.entity.board.*;
import com.example.twithme.model.entity.destination.Continent;
import com.example.twithme.model.entity.destination.Nation;
import com.example.twithme.model.entity.destination.Region;
import com.example.twithme.model.entity.hashtag.Hashtag;
import com.example.twithme.model.entity.user.User;
import com.example.twithme.model.entity.user.UserHashtag;
import com.example.twithme.repository.board.*;
import com.example.twithme.repository.destination.ContinentRepository;
import com.example.twithme.repository.destination.NationRepository;
import com.example.twithme.repository.destination.RegionRepository;
import com.example.twithme.repository.hashtag.HashtagRepository;
import com.example.twithme.repository.user.UserHashtagRepository;
import com.example.twithme.repository.user.UserRepository;
import com.example.twithme.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

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

    private final UserHashtagRepository userHashtagRepository;



    public List<Board> getTripylerList(BoardReq.BoardOptionDto boardOptionDto, int isRecruiting) {
        Long continentId = boardOptionDto.getContinentId();

        List<Board> boards;
        List<Board> returnBoards = new ArrayList<>();

        if(boardOptionDto.getContinentId().equals(0L) || boardOptionDto.getNationId().equals(0L) ||
                boardOptionDto.getRegionId().equals(0L)) {
            return boardRepository.findAll();
        }
        Long nationId = null;
        if (boardOptionDto.getNationId() != null) {
            Nation nation = nationRepository.findNationById(boardOptionDto.getNationId());
            if (nation != null) {
                nationId = nation.getId();
            }
        }

        Long regionId = null;
        if (boardOptionDto.getRegionId() != null) {
            Region region = regionRepository.findRegionById(boardOptionDto.getRegionId());
            if (region != null) {
                regionId = region.getId();
            }
        }

        LocalDate startDate = boardOptionDto.getStartDate();
        if(startDate == null) {
            startDate = LocalDate.of(1900,1,1);
        }
        LocalDate endDate = boardOptionDto.getEndDate();
        if(endDate == null) {
            endDate = LocalDate.of(9999,12,31);
        }
        Integer totalNum = boardOptionDto.getTotalPeopleNum();
        String keyword = boardOptionDto.getKeyWord(); // 해시태그에 트리플러가 포함됨

        List<Hashtag> hashtags = hashtagRepository.findByNameContains(keyword); // 키워드가 포함된 해시태그 객체를 불러옴

        List<Board> tripylersWithKeyword = new ArrayList<>();

        // 이 해시태그의 아이디로 트리플러 해시태그 레포지토리 접근해서 트리플러 아이디를 가져옴
        for (Hashtag hashtag : hashtags) {
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByHashtag(hashtag);
            for (BoardHashtag boardHashtag : boardHashtags) {
                Board board = boardHashtag.getBoard();
                tripylersWithKeyword.add(board); // 트리플러 객체를 리스트에 추가
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
            // 게시물의 시작일, 종료일, 총 인원수 조건을 확인
            if (board.getStartDate().compareTo(startDate) >= 0 &&
                    board.getEndDate().compareTo(endDate) <= 0 &&
                    board.getTotalPeopleNum() == totalNum &&
                    board.getIsRecruiting() == isRecruiting &&
                    (board.getTitle().contains(keyword) ||
                            board.getContent().contains(keyword) ||
                            tripylersWithKeyword.contains(board))
            ) { // 검색어 포함 여부 확인
                returnBoards.add(board);
            }
        }
        return returnBoards;
    }

    public List<BoardRes.TripylerListOrderByRegDateTime> getTripylerListOrderByRegDateTime(List<Board> boards) {
        List<BoardRes.TripylerListOrderByRegDateTime> tripylerList = new ArrayList<>();
        for (Board board : boards) {
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByTripyler(board);
            List<String> hashtagArray = new ArrayList<>();
            for (BoardHashtag boardHashtag : boardHashtags){
                String hashtag = boardHashtag.getHashtag().getName();
                hashtagArray.add(hashtag);
            }
            int likes = boardLikeRepository.countByTripyler(board);
            int comments = boardCommentRepository.countByTripyler(board);
            int age;
            User user = board.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            }
            else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            tripylerList.add(BoardRes.TripylerListOrderByRegDateTime.toDto(board, likes, comments, age, hashtagArray));
        }
        Collections.sort(tripylerList);
        return tripylerList;
    }

    public List<BoardRes.TripylerListOrderByLikes> getTripylerListOrderByLikes(List<Board> boards) {
        List<BoardRes.TripylerListOrderByLikes> tripylerList = new ArrayList<>();
        for (Board board : boards) {
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByTripyler(board);
            List<String> hashtagArray = new ArrayList<>();
            for (BoardHashtag boardHashtag : boardHashtags){
                String hashtag = boardHashtag.getHashtag().getName();
                hashtagArray.add(hashtag);
            }
            int likes = boardLikeRepository.countByTripyler(board);
            int comments = boardCommentRepository.countByTripyler(board);
            int age;
            User user = board.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            }
            else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            tripylerList.add(BoardRes.TripylerListOrderByLikes.toDto(board, likes, comments, age, hashtagArray));
        }
        Collections.sort(tripylerList);
        return tripylerList;
    }

    public List<BoardRes.TripylerListOrderByComments> getTripylerListOrderByComments(List<Board> boards) {
        List<BoardRes.TripylerListOrderByComments> tripylerList = new ArrayList<>();
        for (Board board : boards) {
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByTripyler(board);
            List<String> hashtagArray = new ArrayList<>();
            for (BoardHashtag boardHashtag : boardHashtags){
                String hashtag = boardHashtag.getHashtag().getName();
                hashtagArray.add(hashtag);
            }
            int likes = boardLikeRepository.countByTripyler(board);
            int comments = boardCommentRepository.countByTripyler(board);
            int age;
            User user = board.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            }
            else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            tripylerList.add(BoardRes.TripylerListOrderByComments.toDto(board, likes, comments, age, hashtagArray));
        }
        Collections.sort(tripylerList);
        return tripylerList;
    }

    public List<BoardRes.TripylerListOrderByHits> getTripylerListOrderByHits(List<Board> boards) {
        List<BoardRes.TripylerListOrderByHits> tripylerList = new ArrayList<>();
        for (Board board : boards) {
            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByTripyler(board);
            List<String> hashtagArray = new ArrayList<>();
            for (BoardHashtag boardHashtag : boardHashtags){
                String hashtag = boardHashtag.getHashtag().getName();
                hashtagArray.add(hashtag);
            }
            int likes = boardLikeRepository.countByTripyler(board);
            int comments = boardCommentRepository.countByTripyler(board);
            int age;
            User user = board.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            }
            else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            tripylerList.add(BoardRes.TripylerListOrderByHits.toDto(board, likes, comments, age, hashtagArray));
        }
        Collections.sort(tripylerList);
        return tripylerList;
    }

    public List<BoardLikeRepository.TripylerLikeCount> getTripylerBoardCount() {
        return boardLikeRepository.countTripylerId();
    }

    public List<BoardLikeRepository.TripylerLikeCountWhereIsRecruiting> countTripylerIdWhereIsRecruiting(int isRecruiting) {
        return boardLikeRepository.countTripylerIdWhereIsRecruiting(isRecruiting);
    }

  
    public void createTripyler(Long userId, BoardReq.TripylerCreateDto tripylerCreateDto, MultipartFile multipartFile) {
        User user = userService.getUserByUserId(userId);
        Continent continent = null;
        if(tripylerCreateDto.getContinentId() != null) {
            continent = continentRepository.findContinentById(tripylerCreateDto.getContinentId());
        }
        Nation nation = null;
        if (tripylerCreateDto.getNationId() != null) {
            nation = nationRepository.findNationById(tripylerCreateDto.getNationId());
        }

        Region region = null;
        if (tripylerCreateDto.getRegionId() != null) {
            region = regionRepository.findRegionById(tripylerCreateDto.getRegionId());
        }

        Board board = Board.builder()
                .continent(continent)
                .nation(nation)
                .region(region)
                .writer(user)
                .startDate(tripylerCreateDto.getStartDate())
                .endDate(tripylerCreateDto.getEndDate())
                .totalPeopleNum(tripylerCreateDto.getTotalPeopleNum())

                .estimatedPrice(tripylerCreateDto.getEstimatedPrice())


                .title(tripylerCreateDto.getTitle())
                .content(tripylerCreateDto.getContent())
                .isRecruiting(1)

                .build();
        boardRepository.save(board);
        //String imageUrl = s3Service.uploadImage("tripyler", Long.toString(tripyler.getId()), multipartFile);
        board.setImage("");
        boardRepository.save(board);


        //사전 동행 신청자 추가
        for(String s : tripylerCreateDto.getTripylerWithList()){
            User withUser = userRepository.findByUsername(s);

            BoardApply boardApply = BoardApply.builder()
                    .board(board)
                    .applicant(withUser)
                    .content("사전 신청 동행자")
                    .accepted(1)
                    .build();

            boardApplyRepository.save(boardApply);
        }


        //해시태그 등록
        registerTripStyle(board, tripylerCreateDto);
    }

    public void updateTripyler(Board board, BoardReq.TripylerCreateDto tripylerCreateDto, MultipartFile multipartFile) {
        Continent continent = null;
        if(tripylerCreateDto.getContinentId() != null) {
            continent = continentRepository.findContinentById(tripylerCreateDto.getContinentId());
        }
        Nation nation = null;
        if (tripylerCreateDto.getNationId() != null) {
            nation = nationRepository.findNationById(tripylerCreateDto.getNationId());
        }

        Region region = null;
        if (tripylerCreateDto.getRegionId() != null) {
            region = regionRepository.findRegionById(tripylerCreateDto.getRegionId());
        }
        board.setContinent(continent);
        board.setNation(nation);
        board.setRegion(region);
        board.setStartDate(tripylerCreateDto.getStartDate());
        board.setEndDate(tripylerCreateDto.getEndDate());
        board.setTotalPeopleNum(tripylerCreateDto.getTotalPeopleNum());
        board.setTitle(tripylerCreateDto.getTitle());
        board.setContent(tripylerCreateDto.getContent());
        board.setEstimatedPrice(tripylerCreateDto.getEstimatedPrice());
        boardRepository.save(board);

//        String imageUrl = s3Service.uploadImage("tripyler", Long.toString(tripyler.getId()), multipartFile);
//        if(imageUrl != null) {
//            tripyler.setImage(imageUrl);
//            tripylerRepository.save(tripyler);
//        }

        List<Long> newHashtagIds = Arrays.asList(tripylerCreateDto.getFirstTripStyleId(),
                tripylerCreateDto.getSecondTripStyleId(), tripylerCreateDto.getThirdTripStyleId(),
                tripylerCreateDto.getFourthTripStyleId(), tripylerCreateDto.getFifthTripStyleId());

        List<BoardHashtag> boardHashtagList = boardHashtagRepository.findByTripyler(board);
        List<Long> existingHashtagIds = new ArrayList<>();
        for(BoardHashtag boardHashtag : boardHashtagList) {
            existingHashtagIds.add(boardHashtag.getHashtag().getId());
        }

        for(Long hashtagId : newHashtagIds) {
            if(hashtagId.equals(0L)) {
                continue;
            }
            if(!existingHashtagIds.contains(hashtagId)) {
                Optional<Hashtag> optionalHashtag = hashtagRepository.findById(hashtagId);
                if(optionalHashtag.isEmpty()) {
                    throw new BadRequestException("존재하지 않는 해시태그입니다.");
                }
                Hashtag hashtag = optionalHashtag.get();
                BoardHashtag boardHashtag = BoardHashtag.builder()
                        .board(board)
                        .hashtag(hashtag)
                        .build();
                boardHashtagRepository.save(boardHashtag);
            }
        }

        for(Long hashtagId : existingHashtagIds) {
            if(!newHashtagIds.contains(hashtagId)) {
                Optional<BoardHashtag> optionalTripylerHashtag = Optional.ofNullable(boardHashtagRepository.findByTripylerAndHashtag_Id(board, hashtagId));
                if(optionalTripylerHashtag.isEmpty()) {
                    throw new BadRequestException("해시태그를 삭제할 수 없습니다.");
                }
                BoardHashtag boardHashtag = optionalTripylerHashtag.get();
                boardHashtag.setDeleteYn(true);
                boardHashtagRepository.save(boardHashtag);
            }
        }

    }
  
    public void registerTripStyle(Board board, BoardReq.TripylerCreateDto tripylerCreateDto) {
        if(tripylerCreateDto.getFirstTripStyleId().equals(0L) && tripylerCreateDto.getSecondTripStyleId().equals(0L)
                &&tripylerCreateDto.getThirdTripStyleId().equals(0L)) {
            throw new BadRequestException("해시태그를 하나 이상 입력해주세요");
        }

        List<Long> tripStyleList = new ArrayList<>();
        tripStyleList.add(tripylerCreateDto.getFirstTripStyleId());
        tripStyleList.add(tripylerCreateDto.getSecondTripStyleId());
        tripStyleList.add(tripylerCreateDto.getThirdTripStyleId());
        tripStyleList.add(tripylerCreateDto.getFourthTripStyleId());
        tripStyleList.add(tripylerCreateDto.getFifthTripStyleId());

        for(Long tripStyleId : tripStyleList) {
            if(tripStyleId.equals(0L)) {
                continue;
            }
            Optional<Hashtag> _hashtag = hashtagRepository.findById(tripStyleId);
            if(_hashtag.isEmpty()) {
                throw new BadRequestException("해당 해시태그를 먼저 등록해주세요.");
            }
            Hashtag hashtag = _hashtag.get();
            BoardHashtag boardHashtag = BoardHashtag.builder()
                    .board(board)
                    .hashtag(hashtag)
                    .build();
            boardHashtagRepository.save(boardHashtag);
        }
    }

    public void like(Long tripylerId, Long userId) {
        User user = userService.getUserByUserId(userId);
        Board board = getTripylerByTripylerId(tripylerId);
        Optional<BoardLike> optionalTripylerLike = Optional.ofNullable(boardLikeRepository.findByTripylerAndUser(board, user));
        if(optionalTripylerLike.isPresent()) {
            BoardLike boardLike = optionalTripylerLike.get();
            boardLike.setDeleteYn(true);
        }
        else {
            BoardLike boardLike = BoardLike.builder()
                    .board(board)
                    .user(user)
                    .build();
            boardLikeRepository.save(boardLike);
        }
    }

    public void comment(Long tripylerId, Long userId, String content) {
        User user = userService.getUserByUserId(userId);
        Board board = getTripylerByTripylerId(tripylerId);
        BoardComment boardComment = BoardComment.builder()
                .board(board)
                .commenter(user)
                .content(content)
                .build();
        boardCommentRepository.save(boardComment);
    }

    public Board getTripylerByTripylerId(Long tripylerId) {
        Optional<Board> optionalTripyler = boardRepository.findById(tripylerId);
        if(optionalTripyler.isEmpty()) {
            throw new NotFoundException("존재하지 않는 게시물입니다.");
        }
        return optionalTripyler.get();
    }

    public BoardRes.BoardDetailRes getTripylerBoardDetail(Long tokenUserId, Long tripylerId) {
        Board board = getTripylerByTripylerId(tripylerId);
        List<BoardHashtag> boardHashtags = boardHashtagRepository.findByTripyler(board);


        Nation nation = board.getNation();
        String nationName = nation != null ? nation.getName() : null;
        Region region = board.getRegion();
        String regionName = region != null ? region.getName() : null;


        BoardRes.BoardDetailRes result = BoardRes.BoardDetailRes.toDto(board);

        //나라, 지역 set
        result.setNationName(nationName);
        result.setRegionName(regionName);



        //좋아요, 댓글수, 내가 작성한 트리플러인지
        int likes = boardLikeRepository.countByTripyler(board);
        int commentsCnt = boardCommentRepository.countByTripyler(board);
        result.setLikes(likes);
        result.setCommentsCnt(commentsCnt);
        result.setMyTripyler(board.getWriter().getId().equals(tokenUserId));



        //토큰을 가지고 접근한 유저가 좋아요를 눌렀는지 안눌렀는지
        List<BoardLike> boardLikeList = boardLikeRepository.findByTripyler(board);
        for(BoardLike boardLike : boardLikeList){
            if(boardLike.getUser().getId().equals(tokenUserId)){
                result.setTokenUserLiked(true);
                break;
            }
        }


        List<HashtagRes.HashtagDto> hashtagList = new ArrayList<>();
        //해시태그 리스트
        for(BoardHashtag boardHashtag : boardHashtags){
            HashtagRes.HashtagDto e = new HashtagRes.HashtagDto(boardHashtag.getHashtag().getId(), boardHashtag.getHashtag().getName());
            hashtagList.add(e);
        }

        result.setHashtagList(hashtagList);


        //이 트리플러에 동행한 사람들
        //동행한 사람 리스트
        List<ReviewRes.TripylerWith> tripylerWithList = new ArrayList<>();
        List<BoardApply> boardApplyAcceptedList = boardApplyRepository.findByTripylerAndAccepted(board, 1);

        for(BoardApply boardApply : boardApplyAcceptedList){
            if(boardApply.getAccepted() == 1){
                int age;
                User applicant = boardApply.getApplicant();
                if(applicant.getBirthDate() == null || applicant.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                    age = 0;
                } else {
                    age = LocalDate.now().getYear() - applicant.getBirthDate().getYear() + 1;
                }

                ReviewRes.TripylerWith e = ReviewRes.TripylerWith.builder()
                        .userId(applicant.getId())
                        .profileUrl(applicant.getProfileUrl())
                        .nickname(applicant.getNickname())
                        .age(age)
                        .gender(applicant.getGender())
                        .build();

                tripylerWithList.add(e);
            }
        }

        result.setTripylerWithList(tripylerWithList);



        //이전, 다음 게시글
        List<Board> boardList = boardRepository.findAll();
        for(Board t : boardList){
            int nowIdx = boardList.indexOf(t);

            if(t.getId().equals(tripylerId)){
                if(nowIdx == 0){
                    Board next = boardList.get(nowIdx+1);
                    result.setNextTripylerId(next.getId());
                    result.setNextTitle(next.getTitle());
                } else if(nowIdx == boardList.size()-1){
                    Board previous = boardList.get(nowIdx-1);
                    result.setPreviousTripylerId(previous.getId());
                    result.setPreviousTitle(previous.getTitle());
                } else{
                    Board previous = boardList.get(nowIdx-1);
                    Board next = boardList.get(nowIdx+1);
                    result.setPreviousTripylerId(previous.getId());
                    result.setPreviousTitle(previous.getTitle());
                    result.setNextTripylerId(next.getId());
                    result.setNextTitle(next.getTitle());
                }
                break;
            }
        }







        //조회수 증가
        boardRepository.incrementHits(tripylerId);




        return result;
    }

  
    public void applyTripyler(Long userId, Long tripylerId, String content){
        User user = userService.getUserByUserId(userId);
        Board board = getTripylerByTripylerId(tripylerId);
        BoardApply boardApply = BoardApply.builder()
                .board(board)
                .applicant(user)
                .content(content)
                .build();
        boardApplyRepository.save(boardApply);
    }

    public Map<Long, List<BoardRes.AppliedListDto>> getAppliedList(Long userId) {
        Map<Long, List<BoardRes.AppliedListDto>> appliedListsByTripylerId = new HashMap<>();

        List<Board> boardList = boardRepository.findByWriterId(userId);


        for (Board board : boardList) {
            List<BoardApply> boardApplyList = boardApplyRepository.findByTripylerId(board.getId());
            for (BoardApply boardApply : boardApplyList) {
                List<UserHashtag> userHashtags = userHashtagRepository.findByUser(boardApply.getApplicant());
                List<String> hashtagArray = new ArrayList<>();
                for (UserHashtag userHashtag : userHashtags) {
                    String hashtagName = userHashtag.getHashtag().getName();
                    hashtagArray.add(hashtagName);
                }

                int age;
                User user = boardApply.getApplicant();
                if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                    age = 0;
                }
                else {
                    age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
                }

                BoardRes.AppliedListDto appliedListDto = BoardRes.AppliedListDto.builder()
                        .applicantId(boardApply.getApplicant().getId())
                        .nickname(boardApply.getApplicant().getNickname())
                        .profileUrl(boardApply.getApplicant().getProfileUrl())
                        .age(age)
                        .gender(boardApply.getApplicant().getGender())
                        .hashtag(hashtagArray)
                        .tripylerApplyId(boardApply.getId())
                        .build();

                Long tripylerId = boardApply.getBoard().getId();
                appliedListsByTripylerId.computeIfAbsent(tripylerId, k -> new ArrayList<>()).add(appliedListDto);
            }
        }

        return appliedListsByTripylerId;
    }

    //신청 상세보기
    public BoardRes.AppliedDetailDto getAppliedDetail(Long tripylerApplyId) {
        // 해당 Tripyler 신청 가져오기
        BoardApply boardApply = boardApplyRepository.findTripylerApplyById(tripylerApplyId);

        // Tripyler 신청이 존재할 경우에만 실행
        if (boardApply != null) {
            List<UserHashtag> hashtags = userHashtagRepository.findByUser(boardApply.getApplicant());
            List<String> hashtagArray = new ArrayList<>();
            for (UserHashtag userHashtag : hashtags) {
                hashtagArray.add(userHashtag.getHashtag().getName());
            }

            int age;
            User user = boardApply.getApplicant();
            if (user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            return BoardRes.AppliedDetailDto.builder()
                    .applicantId(boardApply.getApplicant().getId())
                    .nickname(boardApply.getApplicant().getNickname())
                    .profileUrl(boardApply.getApplicant().getProfileUrl())
                    .hashtag(hashtagArray)
                    .gender(boardApply.getApplicant().getGender())
                    .age(age)
                    .title(boardApply.getBoard().getTitle())
                    .content(boardApply.getContent())
                    .accepted(boardApply.getAccepted())
                    .build();
        }

        return null;
    }






    public List<BoardRes.CommentRes> getTripylerComments(Long tripylerId) {
        List<BoardRes.CommentRes> result = new ArrayList<>();
        Board board = getTripylerByTripylerId(tripylerId);
        List<BoardComment> boardComments = boardCommentRepository.findAllByTripylerOrderByRegDateTimeDesc(board);

        for(BoardComment boardComment : boardComments){
            BoardRes.CommentRes e = BoardRes.CommentRes.builder()
                    .userId(boardComment.getCommenter().getId())
                    .nickname(boardComment.getCommenter().getNickname())
                    .content(boardComment.getContent())
                    .build();
            result.add(e);
        }

        return result;
    }

    public Board findUsersTripyler(Long userId, Long tripylerId) {
        User user = userService.getUserByUserId(userId);
        return boardRepository.findByWriterAndId(user, tripylerId);
    }

    public List<BoardRes.MyTripylerApplyListDto> findTripylerApplyByApplicantId(Long applicantId) {
        User applicant = userService.getUserByUserId(applicantId);
        List<BoardApply> boardApplyList = boardApplyRepository.findByApplicant(applicant);
        List<BoardRes.MyTripylerApplyListDto> myTripylerApplyListDtos = new ArrayList<>();
        for(BoardApply boardApply : boardApplyList) {
            Board board = boardApply.getBoard();

            Nation nation = board.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = board.getRegion();
            String regionName = region != null ? region.getName() : null;

            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByTripyler(board);

            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }

            myTripylerApplyListDtos.add(BoardRes.MyTripylerApplyListDto.builder()
                            .tripylerId(board.getId())
                            .nationName(nationName)
                            .regionName(regionName)
                            .startDate(board.getStartDate())
                            .endDate(board.getEndDate())
                            .totalPeopleNum(board.getTotalPeopleNum())
                            .likes(boardLikeRepository.countByTripyler(board))
                            .comments(boardCommentRepository.countByTripyler(board))
                            .hashtag1(boardHashtags.get(0).getHashtag().getName())
                            .hashtag2(boardHashtags.get(1).getHashtag().getName())
                            .hashtag3(boardHashtags.get(2).getHashtag().getName())
                            .hashtag4(boardHashtags.get(3).getHashtag().getName())
                            .hashtag5(boardHashtags.get(4).getHashtag().getName())
                            .imageUrl(imageUrl)
                            .build());
        }
        return myTripylerApplyListDtos;
    }

    public List<BoardRes.MyTripylerApplyListDto> findTripylerByLike(Long userId) {
        User user = userService.getUserByUserId(userId);
        List<BoardLike> boardLikes = boardLikeRepository.findByUser(user);
        List<BoardRes.MyTripylerApplyListDto> myTripylerApplyListDtos = new ArrayList<>();
        for(BoardLike boardLike : boardLikes) {
            Board board = boardLike.getBoard();

            Nation nation = board.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = board.getRegion();
            String regionName = region != null ? region.getName() : null;

            List<BoardHashtag> boardHashtags = boardHashtagRepository.findByTripyler(board);

            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }

            myTripylerApplyListDtos.add(BoardRes.MyTripylerApplyListDto.builder()
                    .tripylerId(board.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .totalPeopleNum(board.getTotalPeopleNum())
                    .likes(boardLikeRepository.countByTripyler(board))
                    .comments(boardCommentRepository.countByTripyler(board))
                    .hashtag1(boardHashtags.get(0).getHashtag().getName())
                    .hashtag2(boardHashtags.get(1).getHashtag().getName())
                    .hashtag3(boardHashtags.get(2).getHashtag().getName())
                    .hashtag4(boardHashtags.get(3).getHashtag().getName())
                    .hashtag5(boardHashtags.get(4).getHashtag().getName())
                    .imageUrl(imageUrl)
                    .build());
        }
        return myTripylerApplyListDtos;
    }


    public boolean acceptTripyler(Long tripylerApplyId){
        BoardApply boardApply = boardApplyRepository.findTripylerApplyById(tripylerApplyId);


        if (boardApply != null) {
            boardApply.setAccepted(1);
            boardApplyRepository.save(boardApply);
            return true;
        } else {
            return false;
        }
    }

    public boolean refuseTripyler(Long tripylerApplyId){
        BoardApply boardApply = boardApplyRepository.findTripylerApplyById(tripylerApplyId);

        if (boardApply != null) {
            boardApply.setAccepted(2);
            boardApplyRepository.save(boardApply);
            return true;
        } else {
            return false;
        }
    }

    public List<BoardRes.MyTripylerListDto> myTripylerWithYear(int year, Long userId) {
        List<BoardRes.MyTripylerListDto> myTripylerListDtos = new ArrayList<>();
        List<Board> boards = boardRepository.findByYearAndUserId(year, userId);
        for(Board board : boards) {
            Nation nation = board.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = board.getRegion();
            String regionName = region != null ? region.getName() : null;

            String imageUrl;
            if(board.getImage() == null) {
                imageUrl = regionName == null ? null : board.getRegion().getImageUrl();
            }
            else {
                imageUrl = board.getImage();
            }

            myTripylerListDtos.add(BoardRes.MyTripylerListDto.builder()
                            .tripylerId(board.getId())
                            .nationName(nationName)
                            .regionName(regionName)
                            .title(board.getTitle())
                            .recruitPeopleNum(board.getRecruitPeopleNum())
                            .totalPeopleNum(board.getTotalPeopleNum())
                            .likes(boardLikeRepository.countByTripyler(board))
                            .comments(boardCommentRepository.countByTripyler(board))
                            .hits(board.getHits())
                            .regDateTime(board.getRegDateTime())
                            .startDate(board.getStartDate())
                            .endDate(board.getEndDate())
                            .imageUrl(imageUrl)
                            .build());
        }
        return myTripylerListDtos;

    }

    public List<BoardRes.MyTripylerTitleDto> myAllTripylers(Long userId){
        User user = userService.getUserByUserId(userId);
        List<Board> boardList = boardRepository.findByWriter(user);
        List<BoardApply> myApplyList = boardApplyRepository.findByApplicantAndAcceptedEquals(user, 1); //내가 신청한 트리플러 리스트

        List<BoardRes.MyTripylerTitleDto> result = new ArrayList<>();


        //내가 작성한 트리플러 게시글
        for(Board board : boardList){
            Nation nation = board.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = board.getRegion();
            String regionName = region != null ? region.getName() : null;

            //내 트리플러에 신청한 사람들
            List<BoardApply> boardApplyList = boardApplyRepository.findByTripyler(board);
            List<ReviewRes.TripylerWith> tripylerWithList = new ArrayList<>();

            for(BoardApply boardApply : boardApplyList){
                int age;
                User applicant = boardApply.getApplicant();
                if(applicant.getBirthDate() == null || applicant.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                    age = 0;
                }
                else {
                    age = LocalDate.now().getYear() - applicant.getBirthDate().getYear() + 1;
                }


                ReviewRes.TripylerWith e =ReviewRes.TripylerWith.builder()
                        .userId(applicant.getId())
                        .nickname(applicant.getNickname())
                        .age(age)
                        .profileUrl(applicant.getProfileUrl())
                        .gender(applicant.getGender())
                        .build();

                tripylerWithList.add(e);
            }

            //최종반환객체
            BoardRes.MyTripylerTitleDto e = BoardRes.MyTripylerTitleDto.builder()
                    .tripylerId(board.getId())
                    .title(board.getTitle())
                    .nationName(nationName)
                    .regionName(regionName)
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .tripylerWithList(tripylerWithList)
                    .regDateTime(board.getRegDateTime())
                    .build();

            result.add(e);
        }


        //내가 신청한 트리플러 리스트
        for(BoardApply boardApply : myApplyList){
            Board board = boardApply.getBoard();
            Nation nation = board.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = board.getRegion();
            String regionName = region != null ? region.getName() : null;


            //내가 신청한 트리플러에 신청한 다른 사람들까지
            List<BoardApply> boardApplyList = boardApplyRepository.findByTripyler(board);
            List<ReviewRes.TripylerWith> tripylerWithList = new ArrayList<>();

            for(BoardApply boardApply2 : boardApplyList){
                int age;
                User applicant = boardApply2.getApplicant();
                if(applicant.getBirthDate() == null || applicant.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                    age = 0;
                }
                else {
                    age = LocalDate.now().getYear() - applicant.getBirthDate().getYear() + 1;
                }


                ReviewRes.TripylerWith e =ReviewRes.TripylerWith.builder()
                        .userId(applicant.getId())
                        .nickname(applicant.getNickname())
                        .age(age)
                        .profileUrl(applicant.getProfileUrl())
                        .gender(applicant.getGender())
                        .build();

                tripylerWithList.add(e);
            }

            BoardRes.MyTripylerTitleDto e = BoardRes.MyTripylerTitleDto.builder()
                    .tripylerId(board.getId())
                    .title(board.getTitle())
                    .nationName(nationName)
                    .regionName(regionName)
                    .startDate(board.getStartDate())
                    .endDate(board.getEndDate())
                    .tripylerWithList(tripylerWithList)
                    .regDateTime(board.getRegDateTime())
                    .build();

            result.add(e);
        }


        //최종 정렬
        Collections.sort(result);

        return result;
    }

    public BoardRes.searchDestinationDto searchDestination(String regionName) {
        Region region = regionRepository.findRegionByName(regionName);
        Nation nation = nationRepository.findNationById(region.getNation().getId());

        BoardRes.searchDestinationDto destinationDto = BoardRes.searchDestinationDto.builder()
                .continentId(nation.getContinent().getId())
                .nationName(region.getNation().getName())
                .nationId(region.getNation().getId())
                .regionName(regionName)
                .regionId(region.getId())
                .build();

        return destinationDto;
    }


}
