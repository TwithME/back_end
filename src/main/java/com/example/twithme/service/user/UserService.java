package com.example.twithme.service.user;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.exception.NotFoundException;

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
import com.example.twithme.config.security.JwtTokenProvider;
import com.example.twithme.service.S3Service;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

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
                    .name(user.getName())
                    .alarmNum(0)
                    .accessToken(jwtTokenProvider.createAccessToken(String.valueOf(user.getId()), Collections.singletonList(user.getUserRole())))
                    .isFirstLogin(isFirstLogin)
                    .build();
        }
        return UserRes.UserInfoWithToken.builder()
                .id(0L)
                .build();
    }

    //유저만 생성 및 저장
    public void doKakaoSignUp(UserReq.KakaoLogInDto kakaoLogInDto) {
        User user = User.builder()
                .snsId(kakaoLogInDto.getSnsId())
                .snsToken(kakaoLogInDto.getSnsToken())
                .loginType("kakao")
                .name(kakaoLogInDto.getName())
                .profileUrl(kakaoLogInDto.getProfileImage())
                .firstLogin(true)
                .userRole("ROLE_USER")
                .build();
        userRepository.save(user);
    }


    //기본 정보 이외에 추가 정보 저장
    public void doKakaoAdditionalSignUp(Long userId, UserReq.KakaoSignUpDto kakaoSignUpDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setGender(kakaoSignUpDto.getGender());
            user.setPhone(kakaoSignUpDto.getPhone());
            user.setInstagram(kakaoSignUpDto.getInstagram());
            user.setBirthDate(kakaoSignUpDto.getBirthDate());
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




    public void uploadProfileImage(Long userId, MultipartFile multipartFile) {
        String profileUrl = s3Service.uploadImage("profile", Long.toString(userId), multipartFile);

        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setProfileUrl(profileUrl);
            userRepository.save(user);
        }
        else {
            throw new NotFoundException("존재하지 않는 회원입니다.");
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



    public String getKakaoAccessToken(String code) {
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + "0f8b7cf617336b262bd00ba6ed4f7805");
            sb.append("&redirect_uri=" +"http://semtle.catholic.ac.kr:8081/oauth/kakao"); //"http://semtle.catholic.ac.kr:8081/oauth/kakao"
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();


            System.out.println("======authService 요청 결과 시작======");
            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token for kakao: " + access_Token);
            System.out.println("refresh_token for kakao: " + refresh_Token);
            System.out.println("======authService 요청 결과 끝=======");

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    @Transactional()
    public UserReq.KakaoLogInDto createAndLoginKakaoUser(String accessTokenFromSocial)  {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessTokenFromSocial); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);


            //카카오에서 받은 정보 파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String kakaoId = element.getAsJsonObject().get("id").getAsString(); //카카오의 인덱스


            //카카오에 저장된 이름
            String name = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            JsonElement jsonElement = element.getAsJsonObject().get("properties").getAsJsonObject().get("profile_image");
            String profileUrl = "";
            if (jsonElement != null) {
                profileUrl = jsonElement.getAsString();
            }

            UserReq.KakaoLogInDto kakaoLogInDto = UserReq.KakaoLogInDto.builder()
                    .snsId(kakaoId)
                    .snsToken(accessTokenFromSocial)
                    .profileImage(profileUrl)
                    .name(name)
                    .build();

            return kakaoLogInDto;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



}
