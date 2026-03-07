package team.themoment.everygsm.server.v2.domain.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repo_urls", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "repo_name"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RepoUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectJpaEntity project;

    @Column(name = "repo_name", nullable = false)
    private String repoName;

    @Column(name = "repo_url", nullable = false, length = 512)
    private String repoUrl;

    public RepoUrl(ProjectJpaEntity project, String repoName, String repoUrl) {
        this.project = project;
        this.repoName = repoName;
        this.repoUrl = repoUrl;
    }
}
