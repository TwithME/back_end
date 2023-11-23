package com.example.twithme.common.service;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.exception.ServerErrorException;
import com.example.twithme.common.exception.dto.HttpRes;
import com.example.twithme.common.exception.dto.SmsDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsService {

    RestTemplate restTemplate;

    @Autowired
    public SmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

//    환경 변수화 필요
    private static final String API_KEY = "ncp:sms:kr:305818671467:tripyle";
    private static final String ACCESS_KEY = "CpWVRffRgqrDYgXyC1WW";
    private static final String SECRET_KEY = "AUOX7pqDo2G7cZDt3NVW8gRJK3l8W7ESgzKcFjB2";
    private static final String OUTGOING_PHONE_NUM = "01050012106";
    private static final String SMS_URI = String.format("/sms/v2/services/%s/messages", API_KEY);
    private static final String SMS_URL = "https://sens.apigw.ntruss.com" + SMS_URI;

    public ResponseEntity<HttpRes<String>> sendSms(String incomingPhoneNum, String messageContent) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-ncp-apigw-timestamp", timestamp);
        headers.set("x-ncp-iam-access-key", ACCESS_KEY);
        headers.set("x-ncp-apigw-signature-v2", createSignature(SMS_URI, timestamp)); // Secret Key 암호화

        List<SmsDto.IncomingNumDto> incomingNums = new ArrayList<>();
        incomingNums.add(SmsDto.IncomingNumDto.builder()
                .to(incomingPhoneNum)
                .build());

        SmsDto.SmsRequestDto request = SmsDto.SmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(OUTGOING_PHONE_NUM)
                .content(messageContent)
                .incomingNums(incomingNums)
                .build();

        HttpEntity<SmsDto.SmsRequestDto> requestHttpEntity = new HttpEntity<>(request, headers);
        try {
            SmsDto.SmsResponseDto response = restTemplate.postForObject(SMS_URL, requestHttpEntity, SmsDto.SmsResponseDto.class);
            if(response == null || response.getStatusCode() != HttpStatus.ACCEPTED.value()){
                throw new ServerErrorException();
            }
        }
        catch (RestClientException e) {
            throw new ServerErrorException();
        }
        HttpRes<String> httpRes = new HttpRes<>(incomingPhoneNum + "로 인증번호가 전송되었습니다.");
        return new ResponseEntity<>(httpRes, HttpStatus.OK);
    }

    public String createSignature(String uri, String timestamp) {
        String message = "POST " + uri + "\n" + timestamp + "\n" + ACCESS_KEY;

        byte[] bytes;
        bytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec signingKey = new SecretKeySpec(bytes, "HmacSHA256");

        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new BadRequestException();
        }

        try {
            mac.init(signingKey);
        }
        catch (InvalidKeyException e) {
            throw new BadRequestException();
        }

        byte[] rawHmac;
        rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64String(rawHmac);
    }
}
