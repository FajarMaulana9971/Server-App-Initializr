package test.controllers;

import test.services.interfaces.SampleService;
import test.models.dto.response.baseresponse.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sample")
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @GetMapping
    public ResponseEntity<?> getSample() {
        return ResponseEntity.ok(SuccessResponse.of("Sample endpoint", "Hello from Test!"));
 }
}
