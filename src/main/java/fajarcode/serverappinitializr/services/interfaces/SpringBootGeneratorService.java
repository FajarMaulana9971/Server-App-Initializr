package fajarcode.serverappinitializr.services.interfaces;

import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.models.dto.responses.GenerateProjectResponse;
import fajarcode.serverappinitializr.models.dto.responses.base.BaseResponse;

import java.io.IOException;
import java.io.OutputStream;

public interface SpringBootGeneratorService {
    BaseResponse<GenerateProjectResponse> generateProject(GenerateProjectRequest request) throws IOException;

//    byte[] getProjectZip(String applicationName) throws IOException;

    void getProjectZip(String applicationName, OutputStream outputStream);
}
