package fajarcode.serverappinitializr.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseType {
    MYSQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/", "mysql-connector-j"),
    POSTGRESQL("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/", "postgresql"),
    SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://localhost:1433;databaseName=", "mssql-jdbc"),
    ORACLE("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521:", "ojdbc8");

    private final String driverClassName;
    private final String urlPrefix;
    private final String dependencyArtifact;
}
