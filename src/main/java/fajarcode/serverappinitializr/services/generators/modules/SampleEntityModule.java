package fajarcode.serverappinitializr.services.generators.modules;

import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class SampleEntityModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String IMPORT = "import ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String ALL_ARGUMENTS_CONSTRUCTOR = "@AllArgsConstructor\n";
    private static final String NO_ARGUMENTS_CONSTRUCTOR = "@NoArgsConstructor\n";
    private static final String IMPORT_NO_ARGUMENTS_CONSTRUCTOR = "import lombok.NoArgsConstructor;\n\n";
    private static final String IMPORT_ALL_ARGUMENTS_CONSTRUCTOR = "import lombok.AllArgsConstructor;\n";
    private static final String IMPORT_LOMBOK_DATA = "import lombok.Data;\n";
    private static final String DATA_ANNOTATION = "@Data\n";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String packagePath = context.getPackagePath();
        GenerateProjectRequest request = context.getRequest();

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
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/models/entities/SampleEntity.java");
    }
}