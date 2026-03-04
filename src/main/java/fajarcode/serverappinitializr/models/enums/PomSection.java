package fajarcode.serverappinitializr.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PomSection {

    HEADER("""
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
            """),

    PARENT("""
            
                <parent>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-parent</artifactId>
                    <version>3.2.0</version>
                    <relativePath/>
                </parent>
            """),

    DEPENDENCIES_OPEN("""
            
                <dependencies>
            """),

    DEPENDENCIES_CLOSE("""
                </dependencies>
            """),

    PROJECT_CLOSE("""
            </project>
            """);

    private final String template;

    public static String projectInfo(String groupId, String artifactId, String version, String name) {
        return """
                
                    <groupId>%s</groupId>
                    <artifactId>%s</artifactId>
                    <version>%s</version>
                    <name>%s</name>
                    <description>Generated Spring Boot Application</description>
                """.formatted(groupId, artifactId, version, name);
    }

    public static String properties(String javaVersion) {
        return """
                
                    <properties>
                        <java.version>%s</java.version>
                    </properties>
                """.formatted(javaVersion);
    }

    public static String buildSection() {
        StringBuilder build = new StringBuilder();
        build.append("\n    <build>\n");
        build.append("        <plugins>\n");
        for (PomPlugin plugin : PomPlugin.values()) {
            build.append(plugin.getTemplate());
        }
        build.append("        </plugins>\n");
        build.append("    </build>\n");
        return build.toString();
    }
}
