package fajarcode.serverappinitializr.models.entities;

import fajarcode.serverappinitializr.models.enums.DatabaseType;
import fajarcode.serverappinitializr.models.enums.FrameworkType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GeneratedProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_name", nullable = false)
    private String applicationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "framework_type", nullable = false)
    private FrameworkType frameworkType;

    @Enumerated(EnumType.STRING)
    @Column(name = "database_type", nullable = false)
    private DatabaseType databaseType;

    @Column(name = "jwt_auth_enabled")
    private Boolean jwtAuthEnabled;

    @Column(name = "base_entity_enabled")
    private Boolean baseEntityEnabled;

    @Column(name = "base_response_enabled")
    private Boolean baseResponseEnabled;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "project_path")
    private String projectPath;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "artifact_id")
    private String artifactId;

    @Column(name = "version")
    private String version;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
