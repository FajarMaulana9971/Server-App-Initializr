package fajarcode.serverappinitializr.services.generators.modules.java.spring;

import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class EnumsModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String packagePath = context.getPackagePath();

        StringBuilder statusEnum = new StringBuilder();

        statusEnum.append(PACKAGE).append(packageName).append(".models.enums;\n\n");
        statusEnum.append("public enum Status {\n");
        statusEnum.append("    ACTIVE,\n");
        statusEnum.append("    INACTIVE,\n");
        statusEnum.append("    PENDING,\n");
        statusEnum.append("    DELETED\n");
        statusEnum.append("}\n");

        String filePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/enums/Status.java";
        Files.writeString(Paths.get(filePath), statusEnum.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/enums/Status.java");
    }
}