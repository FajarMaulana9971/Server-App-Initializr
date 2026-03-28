package fajarcode.serverappinitializr.services.generators.modules.java.spring;

import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.models.enums.PomDependency;
import fajarcode.serverappinitializr.models.enums.PomSection;
import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class PomXmlModule implements ProjectGenerationModule {
    @Override
    public void generate(GenerationContext context) throws IOException {
        GenerateProjectRequest request = context.getRequest();
        String projectPath = context.getProjectPath();

        String groupId = request.getGroupId() != null ? request.getGroupId() : "com.example";
        String artifactId = request.getArtifactId() != null ? request.getArtifactId() : request.getApplicationName().toLowerCase();
        String version = request.getVersion() != null ? request.getVersion() : "1.0.0";
        String javaVersion = request.getJavaVersion() != null ? request.getJavaVersion() : "17";

        StringBuilder pom = new StringBuilder();

        pom.append(PomSection.HEADER.getTemplate());
        pom.append(PomSection.PARENT.getTemplate());
        pom.append(PomSection.projectInfo(groupId, artifactId, version, request.getApplicationName()));
        pom.append(PomSection.properties(javaVersion));

        pom.append(PomSection.DEPENDENCIES_OPEN.getTemplate());

        for (PomDependency dep : PomDependency.values()) {
            if ((!dep.isJwtOnly() || Boolean.TRUE.equals(request.getJwtAuthEnabled()))
                    && !isDriverDependency(dep)) {
                pom.append(dep.toXml());
            }
        }

        pom.append(PomDependency.driverFor(request.getDatabaseType()).toXml());
        pom.append(PomSection.DEPENDENCIES_CLOSE.getTemplate());
        pom.append(PomSection.buildSection());
        pom.append(PomSection.PROJECT_CLOSE.getTemplate());

        String filePath = projectPath + "/pom.xml";
        Files.writeString(Paths.get(filePath), pom.toString());
        context.getGeneratedFiles().add("pom.xml");
    }

    private boolean isDriverDependency(PomDependency dep) {
        return dep == PomDependency.MYSQL_DRIVER
                || dep == PomDependency.POSTGRESQL_DRIVER
                || dep == PomDependency.SQLSERVER_DRIVER
                || dep == PomDependency.ORACLE_DRIVER;
    }
}