package com.example.twithme.service.board;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.exception.NotFoundException;
import com.example.twithme.service.S3Service;
import com.example.twithme.model.dto.board.ReviewRes;
import com.example.twithme.model.dto.board.TripylerReq;
import com.example.twithme.model.dto.board.TripylerRes;
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
public class TripylerService {

    private final UserService userService;
  
    private final S3Service s3Service;
  
    private final UserRepository userRepository;
  
    private final ContinentRepository continentRepository;
  
    private final NationRepository nationRepository;
  
    private final RegionRepository regionRepository;
  
    private final HashtagRepository hashtagRepository;
  
    private final TripylerRepository tripylerRepository;
  
    private final TripylerLikeRepository tripylerLikeRepository;
  
    private final TripylerCommentRepository tripylerCommentRepository;
  
    private final TripylerHashtagRepository tripylerHashtagRepository;
  
    private final TripylerApplyRepository tripylerApplyRepository;

    private final ReviewRepository reviewRepository;

    private final ReviewCommentRepository reviewCommentRepository;

    private final ReviewLikeRepository reviewLikeRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final UserHashtagRepository userHashtagRepository;



    public List<Tripyler> getTripylerList(TripylerReq.BoardOptionDto boardOptionDto, int isRecruiting) {
        Long continentId = boardOptionDto.getContinentId();

        List<Tripyler> tripylers;
        List<Tripyler> returnTripylers = new ArrayList<>();

        if(boardOptionDto.getContinentId().equals(0L) || boardOptionDto.getNationId().equals(0L) ||
                boardOptionDto.getRegionId().equals(0L)) {
            return tripylerRepository.findAll();
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

        List<Tripyler> tripylersWithKeyword = new ArrayList<>();

        // 이 해시태그의 아이디로 트리플러 해시태그 레포지토리 접근해서 트리플러 아이디를 가져옴
        for (Hashtag hashtag : hashtags) {
            List<TripylerHashtag> tripylerHashtags = tripylerHashtagRepository.findByHashtag(hashtag);
            for (TripylerHashtag tripylerHashtag : tripylerHashtags) {
                Tripyler tripyler = tripylerHashtag.getTripyler();
                tripylersWithKeyword.add(tripyler); // 트리플러 객체를 리스트에 추가
            }
        }

        if (nationId != null && regionId != null) {
            tripylers = tripylerRepository.findByContinentIdAndNationIdAndRegionId(continentId, nationId, regionId);
        } else if (nationId != null) {
            tripylers = tripylerRepository.findByContinentIdAndNationId(continentId, nationId);
        } else {
            tripylers = tripylerRepository.findByContinentId(continentId);
        }

        for (Tripyler tripyler : tripylers) {
            // 게시물의 시작일, 종료일, 총 인원수 조건을 확인
            if (tripyler.getStartDate().compareTo(startDate) >= 0 &&
                    tripyler.getEndDate().compareTo(endDate) <= 0 &&
                    tripyler.getTotalPeopleNum() == totalNum &&
                    tripyler.getIsRecruiting() == isRecruiting &&
                    (tripyler.getTitle().contains(keyword) ||
                            tripyler.getContent().contains(keyword) ||
                            tripylersWithKeyword.contains(tripyler))
            ) { // 검색어 포함 여부 확인
                returnTripylers.add(tripyler);
            }
        }
        return returnTripylers;
    }

    public List<TripylerRes.TripylerListOrderByRegDateTime> getTripylerListOrderByRegDateTime(List<Tripyler> tripylers) {
        List<TripylerRes.TripylerListOrderByRegDateTime> tripylerList = new ArrayList<>();
        for (Tripyler tripyler : tripylers) {
            List<TripylerHashtag> tripylerHashtags = tripylerHashtagRepository.findByTripyler(tripyler);
            List<String> hashtagArray = new ArrayList<>();
            for (TripylerHashtag tripylerHashtag : tripylerHashtags){
                String hashtag = tripylerHashtag.getHashtag().getName();
                hashtagArray.add(hashtag);
            }
            int likes = tripylerLikeRepository.countByTripyler(tripyler);
            int comments = tripylerCommentRepository.countByTripyler(tripyler);
            int age;
            User user = tripyler.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            }
            else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            tripylerList.add(TripylerRes.TripylerListOrderByRegDateTime.toDto(tripyler, likes, comments, age, hashtagArray));
        }
        Collections.sort(tripylerList);
        return tripylerList;
    }

    public List<TripylerRes.TripylerListOrderByLikes> getTripylerListOrderByLikes(List<Tripyler> tripylers) {
        List<TripylerRes.TripylerListOrderByLikes> tripylerList = new ArrayList<>();
        for (Tripyler tripyler : tripylers) {
            List<TripylerHashtag> tripylerHashtags = tripylerHashtagRepository.findByTripyler(tripyler);
            List<String> hashtagArray = new ArrayList<>();
            for (TripylerHashtag tripylerHashtag : tripylerHashtags){
                String hashtag = tripylerHashtag.getHashtag().getName();
                hashtagArray.add(hashtag);
            }
            int likes = tripylerLikeRepository.countByTripyler(tripyler);
            int comments = tripylerCommentRepository.countByTripyler(tripyler);
            int age;
            User user = tripyler.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            }
            else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            tripylerList.add(TripylerRes.TripylerListOrderByLikes.toDto(tripyler, likes, comments, age, hashtagArray));
        }
        Collections.sort(tripylerList);
        return tripylerList;
    }

    public List<TripylerRes.TripylerListOrderByComments> getTripylerListOrderByComments(List<Tripyler> tripylers) {
        List<TripylerRes.TripylerListOrderByComments> tripylerList = new ArrayList<>();
        for (Tripyler tripyler : tripylers) {
            List<TripylerHashtag> tripylerHashtags = tripylerHashtagRepository.findByTripyler(tripyler);
            List<String> hashtagArray = new ArrayList<>();
            for (TripylerHashtag tripylerHashtag : tripylerHashtags){
                String hashtag = tripylerHashtag.getHashtag().getName();
                hashtagArray.add(hashtag);
            }
            int likes = tripylerLikeRepository.countByTripyler(tripyler);
            int comments = tripylerCommentRepository.countByTripyler(tripyler);
            int age;
            User user = tripyler.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            }
            else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            tripylerList.add(TripylerRes.TripylerListOrderByComments.toDto(tripyler, likes, comments, age, hashtagArray));
        }
        Collections.sort(tripylerList);
        return tripylerList;
    }

    public List<TripylerRes.TripylerListOrderByHits> getTripylerListOrderByHits(List<Tripyler> tripylers) {
        List<TripylerRes.TripylerListOrderByHits> tripylerList = new ArrayList<>();
        for (Tripyler tripyler : tripylers) {
            List<TripylerHashtag> tripylerHashtags = tripylerHashtagRepository.findByTripyler(tripyler);
            List<String> hashtagArray = new ArrayList<>();
            for (TripylerHashtag tripylerHashtag : tripylerHashtags){
                String hashtag = tripylerHashtag.getHashtag().getName();
                hashtagArray.add(hashtag);
            }
            int likes = tripylerLikeRepository.countByTripyler(tripyler);
            int comments = tripylerCommentRepository.countByTripyler(tripyler);
            int age;
            User user = tripyler.getWriter();
            if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            }
            else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            tripylerList.add(TripylerRes.TripylerListOrderByHits.toDto(tripyler, likes, comments, age, hashtagArray));
        }
        Collections.sort(tripylerList);
        return tripylerList;
    }

    public List<TripylerLikeRepository.TripylerLikeCount> getTripylerBoardCount() {
        return tripylerLikeRepository.countTripylerId();
    }

    public List<TripylerLikeRepository.TripylerLikeCountWhereIsRecruiting> countTripylerIdWhereIsRecruiting(int isRecruiting) {
        return tripylerLikeRepository.countTripylerIdWhereIsRecruiting(isRecruiting);
    }

  
    public void createTripyler(Long userId, TripylerReq.TripylerCreateDto tripylerCreateDto, MultipartFile multipartFile) {
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

        Tripyler tripyler = Tripyler.builder()
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
        tripylerRepository.save(tripyler);
        //String imageUrl = s3Service.uploadImage("tripyler", Long.toString(tripyler.getId()), multipartFile);
        tripyler.setImage("");
        tripylerRepository.save(tripyler);


        //사전 동행 신청자 추가
        for(String s : tripylerCreateDto.getTripylerWithList()){
            User withUser = userRepository.findByUsername(s);

            TripylerApply tripylerApply = TripylerApply.builder()
                    .tripyler(tripyler)
                    .applicant(withUser)
                    .content("사전 신청 동행자")
                    .accepted(1)
                    .build();

            tripylerApplyRepository.save(tripylerApply);
        }


        //해시태그 등록
        registerTripStyle(tripyler, tripylerCreateDto);
    }

    public void updateTripyler(Tripyler tripyler, TripylerReq.TripylerCreateDto tripylerCreateDto, MultipartFile multipartFile) {
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
        tripyler.setContinent(continent);
        tripyler.setNation(nation);
        tripyler.setRegion(region);
        tripyler.setStartDate(tripylerCreateDto.getStartDate());
        tripyler.setEndDate(tripylerCreateDto.getEndDate());
        tripyler.setTotalPeopleNum(tripylerCreateDto.getTotalPeopleNum());
        tripyler.setTitle(tripylerCreateDto.getTitle());
        tripyler.setContent(tripylerCreateDto.getContent());
        tripyler.setEstimatedPrice(tripylerCreateDto.getEstimatedPrice());
        tripylerRepository.save(tripyler);

//        String imageUrl = s3Service.uploadImage("tripyler", Long.toString(tripyler.getId()), multipartFile);
//        if(imageUrl != null) {
//            tripyler.setImage(imageUrl);
//            tripylerRepository.save(tripyler);
//        }

        List<Long> newHashtagIds = Arrays.asList(tripylerCreateDto.getFirstTripStyleId(),
                tripylerCreateDto.getSecondTripStyleId(), tripylerCreateDto.getThirdTripStyleId(),
                tripylerCreateDto.getFourthTripStyleId(), tripylerCreateDto.getFifthTripStyleId());

        List<TripylerHashtag> tripylerHashtagList = tripylerHashtagRepository.findByTripyler(tripyler);
        List<Long> existingHashtagIds = new ArrayList<>();
        for(TripylerHashtag tripylerHashtag : tripylerHashtagList) {
            existingHashtagIds.add(tripylerHashtag.getHashtag().getId());
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
                TripylerHashtag tripylerHashtag = TripylerHashtag.builder()
                        .tripyler(tripyler)
                        .hashtag(hashtag)
                        .build();
                tripylerHashtagRepository.save(tripylerHashtag);
            }
        }

        for(Long hashtagId : existingHashtagIds) {
            if(!newHashtagIds.contains(hashtagId)) {
                Optional<TripylerHashtag> optionalTripylerHashtag = Optional.ofNullable(tripylerHashtagRepository.findByTripylerAndHashtag_Id(tripyler, hashtagId));
                if(optionalTripylerHashtag.isEmpty()) {
                    throw new BadRequestException("해시태그를 삭제할 수 없습니다.");
                }
                TripylerHashtag tripylerHashtag = optionalTripylerHashtag.get();
                tripylerHashtag.setDeleteYn(true);
                tripylerHashtagRepository.save(tripylerHashtag);
            }
        }

    }
  
    public void registerTripStyle(Tripyler tripyler, TripylerReq.TripylerCreateDto tripylerCreateDto) {
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
            TripylerHashtag tripylerHashtag  = TripylerHashtag.builder()
                    .tripyler(tripyler)
                    .hashtag(hashtag)
                    .build();
            tripylerHashtagRepository.save(tripylerHashtag);
        }
    }

    public void like(Long tripylerId, Long userId) {
        User user = userService.getUserByUserId(userId);
        Tripyler tripyler = getTripylerByTripylerId(tripylerId);
        Optional<TripylerLike> optionalTripylerLike = Optional.ofNullable(tripylerLikeRepository.findByTripylerAndUser(tripyler, user));
        if(optionalTripylerLike.isPresent()) {
            TripylerLike tripylerLike = optionalTripylerLike.get();
            tripylerLike.setDeleteYn(true);
        }
        else {
            TripylerLike tripylerLike = TripylerLike.builder()
                    .tripyler(tripyler)
                    .user(user)
                    .build();
            tripylerLikeRepository.save(tripylerLike);
        }
    }

    public void comment(Long tripylerId, Long userId, String content) {
        User user = userService.getUserByUserId(userId);
        Tripyler tripyler = getTripylerByTripylerId(tripylerId);
        TripylerComment tripylerComment = TripylerComment.builder()
                .tripyler(tripyler)
                .commenter(user)
                .content(content)
                .build();
        tripylerCommentRepository.save(tripylerComment);
    }

    public Tripyler getTripylerByTripylerId(Long tripylerId) {
        Optional<Tripyler> optionalTripyler = tripylerRepository.findById(tripylerId);
        if(optionalTripyler.isEmpty()) {
            throw new NotFoundException("존재하지 않는 게시물입니다.");
        }
        return optionalTripyler.get();
    }

    public TripylerRes.BoardDetailRes getTripylerBoardDetail(Long tokenUserId, Long tripylerId) {
        Tripyler tripyler = getTripylerByTripylerId(tripylerId);
        List<TripylerHashtag> tripylerHashtags = tripylerHashtagRepository.findByTripyler(tripyler);


        Nation nation = tripyler.getNation();
        String nationName = nation != null ? nation.getName() : null;
        Region region = tripyler.getRegion();
        String regionName = region != null ? region.getName() : null;


        TripylerRes.BoardDetailRes result = TripylerRes.BoardDetailRes.toDto(tripyler);

        //나라, 지역 set
        result.setNationName(nationName);
        result.setRegionName(regionName);



        //좋아요, 댓글수, 내가 작성한 트리플러인지
        int likes = tripylerLikeRepository.countByTripyler(tripyler);
        int commentsCnt = tripylerCommentRepository.countByTripyler(tripyler);
        result.setLikes(likes);
        result.setCommentsCnt(commentsCnt);
        result.setMyTripyler(tripyler.getWriter().getId().equals(tokenUserId));



        //토큰을 가지고 접근한 유저가 좋아요를 눌렀는지 안눌렀는지
        List<TripylerLike> tripylerLikeList = tripylerLikeRepository.findByTripyler(tripyler);
        for(TripylerLike tripylerLike : tripylerLikeList){
            if(tripylerLike.getUser().getId().equals(tokenUserId)){
                result.setTokenUserLiked(true);
                break;
            }
        }


        List<HashtagRes.HashtagDto> hashtagList = new ArrayList<>();
        //해시태그 리스트
        for(TripylerHashtag tripylerHashtag: tripylerHashtags){
            HashtagRes.HashtagDto e = new HashtagRes.HashtagDto(tripylerHashtag.getHashtag().getId(), tripylerHashtag.getHashtag().getName());
            hashtagList.add(e);
        }

        result.setHashtagList(hashtagList);


        //이 트리플러에 동행한 사람들
        //동행한 사람 리스트
        List<ReviewRes.TripylerWith> tripylerWithList = new ArrayList<>();
        List<TripylerApply> tripylerApplyAcceptedList = tripylerApplyRepository.findByTripylerAndAccepted(tripyler, 1);

        for(TripylerApply tripylerApply : tripylerApplyAcceptedList){
            if(tripylerApply.getAccepted() == 1){
                int age;
                User applicant = tripylerApply.getApplicant();
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
        List<Tripyler> tripylerList = tripylerRepository.findAll();
        for(Tripyler t : tripylerList){
            int nowIdx = tripylerList.indexOf(t);

            if(t.getId().equals(tripylerId)){
                if(nowIdx == 0){
                    Tripyler next = tripylerList.get(nowIdx+1);
                    result.setNextTripylerId(next.getId());
                    result.setNextTitle(next.getTitle());
                } else if(nowIdx == tripylerList.size()-1){
                    Tripyler previous = tripylerList.get(nowIdx-1);
                    result.setPreviousTripylerId(previous.getId());
                    result.setPreviousTitle(previous.getTitle());
                } else{
                    Tripyler previous = tripylerList.get(nowIdx-1);
                    Tripyler next = tripylerList.get(nowIdx+1);
                    result.setPreviousTripylerId(previous.getId());
                    result.setPreviousTitle(previous.getTitle());
                    result.setNextTripylerId(next.getId());
                    result.setNextTitle(next.getTitle());
                }
                break;
            }
        }







        //조회수 증가
        tripylerRepository.incrementHits(tripylerId);




        return result;
    }

  
    public void applyTripyler(Long userId, Long tripylerId, String content){
        User user = userService.getUserByUserId(userId);
        Tripyler tripyler = getTripylerByTripylerId(tripylerId);
        TripylerApply tripylerApply = TripylerApply.builder()
                .tripyler(tripyler)
                .applicant(user)
                .content(content)
                .build();
        tripylerApplyRepository.save(tripylerApply);
    }

    public Map<Long, List<TripylerRes.AppliedListDto>> getAppliedList(Long userId) {
        Map<Long, List<TripylerRes.AppliedListDto>> appliedListsByTripylerId = new HashMap<>();

        List<Tripyler> tripylerList = tripylerRepository.findByWriterId(userId);


        for (Tripyler tripyler : tripylerList) {
            List<TripylerApply> tripylerApplyList = tripylerApplyRepository.findByTripylerId(tripyler.getId());
            for (TripylerApply tripylerApply : tripylerApplyList) {
                List<UserHashtag> userHashtags = userHashtagRepository.findByUser(tripylerApply.getApplicant());
                List<String> hashtagArray = new ArrayList<>();
                for (UserHashtag userHashtag : userHashtags) {
                    String hashtagName = userHashtag.getHashtag().getName();
                    hashtagArray.add(hashtagName);
                }

                int age;
                User user = tripylerApply.getApplicant();
                if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                    age = 0;
                }
                else {
                    age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
                }

                TripylerRes.AppliedListDto appliedListDto = TripylerRes.AppliedListDto.builder()
                        .applicantId(tripylerApply.getApplicant().getId())
                        .nickname(tripylerApply.getApplicant().getNickname())
                        .profileUrl(tripylerApply.getApplicant().getProfileUrl())
                        .age(age)
                        .gender(tripylerApply.getApplicant().getGender())
                        .hashtag(hashtagArray)
                        .tripylerApplyId(tripylerApply.getId())
                        .build();

                Long tripylerId = tripylerApply.getTripyler().getId();
                appliedListsByTripylerId.computeIfAbsent(tripylerId, k -> new ArrayList<>()).add(appliedListDto);
            }
        }

        return appliedListsByTripylerId;
    }

    //신청 상세보기
    public TripylerRes.AppliedDetailDto getAppliedDetail(Long tripylerApplyId) {
        // 해당 Tripyler 신청 가져오기
        TripylerApply tripylerApply = tripylerApplyRepository.findTripylerApplyById(tripylerApplyId);

        // Tripyler 신청이 존재할 경우에만 실행
        if (tripylerApply != null) {
            List<UserHashtag> hashtags = userHashtagRepository.findByUser(tripylerApply.getApplicant());
            List<String> hashtagArray = new ArrayList<>();
            for (UserHashtag userHashtag : hashtags) {
                hashtagArray.add(userHashtag.getHashtag().getName());
            }

            int age;
            User user = tripylerApply.getApplicant();
            if (user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
                age = 0;
            } else {
                age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
            }

            return TripylerRes.AppliedDetailDto.builder()
                    .applicantId(tripylerApply.getApplicant().getId())
                    .nickname(tripylerApply.getApplicant().getNickname())
                    .profileUrl(tripylerApply.getApplicant().getProfileUrl())
                    .hashtag(hashtagArray)
                    .gender(tripylerApply.getApplicant().getGender())
                    .age(age)
                    .title(tripylerApply.getTripyler().getTitle())
                    .content(tripylerApply.getContent())
                    .accepted(tripylerApply.getAccepted())
                    .build();
        }

        return null;
    }






    public List<TripylerRes.CommentRes> getTripylerComments(Long tripylerId) {
        List<TripylerRes.CommentRes> result = new ArrayList<>();
        Tripyler tripyler = getTripylerByTripylerId(tripylerId);
        List<TripylerComment> tripylerComments = tripylerCommentRepository.findAllByTripylerOrderByRegDateTimeDesc(tripyler);

        for(TripylerComment tripylerComment : tripylerComments){
            TripylerRes.CommentRes e = TripylerRes.CommentRes.builder()
                    .userId(tripylerComment.getCommenter().getId())
                    .nickname(tripylerComment.getCommenter().getNickname())
                    .content(tripylerComment.getContent())
                    .build();
            result.add(e);
        }

        return result;
    }

    public Tripyler findUsersTripyler(Long userId, Long tripylerId) {
        User user = userService.getUserByUserId(userId);
        return tripylerRepository.findByWriterAndId(user, tripylerId);
    }

    public List<TripylerRes.MyTripylerApplyListDto> findTripylerApplyByApplicantId(Long applicantId) {
        User applicant = userService.getUserByUserId(applicantId);
        List<TripylerApply> tripylerApplyList = tripylerApplyRepository.findByApplicant(applicant);
        List<TripylerRes.MyTripylerApplyListDto> myTripylerApplyListDtos = new ArrayList<>();
        for(TripylerApply tripylerApply : tripylerApplyList) {
            Tripyler tripyler = tripylerApply.getTripyler();

            Nation nation = tripyler.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = tripyler.getRegion();
            String regionName = region != null ? region.getName() : null;

            List<TripylerHashtag> tripylerHashtags = tripylerHashtagRepository.findByTripyler(tripyler);

            String imageUrl;
            if(tripyler.getImage() == null) {
                imageUrl = regionName == null ? null : tripyler.getRegion().getImageUrl();
            }
            else {
                imageUrl = tripyler.getImage();
            }

            myTripylerApplyListDtos.add(TripylerRes.MyTripylerApplyListDto.builder()
                            .tripylerId(tripyler.getId())
                            .nationName(nationName)
                            .regionName(regionName)
                            .startDate(tripyler.getStartDate())
                            .endDate(tripyler.getEndDate())
                            .totalPeopleNum(tripyler.getTotalPeopleNum())
                            .likes(tripylerLikeRepository.countByTripyler(tripyler))
                            .comments(tripylerCommentRepository.countByTripyler(tripyler))
                            .hashtag1(tripylerHashtags.get(0).getHashtag().getName())
                            .hashtag2(tripylerHashtags.get(1).getHashtag().getName())
                            .hashtag3(tripylerHashtags.get(2).getHashtag().getName())
                            .hashtag4(tripylerHashtags.get(3).getHashtag().getName())
                            .hashtag5(tripylerHashtags.get(4).getHashtag().getName())
                            .imageUrl(imageUrl)
                            .build());
        }
        return myTripylerApplyListDtos;
    }

    public List<TripylerRes.MyTripylerApplyListDto> findTripylerByLike(Long userId) {
        User user = userService.getUserByUserId(userId);
        List<TripylerLike> tripylerLikes = tripylerLikeRepository.findByUser(user);
        List<TripylerRes.MyTripylerApplyListDto> myTripylerApplyListDtos = new ArrayList<>();
        for(TripylerLike tripylerLike : tripylerLikes) {
            Tripyler tripyler = tripylerLike.getTripyler();

            Nation nation = tripyler.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = tripyler.getRegion();
            String regionName = region != null ? region.getName() : null;

            List<TripylerHashtag> tripylerHashtags = tripylerHashtagRepository.findByTripyler(tripyler);

            String imageUrl;
            if(tripyler.getImage() == null) {
                imageUrl = regionName == null ? null : tripyler.getRegion().getImageUrl();
            }
            else {
                imageUrl = tripyler.getImage();
            }

            myTripylerApplyListDtos.add(TripylerRes.MyTripylerApplyListDto.builder()
                    .tripylerId(tripyler.getId())
                    .nationName(nationName)
                    .regionName(regionName)
                    .startDate(tripyler.getStartDate())
                    .endDate(tripyler.getEndDate())
                    .totalPeopleNum(tripyler.getTotalPeopleNum())
                    .likes(tripylerLikeRepository.countByTripyler(tripyler))
                    .comments(tripylerCommentRepository.countByTripyler(tripyler))
                    .hashtag1(tripylerHashtags.get(0).getHashtag().getName())
                    .hashtag2(tripylerHashtags.get(1).getHashtag().getName())
                    .hashtag3(tripylerHashtags.get(2).getHashtag().getName())
                    .hashtag4(tripylerHashtags.get(3).getHashtag().getName())
                    .hashtag5(tripylerHashtags.get(4).getHashtag().getName())
                    .imageUrl(imageUrl)
                    .build());
        }
        return myTripylerApplyListDtos;
    }


    public boolean acceptTripyler(Long tripylerApplyId){
        TripylerApply tripylerApply = tripylerApplyRepository.findTripylerApplyById(tripylerApplyId);


        if (tripylerApply != null) {
            tripylerApply.setAccepted(1);
            tripylerApplyRepository.save(tripylerApply);
            return true;
        } else {
            return false;
        }
    }

    public boolean refuseTripyler(Long tripylerApplyId){
        TripylerApply tripylerApply = tripylerApplyRepository.findTripylerApplyById(tripylerApplyId);

        if (tripylerApply != null) {
            tripylerApply.setAccepted(2);
            tripylerApplyRepository.save(tripylerApply);
            return true;
        } else {
            return false;
        }
    }

    public List<TripylerRes.MyTripylerListDto> myTripylerWithYear(int year, Long userId) {
        List<TripylerRes.MyTripylerListDto> myTripylerListDtos = new ArrayList<>();
        List<Tripyler> tripylers = tripylerRepository.findByYearAndUserId(year, userId);
        for(Tripyler tripyler : tripylers) {
            Nation nation = tripyler.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = tripyler.getRegion();
            String regionName = region != null ? region.getName() : null;

            String imageUrl;
            if(tripyler.getImage() == null) {
                imageUrl = regionName == null ? null : tripyler.getRegion().getImageUrl();
            }
            else {
                imageUrl = tripyler.getImage();
            }

            myTripylerListDtos.add(TripylerRes.MyTripylerListDto.builder()
                            .tripylerId(tripyler.getId())
                            .nationName(nationName)
                            .regionName(regionName)
                            .title(tripyler.getTitle())
                            .recruitPeopleNum(tripyler.getRecruitPeopleNum())
                            .totalPeopleNum(tripyler.getTotalPeopleNum())
                            .likes(tripylerLikeRepository.countByTripyler(tripyler))
                            .comments(tripylerCommentRepository.countByTripyler(tripyler))
                            .hits(tripyler.getHits())
                            .regDateTime(tripyler.getRegDateTime())
                            .startDate(tripyler.getStartDate())
                            .endDate(tripyler.getEndDate())
                            .imageUrl(imageUrl)
                            .build());
        }
        return myTripylerListDtos;

    }

    public List<TripylerRes.MyTripylerTitleDto> myAllTripylers(Long userId){
        User user = userService.getUserByUserId(userId);
        List<Tripyler> tripylerList = tripylerRepository.findByWriter(user);
        List<TripylerApply> myApplyList = tripylerApplyRepository.findByApplicantAndAcceptedEquals(user, 1); //내가 신청한 트리플러 리스트

        List<TripylerRes.MyTripylerTitleDto> result = new ArrayList<>();


        //내가 작성한 트리플러 게시글
        for(Tripyler tripyler : tripylerList){
            Nation nation = tripyler.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = tripyler.getRegion();
            String regionName = region != null ? region.getName() : null;

            //내 트리플러에 신청한 사람들
            List<TripylerApply> tripylerApplyList = tripylerApplyRepository.findByTripyler(tripyler);
            List<ReviewRes.TripylerWith> tripylerWithList = new ArrayList<>();

            for(TripylerApply tripylerApply: tripylerApplyList){
                int age;
                User applicant = tripylerApply.getApplicant();
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
            TripylerRes.MyTripylerTitleDto e = TripylerRes.MyTripylerTitleDto.builder()
                    .tripylerId(tripyler.getId())
                    .title(tripyler.getTitle())
                    .nationName(nationName)
                    .regionName(regionName)
                    .startDate(tripyler.getStartDate())
                    .endDate(tripyler.getEndDate())
                    .tripylerWithList(tripylerWithList)
                    .regDateTime(tripyler.getRegDateTime())
                    .build();

            result.add(e);
        }


        //내가 신청한 트리플러 리스트
        for(TripylerApply tripylerApply : myApplyList){
            Tripyler tripyler = tripylerApply.getTripyler();
            Nation nation = tripyler.getNation();
            String nationName = nation != null ? nation.getName() : null;
            Region region = tripyler.getRegion();
            String regionName = region != null ? region.getName() : null;


            //내가 신청한 트리플러에 신청한 다른 사람들까지
            List<TripylerApply> tripylerApplyList = tripylerApplyRepository.findByTripyler(tripyler);
            List<ReviewRes.TripylerWith> tripylerWithList = new ArrayList<>();

            for(TripylerApply tripylerApply2: tripylerApplyList){
                int age;
                User applicant = tripylerApply2.getApplicant();
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

            TripylerRes.MyTripylerTitleDto e = TripylerRes.MyTripylerTitleDto.builder()
                    .tripylerId(tripyler.getId())
                    .title(tripyler.getTitle())
                    .nationName(nationName)
                    .regionName(regionName)
                    .startDate(tripyler.getStartDate())
                    .endDate(tripyler.getEndDate())
                    .tripylerWithList(tripylerWithList)
                    .regDateTime(tripyler.getRegDateTime())
                    .build();

            result.add(e);
        }


        //최종 정렬
        Collections.sort(result);

        return result;
    }

    public TripylerRes.searchDestinationDto searchDestination(String regionName) {
        Region region = regionRepository.findRegionByName(regionName);
        Nation nation = nationRepository.findNationById(region.getNation().getId());

        TripylerRes.searchDestinationDto destinationDto = TripylerRes.searchDestinationDto.builder()
                .continentId(nation.getContinent().getId())
                .nationName(region.getNation().getName())
                .nationId(region.getNation().getId())
                .regionName(regionName)
                .regionId(region.getId())
                .build();

        return destinationDto;
    }


}
