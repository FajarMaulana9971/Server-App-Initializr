package fajarcode.serverappinitializr.services.generators.modules;

import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class SampleServiceModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String IMPORT = "import ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String INDENT = "    ";
    private static final String BLOCK_CLOSE = INDENT + "}\n";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String packagePath = context.getPackagePath();

        StringBuilder serviceInterface = new StringBuilder();
        serviceInterface.append(PACKAGE).append(packageName).append(".services.interfaces;\n\n");
        serviceInterface.append("public interface SampleService {\n");
        serviceInterface.append("    String getSampleData();\n");
        serviceInterface.append("}\n");

        String interfacePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/services/interfaces/SampleService.java";
        Files.writeString(Paths.get(interfacePath), serviceInterface.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/services/interfaces/SampleService.java");

        StringBuilder serviceImpl = new StringBuilder();
        serviceImpl.append(PACKAGE).append(packageName).append(".services.implementations;\n\n");
        serviceImpl.append(IMPORT).append(packageName).append(".services.interfaces.SampleService;\n");
        serviceImpl.append("import lombok.RequiredArgsConstructor;\n");
        serviceImpl.append("import org.springframework.stereotype.Service;\n\n");
        serviceImpl.append("@Service\n");
        serviceImpl.append("@RequiredArgsConstructor\n");
        serviceImpl.append("public class SampleServiceImpl implements SampleService {\n\n");
        serviceImpl.append("    @Override\n");
        serviceImpl.append("    public String getSampleData() {\n");
        serviceImpl.append("        return \"Sample data from service\";\n");
        serviceImpl.append(BLOCK_CLOSE);
        serviceImpl.append("}\n");

        String implPath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/services/implementations/SampleServiceImpl.java";
        Files.writeString(Paths.get(implPath), serviceImpl.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/services/implementations/SampleServiceImpl.java");
    }
}