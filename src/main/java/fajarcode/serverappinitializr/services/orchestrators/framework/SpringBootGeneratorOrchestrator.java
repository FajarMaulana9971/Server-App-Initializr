package fajarcode.serverappinitializr.services.orchestrators.framework;

import fajarcode.serverappinitializr.models.enums.FrameworkType;
import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.ApplicationPropertiesModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.BaseEntityModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.BaseResponsesModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.EnumsModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.JwtModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.MainClassModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.PomXmlModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.ProjectStructureModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.SampleControllerModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.SampleEntityModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.SampleServiceModule;
import fajarcode.serverappinitializr.services.generators.modules.java.spring.WebConfigModule;
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

        if (Boolean.TRUE.equals(context.getRequest().getBaseEntityEnabled())) {
            baseEntityModule.generate(context);
        }

        if (Boolean.TRUE.equals(context.getRequest().getBaseResponseEnabled())) {
            baseResponsesModule.generate(context);
        }

        if (Boolean.TRUE.equals(context.getRequest().getJwtAuthEnabled())) {
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