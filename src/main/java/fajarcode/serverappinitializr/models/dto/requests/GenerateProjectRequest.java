package fajarcode.serverappinitializr.models.dto.requests;

import fajarcode.serverappinitializr.models.enums.DatabaseType;
import fajarcode.serverappinitializr.models.enums.FrameworkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenerateProjectRequest {
    @NotBlank(message = "Application name is required")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "Application name must start with letter and contain only alphanumeric characters")
    private String applicationName;

    @NotNull(message = "Framework type is required")
    private FrameworkType frameworkType;

    @NotNull(message = "Database type is required")
    private DatabaseType databaseType;

    private Boolean jwtAuthEnabled;

    private Boolean baseEntityEnabled;

    private Boolean baseResponseEnabled;

    private String packageName;

    private String projectPath;

    private Long fileSizeBytes;

    private String groupId;

    private String artifactId;

    private String version;

    private String javaVersion;
}
