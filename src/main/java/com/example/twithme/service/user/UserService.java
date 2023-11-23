package com.example.twithme.service.user;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.exception.NotFoundException;
import com.example.twithme.common.exception.ServerErrorException;
import com.example.twithme.common.exception.dto.SmsDto;
import com.example.twithme.common.service.S3Service;
import com.example.twithme.common.service.SmsService;
import com.example.twithme.model.dto.user.UserReq;
import com.example.twithme.model.dto.user.UserRes;
import com.example.twithme.model.entity.hashtag.Hashtag;
import com.example.twithme.model.entity.user.Mbti;
import com.example.twithme.model.entity.user.User;
import com.example.twithme.model.entity.user.UserHashtag;
import com.example.twithme.repository.hashtag.HashtagRepository;
import com.example.twithme.repository.user.MbtiRepository;
import com.example.twithme.repository.user.UserHashtagRepository;
import com.example.twithme.repository.user.UserRepository;
import com.example.twithme.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final SmsService smsService;
    private final S3Service s3Service;
    private final HashtagRepository hashtagRepository;
    private final UserHashtagRepository userHashtagRepository;
    private final MbtiRepository mbtiRepository;

    public Long getUserId(HttpServletRequest httpServletRequest ){
        return Long.valueOf(String.valueOf(httpServletRequest.getAttribute("id")));
    }

    public User getUserByUserId(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new NotFoundException("존재하지 않는 회원입니다.");
        }
        return optionalUser.get();
    }


    public UserRes.UserInfoWithToken doSocialLogIn(String loginType, String snsId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByLoginTypeAndSnsId(loginType, snsId));
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean isFirstLogin = user.isFirstLogin();
            user.setFirstLogin(false);
            return UserRes.UserInfoWithToken.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .alarmNum(0)
                    .accessToken(jwtTokenProvider.createAccessToken(String.valueOf(user.getId()), Collections.singletonList(user.getUserRole())))
                    .isFirstLogin(isFirstLogin)
                    .build();
        }
        return UserRes.UserInfoWithToken.builder()
                .id(0L)
                .build();
    }
    public void doKakaoSignUp(String snsId, String snsToken) {
//        UserRes.NaverProfileResponseDto naverProfileResponse;
//        final String naverProfileUrl = "https://kapi.kakao.com/v2/user/me";
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set("Authorization", "Bearer " + kakaoLogInDto.getSnsToken());
//        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
//        try {
//            ResponseEntity<UserRes.NaverProfileResponseDto> response = restTemplate.exchange(naverProfileUrl, HttpMethod.GET, httpEntity, UserRes.NaverProfileResponseDto.class);
//            if(response.getStatusCodeValue() != HttpStatus.OK.value()) {
//                throw new ServerErrorException();
//            }
//            naverProfileResponse = response.getBody();
//            if(naverProfileResponse == null){
//                throw new BadRequestException("카카오 회원가입 중 오류가 발생했습니다.");
//            }
//        }
//        catch (RestClientException e) {
//            throw new ServerErrorException();
//        }
//        회원 관련 정보 받아야 함

        User user = User.builder()
                .loginType("kakao")
                .snsId(snsId)
                .snsToken(snsToken)
                .userRole("ROLE_USER")
                .firstLogin(true)
                .build();
        userRepository.save(user);
    }

    public void doKakaoAdditionalSignUp(Long userId, UserReq.KakaoSignUpDto kakaoSignUpDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(kakaoSignUpDto.getName());
            user.setBirthDate(kakaoSignUpDto.getBirthDate());
            user.setGender(kakaoSignUpDto.getGender());
            user.setEmail(kakaoSignUpDto.getEmail());
            user.setFirstLogin(true);
            userRepository.save(user);
        }
        else {
            throw new BadRequestException("카카오 회원가입을 먼저 진행해주세요.");
        }
    }

    public boolean existsSnsUser(String loginType, String snsId) {
        return userRepository.existsByLoginTypeAndSnsId(loginType, snsId);
    }



    //로그인 타입이 phone이 아니면 소셜로그인으로 하라고 알려주기
    //아이디 찾기
    public UserRes.findUserByName findUserByName(UserReq.FindUserByName findUserByName) {
        User user = userRepository.findByNameAndPhone(findUserByName.getName(), findUserByName.getPhone());

        if(user == null)
            throw new NotFoundException("유저를 찾을 수 없습니다. 소셜로그인으로 접근해주세요");

        UserRes.findUserByName result = new UserRes.findUserByName(user.getUsername());

        return result;
    }


    public void uploadProfileImage(Long userId, MultipartFile multipartFile) {
        //String profileUrl = s3Service.uploadImage("profile", Long.toString(userId), multipartFile);

        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setProfileUrl("profileUrl");
            userRepository.save(user);
        }
        else {
//            branch 합친 후에 NotFoundException으로 바꿔야 함
            throw new BadRequestException("존재하지 않는 회원입니다.");
        }
    }

    public void deleteProfileImage(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setProfileUrl(null);
            userRepository.save(user);
        }
        else {
            throw new NotFoundException("존재하지 않는 회원입니다.");
        }
    }


    public void registerTripStyle(Long userId, UserReq.UserFormReq userFormReq) {

        Optional<User> optionalUser = userRepository.findById(userId);
        User user;
        if(optionalUser.isPresent()) {
            user = optionalUser.get();
        }
        else {
            throw new NotFoundException("존재하지 않는 회원입니다.");
        }

        if(userFormReq.getFirstTripStyleId().equals(0L) && userFormReq.getSecondTripStyleId().equals(0L)
                &&userFormReq.getThirdTripStyleId().equals(0L)) {
            throw new BadRequestException("해시태그를 하나 이상 입력해주세요");
        }

        List<Long> tripStyleList = new ArrayList<>();
        tripStyleList.add(userFormReq.getFirstTripStyleId());
        tripStyleList.add(userFormReq.getSecondTripStyleId());
        tripStyleList.add(userFormReq.getThirdTripStyleId());

        for(Long tripStyleId : tripStyleList) {
            if(tripStyleId.equals(0L)) {
                continue;
            }
            Optional<Hashtag> _hashtag = hashtagRepository.findById(tripStyleId);
            if(_hashtag.isEmpty()) {
                throw new BadRequestException("해당 해시태그를 먼저 등록해주세요.");
            }
            Hashtag hashtag = _hashtag.get();
            UserHashtag userHashtag  = UserHashtag.builder()
                    .user(user)
                    .hashtag(hashtag)
                    .build();
            userHashtagRepository.save(userHashtag);
        }
    }

    public UserRes.ProfileDto getMyProfile(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new NotFoundException("존재하지 않는 유저입니다.");
        }
        User user = optionalUser.get();

        List<UserHashtag> userHashtagList = userHashtagRepository.findByUser(user);
        List<String> hashtagName = new ArrayList<>();
        for(UserHashtag userHashtag : userHashtagList) {
            hashtagName.add(userHashtag.getHashtag().getName());
        }
        int hashtagLen = userHashtagList.size();
        for(int i = 0; i < 3 - hashtagLen; i++) {
            hashtagName.add("");
        }

        int age;
        if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
            age = 0;
        }
        else {
            age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
        }
        String mbti = user.getMbti() == null ? null : user.getMbti().getName();

        return UserRes.ProfileDto.builder()
                .name(user.getName())
                .username(user.getUsername())
                .age(age)
                .instagram(user.getInstagram())
                .phone(user.getPhone())
                .gender(user.getGender())
                .firstBio(user.getFirstBio())
                .secondBio(user.getSecondBio())
                .thirdBio(user.getThirdBio())
                .mbti(mbti)
                .firstTripStyle(hashtagName.get(0))
                .secondTripStyle(hashtagName.get(1))
                .thirdTripStyle(hashtagName.get(2))
                .profileUrl(user.getProfileUrl())
                .isPhonePrivate(user.isPhonePrivate())
                .isNamePrivate(user.isNamePrivate())
                .isInstagramPrivate(user.isInstagramPrivate())
                .isPhonePrivate(user.isPhonePrivate())
                .isMbtiPrivate(user.isMbtiPrivate())
                .build();
    }

    public UserRes.ProfileDto getProfile(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new NotFoundException("존재하지 않는 유저입니다.");
        }
        User user = optionalUser.get();

        List<UserHashtag> userHashtagList = userHashtagRepository.findByUser(user);
        List<String> hashtagName = new ArrayList<>();
        for(UserHashtag userHashtag : userHashtagList) {
            hashtagName.add(userHashtag.getHashtag().getName());
        }
        int hashtagLen = userHashtagList.size();
        for(int i = 0; i < 3 - hashtagLen; i++) {
            hashtagName.add("");
        }

        int age;
        if(user.getBirthDate() == null || user.getBirthDate().isEqual(LocalDate.of(1900, 1, 1))) {
            age = 0;
        }
        else {
            age = LocalDate.now().getYear() - user.getBirthDate().getYear() + 1;
        }
        String mbti = user.getMbti() == null ? null : user.getMbti().getName();

        return UserRes.ProfileDto.builder()
                .name(user.isNamePrivate() ? null : user.getName())
                .username(user.getUsername())
                .age(age)
                .instagram(user.isInstagramPrivate() ? null : user.getInstagram())
                .phone(user.isPhonePrivate() ? null : user.getPhone())
                .gender(user.getGender())
                .firstBio(user.getFirstBio())
                .secondBio(user.getSecondBio())
                .thirdBio(user.getThirdBio())
                .mbti(user.isMbtiPrivate() ? null : mbti)
                .firstTripStyle(hashtagName.get(0))
                .secondTripStyle(hashtagName.get(1))
                .thirdTripStyle(hashtagName.get(2))
                .profileUrl(user.getProfileUrl())
                .build();
    }

    public List<UserRes.MbtiDto> getMbtiList() {
        List<Mbti> mbtiList = mbtiRepository.findAll();
        List<UserRes.MbtiDto> mbtiDtoList = new ArrayList<>();
        for(Mbti mbti : mbtiList) {
            mbtiDtoList.add(UserRes.MbtiDto.builder()
                    .id(mbti.getId())
                    .name(mbti.getName())
                    .build());
        }
        return mbtiDtoList;
    }

    public void updateProfile(Long userId, UserReq.ProfileUpdateDto profileUpdateDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new NotFoundException("존재하지 않는 회원입니다.");
        }
        User user = optionalUser.get();

        Optional<Mbti> optionalMbti = mbtiRepository.findById(profileUpdateDto.getMbtiId());
        if(optionalMbti.isEmpty()) {
            throw new BadRequestException("존재하지 않는 MBTI입니다.");
        }
        Mbti mbti = optionalMbti.get();

        user.setFirstBio(profileUpdateDto.getFirstBio());
        user.setSecondBio(profileUpdateDto.getSecondBio());
        user.setThirdBio(profileUpdateDto.getThirdBio());
        user.setInstagram(profileUpdateDto.getInstagram());
        user.setMbti(mbti);
        user.setPhone(profileUpdateDto.getPhone());
        userRepository.save(user);

        List<Long> newHashtagIds = Arrays.asList(profileUpdateDto.getFirstTripStyleId(),
                profileUpdateDto.getSecondTripStyleId(), profileUpdateDto.getThirdTripStyleId());

        List<UserHashtag> userHashtagList = userHashtagRepository.findByUser(user);
        List<Long> existingHashtagIds = new ArrayList<>();
        for(UserHashtag userHashtag : userHashtagList) {
            existingHashtagIds.add(userHashtag.getHashtag().getId());
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
                UserHashtag userHashtag = UserHashtag.builder()
                        .user(user)
                        .hashtag(hashtag)
                        .build();
                userHashtagRepository.save(userHashtag);
            }
        }

        for(Long hashtagId : existingHashtagIds) {
            if(!newHashtagIds.contains(hashtagId)) {
                Optional<UserHashtag> optionalUserHashtag = Optional.ofNullable(userHashtagRepository.findByUserAndHashtag_Id(user, hashtagId));
                if(optionalUserHashtag.isEmpty()) {
                    throw new BadRequestException("해시태그를 삭제할 수 없습니다.");
                }
                UserHashtag userHashtag = optionalUserHashtag.get();
                userHashtag.setDeleteYn(true);
                userHashtagRepository.save(userHashtag);
            }
        }
    }

    public Long findUserIdByUsername(String username) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(username));
        if(optionalUser.isEmpty()) {
            return 0L;
        }
        return optionalUser.get().getId();

    }

}
