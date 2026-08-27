package team.themoment.everygsm.server.v2.domain.project.mapper;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import team.themoment.everygsm.server.v2.domain.project.dto.common.ParticipantDto;
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
        List<ParticipantDto> participants = extractParticipants(project);

        return new ProjectResDto(project.getId(),
                project.getOriginalProjectId(),
                generatePresignedUrl(project.getLogo()),
                project.getTitle(),
                project.getAffiliation(),
                project.getDescription(),
                project.getProdUrl(),
                project.getStartYear(),
                project.getStatus(),
                project.getReason(),
                project.getCreatedAt(),
                techStacks,
                repositories,
                participants,
                liked,
                project.getDatagsmStatus(),
                project.getDatagsmEndYear());
    }

    private String generatePresignedUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
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

    public List<ParticipantDto> extractParticipants(ProjectJpaEntity project) {
        if (project.getParticipants() == null) {
            return List.of();
        }
        return project.getParticipants().stream()
                .map(u -> new ParticipantDto(u.getId(), u.getName(), u.getStudentNumber()))
                .sorted(Comparator.comparing(ParticipantDto::studentNumber).thenComparing(ParticipantDto::userId))
                .toList();
    }
}
