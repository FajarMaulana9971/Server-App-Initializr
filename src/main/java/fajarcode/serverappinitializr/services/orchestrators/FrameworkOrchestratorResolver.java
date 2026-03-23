package fajarcode.serverappinitializr.services.orchestrators;

import fajarcode.serverappinitializr.exceptions.BadRequestException;
import fajarcode.serverappinitializr.models.enums.FrameworkType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class FrameworkOrchestratorResolver {

    private final Map<FrameworkType, FrameworkGenerationOrchestrator> orchestrators = new EnumMap<>(FrameworkType.class);

    public FrameworkOrchestratorResolver(List<FrameworkGenerationOrchestrator> orchestratorList) {
        for (FrameworkGenerationOrchestrator orchestrator : orchestratorList) {
            orchestrators.put(orchestrator.supportedFramework(), orchestrator);
        }
    }

    public FrameworkGenerationOrchestrator resolve(FrameworkType frameworkType) {
        FrameworkGenerationOrchestrator orchestrator = orchestrators.get(frameworkType);
        if (orchestrator == null) {
            throw new BadRequestException("Framework is not supported yet: " + frameworkType);
        }
        return orchestrator;
    }
}