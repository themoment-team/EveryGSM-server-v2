package team.themoment.everygsm.server.v2.domain.project.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repositories")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class RepositoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectJpaEntity project;

    @Column(name = "repo_url", nullable = false, length = 512)
    private String repoUrl;

    @Builder
    public RepositoryJpaEntity(ProjectJpaEntity project, String repoUrl) {
        this.project = project;
        this.repoUrl = repoUrl;
    }
}
