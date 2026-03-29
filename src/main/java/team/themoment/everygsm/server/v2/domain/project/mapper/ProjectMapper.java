package team.themoment.everygsm.server.v2.domain.project.mapper;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;

@Component
@RequiredArgsConstructor
public class ProjectMapper {

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public ProjectResDto toRes(ProjectJpaEntity project, boolean liked) {
        List<TechStackDto> techStacks = extractTechStacks(project);
        List<RepositoryDto> repositories = extractRepositories(project);

        return new ProjectResDto(project.getId(),
                generatePresignedUrl(project.getLogo()),
                project.getTitle(),
                project.getAffiliation(),
                project.getDescription(),
                project.getProdUrl(),
                project.getStatus(),
                project.getReason(),
                project.getCreatedAt(),
                techStacks,
                repositories,
                liked);
    }

    private String generatePresignedUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1)).getObjectRequest(req -> req.bucket(bucket).key(key)).build();
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    public List<TechStackDto> extractTechStacks(ProjectJpaEntity project) {
        return project.getStackNames().stream().map(TechStackDto::new).toList();
    }

    public List<RepositoryDto> extractRepositories(ProjectJpaEntity project) {
        return project.getRepoUrls().stream().map(RepositoryDto::new).toList();
    }
}
