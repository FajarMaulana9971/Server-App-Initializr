package fajarcode.serverappinitializr.controllers.generate;

import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.models.dto.responses.GenerateProjectResponse;
import fajarcode.serverappinitializr.models.dto.responses.base.BaseResponse;
import fajarcode.serverappinitializr.services.interfaces.SpringBootGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("spring-boot/generator")
public class SpringBootGeneratorController {

    private final SpringBootGeneratorService springBootGeneratorService;

    @PostMapping()
    public ResponseEntity<BaseResponse<GenerateProjectResponse>> generateSpringBootProject(@Valid @RequestBody GenerateProjectRequest request) throws IOException {
        BaseResponse<GenerateProjectResponse> responseBody = springBootGeneratorService.generateProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

//    @GetMapping("/download")
//    public ResponseEntity<ByteArrayResource> downloadProject(@RequestBody String projectName) throws IOException {
//        byte[] zipData = springBootGeneratorService.getProjectZip(projectName);
//        ByteArrayResource resource = new ByteArrayResource(zipData);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + projectName + ".zip").contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(zipData.length).body(resource);
//    }

    @GetMapping("/download")
    public void download(@RequestBody String applicationName, HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + applicationName + ".zip"
        );
        springBootGeneratorService.getProjectZip(applicationName, response.getOutputStream());
    }


}
