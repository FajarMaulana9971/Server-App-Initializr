package fajarcode.serverappinitializr.services.generators.modules;

import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class MainClassModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String DELIMITER_PATH = "/";
    private static final String INDENT = "    ";
    private static final String BLOCK_CLOSE = INDENT + "}\n";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String projectName = context.getProjectName();

        String className = projectName + "Application";
        StringBuilder mainClass = new StringBuilder();

        mainClass.append(PACKAGE).append(packageName).append(";\n\n");
        mainClass.append("import org.springframework.boot.SpringApplication;\n");
        mainClass.append("import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n");
        mainClass.append("@SpringBootApplication\n");
        mainClass.append("public class ").append(className).append(" {\n");
        mainClass.append("    public static void main(String[] args) {\n");
        mainClass.append("        SpringApplication.run(").append(className).append(".class, args);\n");
        mainClass.append(BLOCK_CLOSE);
        mainClass.append("}\n");

        String packagePath = context.getPackagePath();
        String filePath = projectPath + SOURCE_MAIN_JAVA + packagePath + DELIMITER_PATH + className + ".java";
        Files.writeString(Paths.get(filePath), mainClass.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + DELIMITER_PATH + className + ".java");
    }
}