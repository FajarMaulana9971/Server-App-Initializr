package fajarcode.serverappinitializr.services.orchestrators.framework;

import fajarcode.serverappinitializr.models.enums.FrameworkType;
import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.modules.ApplicationPropertiesModule;
import fajarcode.serverappinitializr.services.generators.modules.BaseEntityModule;
import fajarcode.serverappinitializr.services.generators.modules.BaseResponsesModule;
import fajarcode.serverappinitializr.services.generators.modules.EnumsModule;
import fajarcode.serverappinitializr.services.generators.modules.JwtModule;
import fajarcode.serverappinitializr.services.generators.modules.MainClassModule;
import fajarcode.serverappinitializr.services.generators.modules.PomXmlModule;
import fajarcode.serverappinitializr.services.generators.modules.ProjectStructureModule;
import fajarcode.serverappinitializr.services.generators.modules.SampleControllerModule;
import fajarcode.serverappinitializr.services.generators.modules.SampleEntityModule;
import fajarcode.serverappinitializr.services.generators.modules.SampleServiceModule;
import fajarcode.serverappinitializr.services.generators.modules.WebConfigModule;
import fajarcode.serverappinitializr.services.orchestrators.FrameworkGenerationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SpringBootGeneratorOrchestrator implements FrameworkGenerationOrchestrator {
    private final ProjectStructureModule projectStructureModule;
    private final PomXmlModule pomXmlModule;
    private final ApplicationPropertiesModule applicationPropertiesModule;
    private final MainClassModule mainClassModule;
    private final BaseEntityModule baseEntityModule;
    private final BaseResponsesModule baseResponsesModule;
    private final JwtModule jwtModule;
    private final SampleControllerModule sampleControllerModule;
    private final SampleServiceModule sampleServiceModule;
    private final SampleEntityModule sampleEntityModule;
    private final EnumsModule enumsModule;
    private final WebConfigModule webConfigModule;

    public void generate(GenerationContext context) throws IOException {
        projectStructureModule.generate(context);
        pomXmlModule.generate(context);
        applicationPropertiesModule.generate(context);
        mainClassModule.generate(context);

        if (context.getRequest().getBaseEntityEnabled()) {
            baseEntityModule.generate(context);
        }

        if (context.getRequest().getBaseResponseEnabled()) {
            baseResponsesModule.generate(context);
        }

        if (context.getRequest().getJwtAuthEnabled()) {
            jwtModule.generate(context);
        }

        sampleControllerModule.generate(context);
        sampleServiceModule.generate(context);
        sampleEntityModule.generate(context);
        enumsModule.generate(context);
        webConfigModule.generate(context);
    }

    @Override
    public FrameworkType supportedFramework() {
        return FrameworkType.SPRINGBOOT;
    }
}