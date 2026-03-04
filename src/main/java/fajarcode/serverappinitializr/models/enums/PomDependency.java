package fajarcode.serverappinitializr.models.enums;

import lombok.Getter;

@Getter
public enum PomDependency {

    SPRING_WEB(
            "org.springframework.boot", "spring-boot-starter-web",
            null, null, "Spring Boot Starter Web", false
    ),
    SPRING_DATA_JPA(
            "org.springframework.boot", "spring-boot-starter-data-jpa",
            null, null, "Spring Boot Starter Data JPA", false
    ),
    SPRING_VALIDATION(
            "org.springframework.boot", "spring-boot-starter-validation",
            null, null, "Spring Boot Starter Validation", false
    ),

    // Database drivers
    MYSQL_DRIVER(
            "com.mysql", "mysql-connector-j",
            null, "runtime", "MySQL Driver", false
    ),
    POSTGRESQL_DRIVER(
            "org.postgresql", "postgresql",
            null, "runtime", "PostgreSQL Driver", false
    ),
    SQLSERVER_DRIVER(
            "com.microsoft.sqlserver", "mssql-jdbc",
            null, "runtime", "SQL Server Driver", false
    ),
    ORACLE_DRIVER(
            "com.oracle.database.jdbc", "ojdbc8",
            null, "runtime", "Oracle Driver", false
    ),

    // Security & JWT
    SPRING_SECURITY(
            "org.springframework.boot", "spring-boot-starter-security",
            null, null, "Spring Security", true
    ),
    JJWT_API(
            "io.jsonwebtoken", "jjwt-api",
            "0.11.5", null, "JWT API", true
    ),
    JJWT_IMPL(
            "io.jsonwebtoken", "jjwt-impl",
            "0.11.5", "runtime", "JWT Implementation", true
    ),
    JJWT_JACKSON(
            "io.jsonwebtoken", "jjwt-jackson",
            "0.11.5", "runtime", "JWT Jackson", true
    ),

    // Utility
    LOMBOK(
            "org.projectlombok", "lombok",
            null, null, "Lombok", false
    ) {
        @Override
        public String toXml() {
            return """
                        <!-- %s -->
                        <dependency>
                            <groupId>%s</groupId>
                            <artifactId>%s</artifactId>
                            <optional>true</optional>
                        </dependency>
                    """.formatted(getComment(), getGroupId(), getArtifactId());
        }
    },

    // Test
    SPRING_BOOT_TEST(
            "org.springframework.boot", "spring-boot-starter-test",
            null, "test", "Spring Boot Starter Test", false
    );

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String scope;
    private final String comment;
    private final boolean jwtOnly;

    PomDependency(String groupId, String artifactId, String version, String scope, String comment, boolean jwtOnly) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
        this.comment = comment;
        this.jwtOnly = jwtOnly;
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("        <!-- ").append(comment).append(" -->\n");
        sb.append("        <dependency>\n");
        sb.append("            <groupId>").append(groupId).append("</groupId>\n");
        sb.append("            <artifactId>").append(artifactId).append("</artifactId>\n");
        if (version != null) {
            sb.append("            <version>").append(version).append("</version>\n");
        }
        if (scope != null) {
            sb.append("            <scope>").append(scope).append("</scope>\n");
        }
        sb.append("        </dependency>\n");
        return sb.toString();
    }

    public static PomDependency driverFor(DatabaseType databaseType) {
        return switch (databaseType) {
            case MYSQL -> MYSQL_DRIVER;
            case POSTGRESQL -> POSTGRESQL_DRIVER;
            case SQLSERVER -> SQLSERVER_DRIVER;
            case ORACLE -> ORACLE_DRIVER;
        };
    }
}
