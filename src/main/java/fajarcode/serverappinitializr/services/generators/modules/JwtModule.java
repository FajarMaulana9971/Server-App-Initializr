package fajarcode.serverappinitializr.services.generators.modules;

import fajarcode.serverappinitializr.services.generators.GenerationContext;
import fajarcode.serverappinitializr.services.generators.ProjectGenerationModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class JwtModule implements ProjectGenerationModule {
    private static final String PACKAGE = "package ";
    private static final String SOURCE_MAIN_JAVA = "/src/main/java/";
    private static final String ADD_GENERATED_FILES_SOURCE = "src/main/java/";
    private static final String INDENT = " ";
    private static final String BLOCK_CLOSE = INDENT + "}\n";
    private static final String BLOCK_CLOSE_WITH_NEWLINE = INDENT + "}\n\n";

    @Override
    public void generate(GenerationContext context) throws IOException {
        String projectPath = context.getProjectPath();
        String packageName = context.getPackageName();
        String packagePath = context.getPackagePath();

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
        context.getGeneratedFiles().add(ADD_GENERATED_FILES_SOURCE + packagePath + "/security/JwtUtil.java");
    }
}