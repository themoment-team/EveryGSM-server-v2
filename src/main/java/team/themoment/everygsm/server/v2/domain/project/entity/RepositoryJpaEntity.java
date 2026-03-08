package team.themoment.everygsm.server.v2.domain.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repo", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "repo_name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RepositoryJpaEntity {

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

    public RepositoryJpaEntity(ProjectJpaEntity project, String repoName, String repoUrl) {
        this.project = project;
        this.repoName = repoName;
        this.repoUrl = repoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RepositoryJpaEntity other))
            return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
