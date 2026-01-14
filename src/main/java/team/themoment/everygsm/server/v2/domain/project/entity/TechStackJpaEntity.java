package team.themoment.everygsm.server.v2.domain.project.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tech_stacks")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TechStackJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectJpaEntity project;

    @Column(name = "stack_name", nullable = false)
    private String stackName;

    @Builder
    public TechStackJpaEntity(ProjectJpaEntity project, String stackName) {
        this.project = project;
        this.stackName = stackName;
    }
}
