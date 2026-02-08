package fajarcode.serverappinitializr.services.implementations;

import fajarcode.serverappinitializr.exceptions.BadRequestException;
import fajarcode.serverappinitializr.exceptions.InternalServerErrorException;
import fajarcode.serverappinitializr.exceptions.NotFoundException;
import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.models.dto.responses.GenerateProjectResponse;
import fajarcode.serverappinitializr.models.dto.responses.base.BaseResponse;
import fajarcode.serverappinitializr.models.entities.GeneratedProject;
import fajarcode.serverappinitializr.models.enums.DatabaseType;
import fajarcode.serverappinitializr.models.enums.FrameworkType;
import fajarcode.serverappinitializr.repositories.GeneratedProjectRepository;
import fajarcode.serverappinitializr.services.interfaces.SpringBootGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringBootGeneratorServiceImplementation implements SpringBootGeneratorService {
    private final GeneratedProjectRepository generatedProjectRepository;

    private static final String GENERATED_PROJECTS_DIR = "generated-projects";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String ALL_ARGUMENTS_CONSTRUCTOR = "@AllArgsConstructor\n";
    private static final String NO_ARGUMENTS_CONSTRUCTOR = "@NoArgsConstructor\n";
    private static final String IMPORT_NO_ARGUMENTS_CONSTRUCTOR = "import lombok.NoArgsConstructor;\n\n";
    private static final String IMPORT_ALL_ARGUMENTS_CONSTRUCTOR = "import lombok.AllArgsConstructor;\n";
    private static final String IMPORT = "import ";
    private static final String DEPENDENCY = "        </dependency>\n";
    private static final String OPEN_DEPENDENCY_SECTION = "        <dependency>\n";
    private static final String CLOSED_DEPENDENCY_SECTION = "        </dependency>\n\n";
    private static final String IMPORT_LOMBOK_DATA = "import lombok.Data;\n";
    private static final String BUILDER_DEFAULT = "    @Builder.Default\n";
    private static final String GROUP_ID_DEPENDENCY = "            <groupId>org.springframework.boot</groupId>\n";
    private static final String GROUP_ID_JSON_WEB_TOKEN = "            <groupId>io.jsonwebtoken</groupId>\n";
    private static final String JWT_VERSION = "            <version>0.11.5</version>\n";
    private static final String DATA_ANNOTATION = "@Data\n";
    private static final String PACKAGE = "package ";
    private static final String DELIMITER_PATH = "/";
    private static final String INDENT = "    ";
    private static final String BLOCK_CLOSE = INDENT + "}\n";
    private static final String BLOCK_CLOSE_WITH_NEWLINE = INDENT + "}\n\n";

    @Override
    public BaseResponse<GenerateProjectResponse> generateProject(GenerateProjectRequest request) throws IOException {
        if (request.getFrameworkType() != FrameworkType.SPRINGBOOT) {
            throw new BadRequestException("Framework Must Be SpringBoot");
        }

        String projectName = request.getApplicationName();
        String packageName = request.getPackageName() != null ? request.getPackageName() : projectName.toLowerCase();
        String projectPath = GENERATED_PROJECTS_DIR + DELIMITER_PATH + projectName;
        List<String> generatedFiles = new ArrayList<>();

        createProjectStructure(projectPath, packageName);

        generatePomXml(projectPath, request, generatedFiles);
        generateApplicationProperties(projectPath, request, generatedFiles);
        generateMainClass(projectPath, packageName, projectName, generatedFiles);

        if (request.getBaseEntityEnabled()) {
            generateBaseEntity(projectPath, packageName, generatedFiles);
        }

        if (request.getBaseResponseEnabled()) {
            generateBaseResponses(projectPath, packageName, generatedFiles);
        }

        if (request.getJwtAuthEnabled()) {
            generateJwtComponents(projectPath, packageName, generatedFiles);
        }

        generateSampleController(projectPath, packageName, request, generatedFiles);
        generateSampleService(projectPath, packageName, generatedFiles);
        generateSampleEntity(projectPath, packageName, request, generatedFiles);
        generateEnums(projectPath, packageName, generatedFiles);
        generateConfiguration(projectPath, packageName, generatedFiles);

        long projectSize = calculateDirectorySize(Paths.get(projectPath));

        GeneratedProject generatedProject = new GeneratedProject();
        generatedProject.setApplicationName(projectName);
        generatedProject.setFrameworkType(request.getFrameworkType());
        generatedProject.setDatabaseType(request.getDatabaseType());
        generatedProject.setJwtAuthEnabled(request.getJwtAuthEnabled());
        generatedProject.setBaseEntityEnabled(request.getBaseEntityEnabled());
        generatedProject.setBaseResponseEnabled(request.getBaseResponseEnabled());
        generatedProject.setPackageName(packageName);
        generatedProject.setProjectPath(projectPath);
        generatedProject.setFileSizeBytes(projectSize);
        generatedProject.setGroupId(request.getGroupId());
        generatedProject.setArtifactId(request.getArtifactId());
        generatedProject.setVersion(request.getVersion());
        generatedProject.setDownloadCount(0);

        GeneratedProject savedGeneratedProject = generatedProjectRepository.save(generatedProject);

        log.info("Project '{}' generated successfully and saved to database", projectName);

        return BaseResponse.success("Project Successfully Generated", mapEntityToResponse(savedGeneratedProject));
    }

//    @Override
//    public byte[] getProjectZip(String applicationName) throws IOException {
//
//        GeneratedProject project = generatedProjectRepository.getProjectByApplicationName(applicationName).orElseThrow(() -> new NotFoundException("Project Is Not Found"));
//
//        String projectName = Paths.get(project.getProjectPath())
//                .getFileName()
//                .toString();
//
//        int updated = generatedProjectRepository.incrementDownloadCount(projectName);
//
//        if (updated > 0) {
//            log.info("Download count incremented for project '{}'", projectName);
//        }
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
//            Path sourcePath = Paths.get(project.getProjectPath());
//
//            try (var stream = Files.walk(sourcePath)) {
//                stream.filter(Files::isRegularFile)
//                        .forEach(path -> {
//                            try {
//                                String entryName = sourcePath.relativize(path).toString();
//                                zos.putNextEntry(new ZipEntry(entryName));
//                                Files.copy(path, zos);
//                                zos.closeEntry();
//                            } catch (IOException e) {
//                                throw new InternalServerErrorException(e.getMessage());
//                            }
//                        });
//            }
//        }
//
//        return byteArrayOutputStream.toByteArray();
//    }

    public void getProjectZip(String applicationName, OutputStream outputStream) {

        GeneratedProject project = generatedProjectRepository
                .getProjectByApplicationName(applicationName)
                .orElseThrow(() -> new NotFoundException("Project Is Not Found"));

        generatedProjectRepository.incrementDownloadCount(applicationName);

        Path sourcePath = Paths.get(project.getProjectPath());

        try (ZipOutputStream zos = new ZipOutputStream(
                new BufferedOutputStream(outputStream))) {

            Files.walk(sourcePath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            String entryName = sourcePath.relativize(path).toString();
                            zos.putNextEntry(new ZipEntry(entryName));
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new InternalServerErrorException(e.getMessage());
                        }
                    });

        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private GenerateProjectResponse mapEntityToResponse(GeneratedProject generatedProject) {
        GenerateProjectResponse response = new GenerateProjectResponse();

        response.setId(generatedProject.getId());
        response.setProjectName(generatedProject.getApplicationName());
        response.setFrameworkType(generatedProject.getFrameworkType());
        response.setDatabaseType(generatedProject.getDatabaseType());
        response.setJwtAuthEnabled(generatedProject.getJwtAuthEnabled());
        response.setBaseEntityEnabled(generatedProject.getBaseEntityEnabled());
        response.setBaseResponseEnabled(generatedProject.getBaseResponseEnabled());
        response.setPackageName(generatedProject.getPackageName());
        response.setProjectPath(generatedProject.getProjectPath());
        response.setFileSizeBytes(generatedProject.getFileSizeBytes());
        response.setDownloadCount(generatedProject.getDownloadCount());
        response.setGroupId(generatedProject.getGroupId());
        response.setArtifactId(generatedProject.getArtifactId());
        response.setVersion(generatedProject.getVersion());
        response.setCreatedAt(generatedProject.getCreatedAt());

        return response;
    }

    private void createProjectStructure(String projectPath, String packageName) throws IOException {
        String packagePath = packageName.replace(".", DELIMITER_PATH);

        String[] directories = {
                projectPath,
                projectPath + SOURCE_MAIN_JAVA + packagePath,
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/configuration",
                projectPath + SOURCE_MAIN_JAVA + packagePath + "/controller",
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

    private long calculateDirectorySize(Path path) throws IOException {
        try (var stream = Files.walk(path)) {
            return stream
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .sum();
        }
    }

    private void generatePomXml(String projectPath, GenerateProjectRequest request, List<String> generatedFiles) throws IOException {
        String groupId = request.getGroupId() != null ? request.getGroupId() : "com.example";
        String artifactId = request.getArtifactId() != null ? request.getArtifactId() : request.getApplicationName().toLowerCase();
        String version = request.getVersion() != null ? request.getVersion() : "1.0.0";
        String javaVersion = request.getJavaVersion() != null ? request.getJavaVersion() : "17";

        StringBuilder pom = new StringBuilder();
        pom.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        pom.append("    <modelVersion>4.0.0</modelVersion>\n\n");

        pom.append("    <parent>\n");
        pom.append("        <groupId>org.springframework.boot</groupId>\n");
        pom.append("        <artifactId>spring-boot-starter-parent</artifactId>\n");
        pom.append("        <version>3.2.0</version>\n");
        pom.append("        <relativePath/>\n");
        pom.append("    </parent>\n\n");

        pom.append("    <groupId>").append(groupId).append("</groupId>\n");
        pom.append("    <artifactId>").append(artifactId).append("</artifactId>\n");
        pom.append("    <version>").append(version).append("</version>\n");
        pom.append("    <name>").append(request.getApplicationName()).append("</name>\n");
        pom.append("    <description>Generated Spring Boot Application</description>\n\n");

        pom.append("    <properties>\n");
        pom.append("        <java.version>").append(javaVersion).append("</java.version>\n");
        pom.append("    </properties>\n\n");

        pom.append("    <dependencies>\n");
        pom.append("        <!-- Spring Boot Starter Web -->\n");
        pom.append(OPEN_DEPENDENCY_SECTION);
        pom.append(GROUP_ID_DEPENDENCY);
        pom.append("            <artifactId>spring-boot-starter-web</artifactId>\n");
        pom.append(CLOSED_DEPENDENCY_SECTION);

        pom.append("        <!-- Spring Boot Starter Data JPA -->\n");
        pom.append(OPEN_DEPENDENCY_SECTION);
        pom.append(GROUP_ID_DEPENDENCY);
        pom.append("            <artifactId>spring-boot-starter-data-jpa</artifactId>\n");
        pom.append(CLOSED_DEPENDENCY_SECTION);

        pom.append("        <!-- Spring Boot Starter Validation -->\n");
        pom.append(OPEN_DEPENDENCY_SECTION);
        pom.append(GROUP_ID_DEPENDENCY);
        pom.append("            <artifactId>spring-boot-starter-validation</artifactId>\n");
        pom.append(CLOSED_DEPENDENCY_SECTION);

        pom.append("        <!-- Database Driver -->\n");
        pom.append(OPEN_DEPENDENCY_SECTION);
        String dbDependency = getDatabaseDependency(request.getDatabaseType());
        pom.append(dbDependency);
        pom.append(CLOSED_DEPENDENCY_SECTION);

        if (request.getJwtAuthEnabled()) {
            pom.append("        <!-- Spring Security -->\n");
            pom.append(OPEN_DEPENDENCY_SECTION);
            pom.append(GROUP_ID_DEPENDENCY);
            pom.append("            <artifactId>spring-boot-starter-security</artifactId>\n");
            pom.append(CLOSED_DEPENDENCY_SECTION);

            pom.append("        <!-- JWT -->\n");
            pom.append(OPEN_DEPENDENCY_SECTION);
            pom.append(GROUP_ID_JSON_WEB_TOKEN);
            pom.append("            <artifactId>jjwt-api</artifactId>\n");
            pom.append(JWT_VERSION);
            pom.append(DEPENDENCY);
            pom.append(OPEN_DEPENDENCY_SECTION);
            pom.append(GROUP_ID_JSON_WEB_TOKEN);
            pom.append("            <artifactId>jjwt-impl</artifactId>\n");
            pom.append(JWT_VERSION);
            pom.append("            <scope>runtime</scope>\n");
            pom.append(DEPENDENCY);
            pom.append(OPEN_DEPENDENCY_SECTION);
            pom.append(GROUP_ID_JSON_WEB_TOKEN);
            pom.append("            <artifactId>jjwt-jackson</artifactId>\n");
            pom.append(JWT_VERSION);
            pom.append("            <scope>runtime</scope>\n");
            pom.append(CLOSED_DEPENDENCY_SECTION);
        }

        pom.append("        <!-- Lombok -->\n");
        pom.append(OPEN_DEPENDENCY_SECTION);
        pom.append("            <groupId>org.projectlombok</groupId>\n");
        pom.append("            <artifactId>lombok</artifactId>\n");
        pom.append("            <optional>true</optional>\n");
        pom.append(CLOSED_DEPENDENCY_SECTION);

        pom.append("        <!-- Spring Boot Starter Test -->\n");
        pom.append(OPEN_DEPENDENCY_SECTION);
        pom.append(GROUP_ID_DEPENDENCY);
        pom.append("            <artifactId>spring-boot-starter-test</artifactId>\n");
        pom.append("            <scope>test</scope>\n");
        pom.append(DEPENDENCY);
        pom.append("    </dependencies>\n\n");

        pom.append("    <build>\n");
        pom.append("        <plugins>\n");
        pom.append("            <plugin>\n");
        pom.append("                <groupId>org.springframework.boot</groupId>\n");
        pom.append("                <artifactId>spring-boot-maven-plugin</artifactId>\n");
        pom.append("                <configuration>\n");
        pom.append("                    <excludes>\n");
        pom.append("                        <exclude>\n");
        pom.append("                            <groupId>org.projectlombok</groupId>\n");
        pom.append("                            <artifactId>lombok</artifactId>\n");
        pom.append("                        </exclude>\n");
        pom.append("                    </excludes>\n");
        pom.append("                </configuration>\n");
        pom.append("            </plugin>\n");
        pom.append("        </plugins>\n");
        pom.append("    </build>\n");
        pom.append("</project>\n");

        String filePath = projectPath + "/pom.xml";
        Files.writeString(Paths.get(filePath), pom.toString());
        generatedFiles.add("pom.xml");
    }

    private String getDatabaseDependency(DatabaseType databaseType) {
        return switch (databaseType) {
            case MYSQL -> """
                            <groupId>com.mysql</groupId>
                                        <artifactId>mysql-connector-j</artifactId>
                                        <scope>runtime</scope>
                    """;
            case POSTGRESQL -> """
                            <groupId>org.postgresql</groupId>
                                        <artifactId>postgresql</artifactId>
                                        <scope>runtime</scope>
                    """;
            case SQLSERVER -> """
                            <groupId>com.microsoft.sqlserver</groupId>
                                        <artifactId>mssql-jdbc</artifactId>
                                        <scope>runtime</scope>
                    """;
            case ORACLE -> """
                            <groupId>com.oracle.database.jdbc</groupId>
                                        <artifactId>ojdbc8</artifactId>
                                        <scope>runtime</scope>
                    """;
        };
    }

    private void generateApplicationProperties(String projectPath, GenerateProjectRequest request, List<String> generatedFiles) throws IOException {
        StringBuilder props = new StringBuilder();

        props.append("# Application Configuration\n");
        props.append("spring.application.name=").append(request.getApplicationName()).append("\n");
        props.append("server.port=8080\n\n");

        props.append("# Database Configuration\n");
        DatabaseType dbType = request.getDatabaseType();
        props.append("spring.datasource.url=").append(dbType.getUrlPrefix()).append("your_database_name\n");
        props.append("spring.datasource.username=your_username\n");
        props.append("spring.datasource.password=your_password\n");
        props.append("spring.datasource.driver-class-name=").append(dbType.getDriverClassName()).append("\n\n");

        props.append("# JPA Configuration\n");
        props.append("spring.jpa.hibernate.ddl-auto=update\n");
        props.append("spring.jpa.show-sql=true\n");
        props.append("spring.jpa.properties.hibernate.format_sql=true\n");

        if (dbType == DatabaseType.MYSQL) {
            props.append("spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect\n");
        } else if (dbType == DatabaseType.POSTGRESQL) {
            props.append("spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect\n");
        } else if (dbType == DatabaseType.SQLSERVER) {
            props.append("spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect\n");
        } else if (dbType == DatabaseType.ORACLE) {
            props.append("spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect\n");
        }

        if (request.getJwtAuthEnabled()) {
            props.append("\n# JWT Configuration\n");
            props.append("jwt.secret=yourSecretKeyHere123456789012345678901234567890\n");
            props.append("jwt.expiration=86400000\n");
        }

        props.append("\n# Logging Configuration\n");
        props.append("logging.level.root=INFO\n");
        props.append("logging.level.").append(request.getPackageName() != null ? request.getPackageName() : "com." + request.getApplicationName().toLowerCase()).append("=DEBUG\n");

        String filePath = projectPath + "/src/main/resources/application.properties";
        Files.writeString(Paths.get(filePath), props.toString());
        generatedFiles.add("src/main/resources/application.properties");
    }

    private void generateMainClass(String projectPath, String packageName, String projectName, List<String> generatedFiles) throws IOException {
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

        String packagePath = packageName.replace(".", DELIMITER_PATH);
        String filePath = projectPath + SOURCE_MAIN_JAVA + packagePath + DELIMITER_PATH + className + ".java";
        Files.writeString(Paths.get(filePath), mainClass.toString());
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + DELIMITER_PATH + className + ".java");
    }

    private void generateBaseEntity(String projectPath, String packageName, List<String> generatedFiles) throws IOException {
        StringBuilder baseEntity = new StringBuilder();

        baseEntity.append(PACKAGE).append(packageName).append(".models.entities.baseentity;\n\n");
        baseEntity.append("import jakarta.persistence.*;\n");
        baseEntity.append(IMPORT_LOMBOK_DATA);
        baseEntity.append("import org.hibernate.annotations.CreationTimestamp;\n");
        baseEntity.append("import org.hibernate.annotations.UpdateTimestamp;\n\n");
        baseEntity.append("import java.io.Serializable;\n");
        baseEntity.append("import java.time.LocalDateTime;\n\n");
        baseEntity.append(DATA_ANNOTATION);
        baseEntity.append("@MappedSuperclass\n");
        baseEntity.append("public abstract class BaseEntity implements Serializable {\n\n");
        baseEntity.append("    @Id\n");
        baseEntity.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
        baseEntity.append("    private Long id;\n\n");
        baseEntity.append("    @CreationTimestamp\n");
        baseEntity.append("    @Column(name = \"created_at\", nullable = false, updatable = false)\n");
        baseEntity.append("    private LocalDateTime createdAt;\n\n");
        baseEntity.append("    @UpdateTimestamp\n");
        baseEntity.append("    @Column(name = \"updated_at\")\n");
        baseEntity.append("    private LocalDateTime updatedAt;\n\n");
        baseEntity.append("    @Column(name = \"created_by\")\n");
        baseEntity.append("    private String createdBy;\n\n");
        baseEntity.append("    @Column(name = \"updated_by\")\n");
        baseEntity.append("    private String updatedBy;\n\n");
        baseEntity.append("    @Column(name = \"is_deleted\")\n");
        baseEntity.append("    private Boolean isDeleted = false;\n\n");
        baseEntity.append("    @Column(name = \"deleted_at\")\n");
        baseEntity.append("    private LocalDateTime deletedAt;\n\n");
        baseEntity.append("    @Column(name = \"deleted_by\")\n");
        baseEntity.append("    private String deletedBy;\n");
        baseEntity.append("}\n");

        String packagePath = packageName.replace(".", DELIMITER_PATH);
        String filePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/entities/baseentity/BaseEntity.java";
        Files.writeString(Paths.get(filePath), baseEntity.toString());
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/entities/baseentity/BaseEntity.java");
    }

    private void generateBaseResponses(String projectPath, String packageName, List<String> generatedFiles) throws IOException {
        String packagePath = packageName.replace(".", DELIMITER_PATH);

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
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/dto/response/baseresponse/SuccessResponse.java");

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
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/dto/response/baseresponse/ErrorResponse.java");
    }

    private void generateJwtComponents(String projectPath, String packageName, List<String> generatedFiles) throws IOException {
        String packagePath = packageName.replace(".", DELIMITER_PATH);

        StringBuilder jwtUtil = new StringBuilder();
        jwtUtil.append(PACKAGE).append(packageName).append(".security;\n\n");
        jwtUtil.append("import io.jsonwebtoken.*;\n");
        jwtUtil.append("import io.jsonwebtoken.security.Keys;\n");
        jwtUtil.append("import org.springframework.beans.factory.annotation.Value;\n");
        jwtUtil.append("import org.springframework.stereotype.Component;\n\n");
        jwtUtil.append("import javax.crypto.SecretKey;\n");
        jwtUtil.append("import java.util.Date;\n\n");
        jwtUtil.append("@Component\n");
        jwtUtil.append("public class JwtUtil {\n\n");
        jwtUtil.append("    @Value(\"${jwt.secret}\")\n");
        jwtUtil.append("    private String secret;\n\n");
        jwtUtil.append("    @Value(\"${jwt.expiration}\")\n");
        jwtUtil.append("    private Long expiration;\n\n");
        jwtUtil.append("    private SecretKey getSigningKey() {\n");
        jwtUtil.append("        return Keys.hmacShaKeyFor(secret.getBytes());\n");
        jwtUtil.append(BLOCK_CLOSE_WITH_NEWLINE);
        jwtUtil.append("    public String generateToken(String username) {\n");
        jwtUtil.append("        return Jwts.builder()\n");
        jwtUtil.append("                .setSubject(username)\n");
        jwtUtil.append("                .setIssuedAt(new Date())\n");
        jwtUtil.append("                .setExpiration(new Date(System.currentTimeMillis() + expiration))\n");
        jwtUtil.append("                .signWith(getSigningKey())\n");
        jwtUtil.append("                .compact();\n");
        jwtUtil.append(BLOCK_CLOSE_WITH_NEWLINE);
        jwtUtil.append("    public String extractUsername(String token) {\n");
        jwtUtil.append("        return extractClaims(token).getSubject();\n");
        jwtUtil.append(BLOCK_CLOSE_WITH_NEWLINE);
        jwtUtil.append("    public boolean validateToken(String token) {\n");
        jwtUtil.append("        try {\n");
        jwtUtil.append("            extractClaims(token);\n");
        jwtUtil.append("            return true;\n");
        jwtUtil.append("        } catch (JwtException | IllegalArgumentException e) {\n");
        jwtUtil.append("            return false;\n");
        jwtUtil.append("        }\n");
        jwtUtil.append(BLOCK_CLOSE_WITH_NEWLINE);
        jwtUtil.append("    private Claims extractClaims(String token) {\n");
        jwtUtil.append("        return Jwts.parserBuilder()\n");
        jwtUtil.append("                .setSigningKey(getSigningKey())\n");
        jwtUtil.append("                .build()\n");
        jwtUtil.append("                .parseClaimsJws(token)\n");
        jwtUtil.append("                .getBody();\n");
        jwtUtil.append(BLOCK_CLOSE);
        jwtUtil.append("}\n");

        String jwtUtilPath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/security/JwtUtil.java";
        Files.writeString(Paths.get(jwtUtilPath), jwtUtil.toString());
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/security/JwtUtil.java");
    }

    private void generateSampleController(String projectPath, String packageName, GenerateProjectRequest request, List<String> generatedFiles) throws IOException {
        String packagePath = packageName.replace(".", DELIMITER_PATH);
        StringBuilder controller = new StringBuilder();

        controller.append(PACKAGE).append(packageName).append(".controller;\n\n");
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

        String filePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/controller/SampleController.java";
        Files.writeString(Paths.get(filePath), controller.toString());
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/controller/SampleController.java");
    }

    private void generateSampleService(String projectPath, String packageName, List<String> generatedFiles) throws IOException {
        String packagePath = packageName.replace(".", DELIMITER_PATH);

        StringBuilder serviceInterface = new StringBuilder();
        serviceInterface.append(PACKAGE).append(packageName).append(".services.interfaces;\n\n");
        serviceInterface.append("public interface SampleService {\n");
        serviceInterface.append("    String getSampleData();\n");
        serviceInterface.append("}\n");

        String interfacePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/services/interfaces/SampleService.java";
        Files.writeString(Paths.get(interfacePath), serviceInterface.toString());
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/services/interfaces/SampleService.java");

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
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/services/implementations/SampleServiceImpl.java");
    }

    private void generateSampleEntity(String projectPath, String packageName, GenerateProjectRequest request, List<String> generatedFiles) throws IOException {
        String packagePath = packageName.replace(".", DELIMITER_PATH);
        StringBuilder entity = new StringBuilder();

        entity.append(PACKAGE).append(packageName).append(".models.entities;\n\n");
        entity.append("import jakarta.persistence.*;\n");
        if (request.getBaseEntityEnabled()) {
            entity.append(IMPORT).append(packageName).append(".models.entities.baseentity.BaseEntity;\n");
        }
        entity.append(IMPORT_ALL_ARGUMENTS_CONSTRUCTOR);
        entity.append(IMPORT_LOMBOK_DATA);
        if (request.getBaseEntityEnabled()) {
            entity.append("import lombok.EqualsAndHashCode;\n");
        }
        entity.append(IMPORT_NO_ARGUMENTS_CONSTRUCTOR);
        entity.append("@Entity\n");
        entity.append("@Table(name = \"sample_entity\")\n");
        entity.append(DATA_ANNOTATION);
        if (request.getBaseEntityEnabled()) {
            entity.append("@EqualsAndHashCode(callSuper = true)\n");
        }
        entity.append(NO_ARGUMENTS_CONSTRUCTOR);
        entity.append(ALL_ARGUMENTS_CONSTRUCTOR);
        entity.append("public class SampleEntity");
        if (request.getBaseEntityEnabled()) {
            entity.append(" extends BaseEntity");
        }
        entity.append(" {\n\n");

        if (!request.getBaseEntityEnabled()) {
            entity.append("    @Id\n");
            entity.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
            entity.append("    private Long id;\n\n");
        }

        entity.append("    @Column(name = \"name\")\n");
        entity.append("    private String name;\n\n");
        entity.append("    @Column(name = \"description\")\n");
        entity.append("    private String description;\n");
        entity.append("}\n");

        String filePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/entities/SampleEntity.java";
        Files.writeString(Paths.get(filePath), entity.toString());
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/entities/SampleEntity.java");
    }

    private void generateEnums(String projectPath, String packageName, List<String> generatedFiles) throws IOException {
        String packagePath = packageName.replace(".", DELIMITER_PATH);
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
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/enums/Status.java");
    }

    private void generateConfiguration(String projectPath, String packageName, List<String> generatedFiles) throws IOException {
        String packagePath = packageName.replace(".", DELIMITER_PATH);

        StringBuilder webConfig = new StringBuilder();
        webConfig.append(PACKAGE).append(packageName).append(".configuration;\n\n");
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

        String webConfigPath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/configuration/WebConfig.java";
        Files.writeString(Paths.get(webConfigPath), webConfig.toString());
        generatedFiles.add(ADD_GENERATED_FILES_SOURCE + packagePath + "/configuration/WebConfig.java");
    }

}
