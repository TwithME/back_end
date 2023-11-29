package com.example.twithme.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.twithme.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    public String uploadImage(String folderName, String fileName, MultipartFile multipartFile) {
        if(multipartFile == null) {
            return null;
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        String uploadFileName = concatFolderName(folderName, fileName);
        try {
            InputStream inputStream = multipartFile.getInputStream();
            amazonS3Client.putObject(
                    new PutObjectRequest("twithme", uploadFileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        }
        catch(IOException e) {
            throw new BadRequestException("잘못된 형식의 파일입니다.");
        }
        return getImageUrl(uploadFileName);
    }

    public String concatFolderName(String folderName, String fileName) {
        return folderName + "/" + UUID.randomUUID() + "-" + fileName;
    }

    public String getImageUrl(String uploadFileName) {
        return "https://twithme.s3.ap-northeast-2.amazonaws.com/" + uploadFileName;
    }
}
