package team.themoment.everygsm.server.v2.domain.project.dto.common;

public record RepositoryDto(String repoName, String repoUrl) {
    public RepositoryDto(String repoUrl) {
        this(repoUrl.substring(repoUrl.lastIndexOf('/') + 1), repoUrl);
    }
}
