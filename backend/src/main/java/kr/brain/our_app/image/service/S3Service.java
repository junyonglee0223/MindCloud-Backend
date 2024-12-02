package kr.brain.our_app.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    public List<String> listFiles() {
        List<String> fileUrls = new ArrayList<>();
        ObjectListing objectListing = s3Client.listObjects(bucketName);
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            String fileUrl = s3Client.getUrl(bucketName, os.getKey()).toString();
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }
    // 파일 삭제
    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    // 이미지 URL을 받아 S3에 업로드하는 메서드
    public String uploadImageFromUrl(String imageUrl, String title, String userId) throws IOException {
        URL url = new URL(imageUrl);
        try (InputStream inputStream = url.openStream()) {
            // 파일명은 title과 userId를 조합하여 고유 생성
            String fileName = title.replaceAll("\\s+", "_") + "_" + userId + ".jpg";
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return s3Client.getUrl(bucketName, fileName).toString();
        }
    }

    // 파일명으로 조회하는 메서드 추가
    public String getFileUrl(String title, String userId) {
        String fileName = title.replaceAll("\\s+", "_") + "_" + userId + ".jpg";
        return s3Client.getUrl(bucketName, fileName).toString();
    }
}
