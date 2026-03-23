package fajarcode.serverappinitializr.services.generators;

import java.io.IOException;

public interface ProjectGenerationModule {
    void generate(GenerationContext context) throws IOException;
}
