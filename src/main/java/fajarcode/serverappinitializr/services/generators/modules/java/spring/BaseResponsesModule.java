package fajarcode.serverappinitializr.services.generators.modules.java.spring;

import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class BaseResponsesModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String ALL_ARGUMENTS_CONSTRUCTOR = "@AllArgsConstructor\n";
    private static final String NO_ARGUMENTS_CONSTRUCTOR = "@NoArgsConstructor\n";
    private static final String IMPORT_NO_ARGUMENTS_CONSTRUCTOR = "import lombok.NoArgsConstructor;\n\n";
    private static final String IMPORT_ALL_ARGUMENTS_CONSTRUCTOR = "import lombok.AllArgsConstructor;\n";
    private static final String IMPORT_LOMBOK_DATA = "import lombok.Data;\n";
    private static final String BUILDER_DEFAULT = "    @Builder.Default\n";
    private static final String DATA_ANNOTATION = "@Data\n";
    private static final String INDENT = "    ";
    private static final String BLOCK_CLOSE = INDENT + "}\n";
    private static final String BLOCK_CLOSE_WITH_NEWLINE = INDENT + "}\n\n";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String packagePath = context.getPackagePath();

        StringBuilder successResponse = new StringBuilder();
        successResponse.append(PACKAGE).append(packageName).append(".models.dto.response.baseresponse;\n\n");
        successResponse.append("import com.fasterxml.jackson.annotation.JsonInclude;\n");
        successResponse.append(IMPORT_ALL_ARGUMENTS_CONSTRUCTOR);
        successResponse.append("import lombok.Builder;\n");
        successResponse.append(IMPORT_LOMBOK_DATA);
        successResponse.append(IMPORT_NO_ARGUMENTS_CONSTRUCTOR);
        successResponse.append("import java.time.LocalDateTime;\n\n");
        successResponse.append(DATA_ANNOTATION);
        successResponse.append("@Builder\n");
        successResponse.append(NO_ARGUMENTS_CONSTRUCTOR);
        successResponse.append(ALL_ARGUMENTS_CONSTRUCTOR);
        successResponse.append("@JsonInclude(JsonInclude.Include.NON_NULL)\n");
        successResponse.append("public class SuccessResponse<T> {\n\n");
        successResponse.append(BUILDER_DEFAULT);
        successResponse.append("    private boolean success = true;\n\n");
        successResponse.append("    private String message;\n\n");
        successResponse.append("    private T data;\n\n");
        successResponse.append(BUILDER_DEFAULT);
        successResponse.append("    private LocalDateTime timestamp = LocalDateTime.now();\n\n");
        successResponse.append("    private String path;\n\n");
        successResponse.append("    public static <T> SuccessResponse<T> of(String message, T data) {\n");
        successResponse.append("        return SuccessResponse.<T>builder()\n");
        successResponse.append("                .success(true)\n");
        successResponse.append("                .message(message)\n");
        successResponse.append("                .data(data)\n");
        successResponse.append("                .timestamp(LocalDateTime.now())\n");
        successResponse.append("                .build();\n");
        successResponse.append(BLOCK_CLOSE_WITH_NEWLINE);
        successResponse.append("    public static <T> SuccessResponse<T> of(T data) {\n");
        successResponse.append("        return SuccessResponse.<T>builder()\n");
        successResponse.append("                .success(true)\n");
        successResponse.append("                .message(\"Success\")\n");
        successResponse.append("                .data(data)\n");
        successResponse.append("                .timestamp(LocalDateTime.now())\n");
        successResponse.append("                .build();\n");
        successResponse.append(BLOCK_CLOSE);
        successResponse.append("}\n");

        String successPath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/dto/response/baseresponse/SuccessResponse.java";
        Files.writeString(Paths.get(successPath), successResponse.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/dto/response/baseresponse/SuccessResponse.java");

        StringBuilder errorResponse = new StringBuilder();
        errorResponse.append(PACKAGE).append(packageName).append(".models.dto.response.baseresponse;\n\n");
        errorResponse.append("import com.fasterxml.jackson.annotation.JsonInclude;\n");
        errorResponse.append(IMPORT_ALL_ARGUMENTS_CONSTRUCTOR);
        errorResponse.append("import lombok.Builder;\n");
        errorResponse.append(IMPORT_LOMBOK_DATA);
        errorResponse.append(IMPORT_NO_ARGUMENTS_CONSTRUCTOR);
        errorResponse.append("import java.time.LocalDateTime;\n");
        errorResponse.append("import java.util.List;\n\n");
        errorResponse.append(DATA_ANNOTATION);
        errorResponse.append("@Builder\n");
        errorResponse.append(NO_ARGUMENTS_CONSTRUCTOR);
        errorResponse.append(ALL_ARGUMENTS_CONSTRUCTOR);
        errorResponse.append("@JsonInclude(JsonInclude.Include.NON_NULL)\n");
        errorResponse.append("public class ErrorResponse {\n\n");
        errorResponse.append(BUILDER_DEFAULT);
        errorResponse.append("    private boolean success = false;\n\n");
        errorResponse.append("    private String message;\n\n");
        errorResponse.append("    private String error;\n\n");
        errorResponse.append("    private Integer status;\n\n");
        errorResponse.append(BUILDER_DEFAULT);
        errorResponse.append("    private LocalDateTime timestamp = LocalDateTime.now();\n\n");
        errorResponse.append("    private String path;\n\n");
        errorResponse.append("    private List<ValidationError> errors;\n\n");
        errorResponse.append("    @Data\n");
        errorResponse.append("    @Builder\n");
        errorResponse.append("    @NoArgsConstructor\n");
        errorResponse.append("    @AllArgsConstructor\n");
        errorResponse.append("    public static class ValidationError {\n");
        errorResponse.append("        private String field;\n");
        errorResponse.append("        private String message;\n");
        errorResponse.append(BLOCK_CLOSE);
        errorResponse.append("}\n");

        String errorPath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/dto/response/baseresponse/ErrorResponse.java";
        Files.writeString(Paths.get(errorPath), errorResponse.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/dto/response/baseresponse/ErrorResponse.java");

        StringBuilder sampleRequest = new StringBuilder();
        sampleRequest.append(PACKAGE).append(packageName).append(".models.dto.request;\n\n");
        sampleRequest.append("public class SampleRequest {\n\n");
        sampleRequest.append("}\n");

        String samplePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/dto/request/SampleRequest.java";
        Files.writeString(Paths.get(samplePath), sampleRequest.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/dto/request/SampleRequest.java");
    }
}