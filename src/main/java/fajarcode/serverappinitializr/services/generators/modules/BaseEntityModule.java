package fajarcode.serverappinitializr.services.generators.modules;

import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class BaseEntityModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String IMPORT_LOMBOK_DATA = "import lombok.Data;\n";
    private static final String DATA_ANNOTATION = "@Data\n";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String packagePath = context.getPackagePath();

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

        String filePath = projectPath + SOURCE_MAIN_JAVA + packagePath + "/models/entities/baseentity/BaseEntity.java";
        Files.writeString(Paths.get(filePath), baseEntity.toString());
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/entities/baseentity/BaseEntity.java");
    }
}