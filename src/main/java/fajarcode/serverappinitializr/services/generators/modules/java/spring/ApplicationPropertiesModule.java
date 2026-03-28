package fajarcode.serverappinitializr.services.generators.modules.java.spring;

import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.models.enums.DatabaseType;
import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ApplicationPropertiesModule implements ProjectGenerationModule {

    @Override
    public void generate(GenerationContext context) throws IOException {
        GenerateProjectRequest request = context.getRequest();
        String projectPath = context.getProjectPath();

        StringBuilder props = new StringBuilder();

        props.append("# Application Configuration\n");
        props.append("spring.application.name=").append(request.getApplicationName()).append("\n");
        props.append("server.port=8080\n\n");

        props.append("# Database Configuration\n");
        DatabaseType dbType = request.getDatabaseType();
        props.append("spring.datasource.url=").append(dbType.getUrlPrefix()).append("your_database_name\n");
        props.append("spring.datasource.username=your_username\n");
        props.append("spring.datasource.password=\n");
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

        if (Boolean.TRUE.equals(request.getJwtAuthEnabled())) {
            props.append("\n# JWT Configuration\n");
            props.append("jwt.secret=yourSecretKeyHere123456789012345678901234567890\n");
            props.append("jwt.expiration=86400000\n");
        }

        props.append("\n# Logging Configuration\n");
        props.append("logging.level.root=INFO\n");
        props.append("logging.level.")
                .append(request.getPackageName() != null ? request.getPackageName() : "com." + request.getApplicationName().toLowerCase())
                .append("=DEBUG\n");

        String filePath = projectPath + "/src/main/resources/application.properties";
        Files.writeString(Paths.get(filePath), props.toString());
        context.getGeneratedFiles().add("src/main/resources/application.properties");
    }
}