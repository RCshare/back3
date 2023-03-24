package rc.personal.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rc.personal.app.DTO.MethodNodeDTO;
import rc.personal.app.mapper.MethodMapper;
import rc.personal.app.model.Method;
import rc.personal.app.repository.MethodRepository;

import java.io.IOException;
import java.util.List;

@Service
public class MethodService {

    @Autowired
    private MethodRepository repository; // assuming you have defined a MongoDB repository for methods

    @Autowired
    private MethodExtractorService methodExtractorService;


    public void updateMethodsFromDirectory() throws IOException {
        List<Method> methods = methodExtractorService.extractMethodsFromDirectory();
        repository.deleteAll();
        repository.saveAll(methods);
    }

    public List<MethodNodeDTO> getAllMethods() {
        List<Method> methods = repository.findAll();
        return MethodMapper.getMethodsByFileName(methods);
    }
}
