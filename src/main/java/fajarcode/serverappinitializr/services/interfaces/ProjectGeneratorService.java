package fajarcode.serverappinitializr.services.interfaces;

import fajarcode.serverappinitializr.models.dto.requests.GenerateProjectRequest;
import fajarcode.serverappinitializr.models.dto.responses.GenerateProjectResponse;
import fajarcode.serverappinitializr.models.dto.responses.base.BaseResponse;

import java.io.IOException;
import java.io.OutputStream;

public interface ProjectGeneratorService {
    BaseResponse<GenerateProjectResponse> generateSpringProject(GenerateProjectRequest request) throws IOException;

    void getProjectZip(String applicationName, OutputStream outputStream);
}
