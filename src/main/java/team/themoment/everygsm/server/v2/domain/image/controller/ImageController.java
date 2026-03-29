package team.themoment.everygsm.server.v2.domain.image.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.image.dto.response.ImageUploadResDto;
import team.themoment.everygsm.server.v2.domain.image.service.UploadImageService;

@Tag(name = "Image", description = "이미지 API")
@RestController
@RequestMapping("/api/v2/images")
@RequiredArgsConstructor
public class ImageController {

    private final UploadImageService uploadImageService;

    @Operation(summary = "이미지 업로드", description = "이미지를 S3에 업로드하고 Pre-Signed URL을 반환합니다")
    @PostMapping(consumes = "multipart/form-data")
    public ImageUploadResDto upload(@RequestPart MultipartFile image) {
        return uploadImageService.execute(image);
    }
}