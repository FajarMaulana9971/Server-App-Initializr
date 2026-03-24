package fajarcode.serverappinitializr.services.generators;

import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class GenerationContext {
    private final GenerateProjectRequest request;
    private final String projectName;
    private final String packageName;
    private final String projectPath;
    private final String packagePath;
    private final List<String> generatedFiles;
}
