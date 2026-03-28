package fajarcode.serverappinitializr.services.generators.modules.java.spring;

import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class SampleControllerModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String IMPORT = "import ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String INDENT = " ";
    private static final String BLOCK_CLOSE = INDENT + "}\n";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String packagePath = context.getPackagePath();
        GenerateProjectRequest request = context.getRequest();

        StringBuilder controller = new StringBuilder();

        controller.append(PACKAGE).append(packageName).append(".controllers;\n\n");
        controller.append(IMPORT).append(packageName).append(".services.interfaces.SampleService;\n");
        if (request.getBaseResponseEnabled()) {
            controller.append(IMPORT).append(packageName).append(".models.dto.response.baseresponse.SuccessResponse;\n");
        }
        controller.append("import lombok.RequiredArgsConstructor;\n");
        controller.append("import org.springframework.http.ResponseEntity;\n");
        controller.append("import org.springframework.web.bind.annotation.*;\n\n");
        controller.append("@RestController\n");
        controller.append("@RequestMapping(\"/api/sample\")\n");
        controller.append("@RequiredArgsConstructor\n");
        controller.append("public class SampleController {\n\n");
        controller.append("    private final SampleService sampleService;\n\n");
        controller.append("    @GetMapping\n");
        controller.append("    public ResponseEntity<?> getSample() {\n");
        if (request.getBaseResponseEnabled()) {
            controller.append("        return ResponseEntity.ok(SuccessResponse.of(\"Sample endpoint\", \"Hello from ").append(request.getApplicationName()).append("!\"));\n");
        } else {
            controller.append("        return ResponseEntity.ok(\"Hello from ").append(request.getApplicationName()).append("!\");\n");
        }
        controller.append(BLOCK_CLOSE);
        controller.append("}\n");

        String filePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/controllers/SampleController.java";
        Files.writeString(Paths.get(filePath), controller.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/controllers/SampleController.java");
    }
}