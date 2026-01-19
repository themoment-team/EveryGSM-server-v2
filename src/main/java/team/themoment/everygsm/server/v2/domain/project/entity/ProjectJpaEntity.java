package team.themoment.everygsm.server.v2.domain.project.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;

@Entity
@Table(name = "projects")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class ProjectJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(nullable = false, length = 512)
    private String logo;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 512)
    private String affiliation;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(name = "prod_url", nullable = false, length = 512)
    private String prodUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(length = 512)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "repo_urls", joinColumns = @JoinColumn(name = "project_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "project_id", "repo_url"}))
    @Column(name = "repo_url", nullable = false, length = 512)
    private Set<String> repoUrls;

    @ElementCollection
    @CollectionTable(name = "stack_names", joinColumns = @JoinColumn(name = "project_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "project_id", "stack_name"}))
    @Column(name = "stack_name", nullable = false)
    private Set<String> stackNames;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public ProjectJpaEntity(UserJpaEntity user, String logo, String title, String affiliation, String description,
            String prodUrl, Status status, String reason, Set<String> repoUrls, Set<String> stackNames) {
        this.user = user;
        this.logo = logo;
        this.title = title;
        this.affiliation = affiliation;
        this.description = description;
        this.prodUrl = prodUrl;
        this.status = status;
        this.reason = reason;
        this.repoUrls = repoUrls != null ? repoUrls : new HashSet<>();
        this.stackNames = stackNames != null ? stackNames : new HashSet<>();
    }

    public void updateStatus(Status status, String reason) {
        this.status = status;
        this.reason = reason;
    }
}
