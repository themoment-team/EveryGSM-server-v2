package team.themoment.everygsm.server.v2.domain.image.service;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import team.themoment.everygsm.server.v2.domain.image.dto.response.ImageUploadResDto;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class UploadImageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set
            .of("image/jpeg", "image/png", "image/gif", "image/webp");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public ImageUploadResDto execute(MultipartFile image) {
        validateContentType(image);

        String key = generateKey(image);
        upload(image, key);

        String presignedUrl = generatePresignedUrl(key);
        return new ImageUploadResDto(key, presignedUrl);
    }

    private void validateContentType(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ExpectedException("지원하지 않는 이미지 형식입니다. (jpeg, png, gif, webp만 허용)", HttpStatus.BAD_REQUEST);
        }
    }

    private String generateKey(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "images/" + UUID.randomUUID() + extension;
    }

    private void upload(MultipartFile image, String key) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder().bucket(bucket).key(key)
                    .contentType(image.getContentType()).contentLength(image.getSize()).build();
            s3Client.putObject(putRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
        } catch (IOException e) {
            throw new ExpectedException("이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generatePresignedUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1)).getObjectRequest(req -> req.bucket(bucket).key(key)).build();
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
}
