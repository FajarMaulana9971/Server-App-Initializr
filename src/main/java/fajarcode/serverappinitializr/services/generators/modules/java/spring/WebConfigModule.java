package fajarcode.serverappinitializr.services.generators.modules.java.spring;

import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class WebConfigModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String INDENT = " ";
    private static final String BLOCK_CLOSE = INDENT + "}\n";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String packagePath = context.getPackagePath();

        StringBuilder webConfig = new StringBuilder();
        webConfig.append(PACKAGE).append(packageName).append(".configurations;\n\n");
        webConfig.append("import org.springframework.context.annotation.Configuration;\n");
        webConfig.append("import org.springframework.web.servlet.config.annotation.CorsRegistry;\n");
        webConfig.append("import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;\n\n");
        webConfig.append("@Configuration\n");
        webConfig.append("public class WebConfig implements WebMvcConfigurer {\n\n");
        webConfig.append("    @Override\n");
        webConfig.append("    public void addCorsMappings(CorsRegistry registry) {\n");
        webConfig.append("        registry.addMapping(\"/**\")\n");
        webConfig.append("                .allowedOrigins(\"*\")\n");
        webConfig.append("                .allowedMethods(\"GET\", \"POST\", \"PUT\", \"DELETE\", \"PATCH\")\n");
        webConfig.append("                .allowedHeaders(\"*\");\n");
        webConfig.append(BLOCK_CLOSE);
        webConfig.append("}\n");

        String webConfigPath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/configurations/WebConfig.java";
        Files.writeString(Paths.get(webConfigPath), webConfig.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/configurations/WebConfig.java");
    }
}