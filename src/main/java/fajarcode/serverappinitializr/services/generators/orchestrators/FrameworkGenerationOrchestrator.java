package fajarcode.serverappinitializr.services.generators.orchestrators;

import fajarcode.serverappinitializr.models.enums.FrameworkType;
import fajarcode.serverappinitializr.services.generators.GenerationContext;

import java.io.IOException;

public interface FrameworkGenerationOrchestrator {
    FrameworkType supportedFramework();
    void generate(GenerationContext context) throws IOException;
}