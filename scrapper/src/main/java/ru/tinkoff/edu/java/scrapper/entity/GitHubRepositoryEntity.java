package ru.tinkoff.edu.java.scrapper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "github_repositories")
public class GitHubRepositoryEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    @Setter
    private String name;

    @Column(name = "username")
    @Setter
    private String username;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @CreationTimestamp
    @Column(name = "updated_at")
    @Setter
    private OffsetDateTime updatedAt;

    @CreationTimestamp
    @Setter
    @Column(name = "issues_updated_at")
    private OffsetDateTime issuesUpdatedAt;
}
