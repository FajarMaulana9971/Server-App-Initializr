package fajarcode.serverappinitializr.services.implementations;

import fajarcode.serverappinitializr.exceptions.BadRequestException;
import fajarcode.serverappinitializr.exceptions.InternalServerErrorException;
import fajarcode.serverappinitializr.exceptions.NotFoundException;
import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.models.dto.responses.GenerateProjectResponse;
import fajarcode.serverappinitializr.models.dto.responses.base.BaseResponse;
import fajarcode.serverappinitializr.models.entities.GeneratedProject;
import fajarcode.serverappinitializr.models.enums.FrameworkType;
import fajarcode.serverappinitializr.repositories.GeneratedProjectRepository;
import fajarcode.serverappinitializr.services.interfaces.SpringBootGeneratorService;
import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.modules.ProjectStructureModule;
import fajarcode.serverappinitializr.services.generators.modules.PomXmlModule;
import fajarcode.serverappinitializr.services.generators.modules.ApplicationPropertiesModule;
import fajarcode.serverappinitializr.services.generators.modules.MainClassModule;
import fajarcode.serverappinitializr.services.generators.modules.BaseEntityModule;
import fajarcode.serverappinitializr.services.generators.modules.BaseResponsesModule;
import fajarcode.serverappinitializr.services.generators.modules.JwtModule;
import fajarcode.serverappinitializr.services.generators.modules.SampleControllerModule;
import fajarcode.serverappinitializr.services.generators.modules.SampleServiceModule;
import fajarcode.serverappinitializr.services.generators.modules.SampleEntityModule;
import fajarcode.serverappinitializr.services.generators.modules.EnumsModule;
import fajarcode.serverappinitializr.services.generators.modules.WebConfigModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringBootGeneratorServiceImplementation implements SpringBootGeneratorService {
    private final GeneratedProjectRepository generatedProjectRepository;
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

    private static final String GENERATED_PROJECTS_DIR = "generated-projects";
    private static final String DELIMITER_PATH = "/";

    @Override
    public BaseResponse<GenerateProjectResponse> generateProject(GenerateProjectRequest request) throws IOException {
        if (request.getFrameworkType() != FrameworkType.SPRINGBOOT) {
            throw new BadRequestException("Framework Must Be SpringBoot");
        }
        if (generatedProjectRepository.getProjectByApplicationName(request.getApplicationName()).isPresent()) {
            throw new BadRequestException("Project With The Same Name Already Exists");
        }

        String projectName = request.getApplicationName();
        String packageName = request.getPackageName() != null ? request.getPackageName() : projectName.toLowerCase();
        String projectPath = GENERATED_PROJECTS_DIR + DELIMITER_PATH + projectName;
        String packagePath = packageName.replace(".", DELIMITER_PATH);
        List<String> generatedFiles = new ArrayList<>();

        GenerationContext context = GenerationContext.builder()
            .request(request)
            .projectName(projectName)
            .packageName(packageName)
            .projectPath(projectPath)
            .packagePath(packagePath)
            .generatedFiles(generatedFiles)
            .build();

        try {
            projectStructureModule.generate(context);
            pomXmlModule.generate(context);
            applicationPropertiesModule.generate(context);
            mainClassModule.generate(context);
            if (request.getBaseEntityEnabled()) {
                baseEntityModule.generate(context);
            }
            if (request.getBaseResponseEnabled()) {
                baseResponsesModule.generate(context);
            }
            if (request.getJwtAuthEnabled()) {
                jwtModule.generate(context);
            }
            sampleControllerModule.generate(context);
            sampleServiceModule.generate(context);
            sampleEntityModule.generate(context);
            enumsModule.generate(context);
            webConfigModule.generate(context);
        } catch (Exception e) {
            cleanupProjectDirectory(projectPath);
            throw e;
        }

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

    @Override
    @Transactional
    public void getProjectZip(String applicationName, OutputStream outputStream) {

        GeneratedProject project = generatedProjectRepository
                .getProjectByApplicationName(applicationName)
                .orElseThrow(() -> new NotFoundException("Project Is Not Found"));

        generatedProjectRepository.incrementDownloadCount(applicationName);

        Path sourcePath = Paths.get(project.getProjectPath());

        try (ZipOutputStream zos = new ZipOutputStream(
                new BufferedOutputStream(outputStream));
             var fileStream = Files.walk(sourcePath)) {

            fileStream
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

    private void cleanupProjectDirectory(String projectPath) {
        try {
            Path dir = Paths.get(projectPath);
            if (Files.exists(dir)) {
                try (var dirStream = Files.walk(dir)) {
                    dirStream
                            .sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException ex) {
                                    log.warn("Failed to clean up file: {}", path, ex);
                                }
                            });
                }
            }
        } catch (IOException ex) {
            log.warn("Failed to clean up project directory: {}", projectPath, ex);
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
}
