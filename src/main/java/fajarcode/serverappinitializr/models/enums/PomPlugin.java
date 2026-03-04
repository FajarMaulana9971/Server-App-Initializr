package fajarcode.serverappinitializr.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PomPlugin {

    MAVEN_COMPILER("""
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-compiler-plugin</artifactId>
                            <configuration>
                                <annotationProcessorPaths>
                                    <path>
                                        <groupId>org.projectlombok</groupId>
                                        <artifactId>lombok</artifactId>
                                        <version>1.18.42</version>
                                    </path>
                                </annotationProcessorPaths>
                            </configuration>
                        </plugin>
            """),

    SPRING_BOOT_MAVEN("""
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                            <configuration>
                                <excludes>
                                    <exclude>
                                        <groupId>org.projectlombok</groupId>
                                        <artifactId>lombok</artifactId>
                                    </exclude>
                                </excludes>
                            </configuration>
                        </plugin>
            """);

    private final String template;
}
