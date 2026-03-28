package test.services.implementations;

import test.services.interfaces.SampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {

    @Override
    public String getSampleData() {
        return "Sample data from service";
    }
}
