package fajarcode.serverappinitializr.services.generators.modules.java.spring;

import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ProjectStructureModule implements ProjectGenerationModule {

    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packagePath = context.getPackagePath();

        String[] directories = {
                projectPath,
                projectPath + SOURCE_MAIN_JAVA + packagePath,
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/configurations",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/controllers",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/models",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/entities",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/entities/baseentity",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/enums",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/dto",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/dto/request",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/dto/response",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/dto/response/baseresponse",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/services",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/services/interfaces",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/services/implementations",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/repository",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/security",
                projectPath + "/src/main/resources",
                projectPath + "/src/test/java/" + packagePath
        };

        for (String dir : directories) {
            Files.createDirectories(Paths.get(dir));
        }
    }
}