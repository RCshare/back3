package rc.personal.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rc.personal.app.model.Method;
import rc.personal.app.repository.MethodRepository;
import rc.personal.app.service.MethodExtractorService;
@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private MethodExtractorService methodExtractorService;

    @Autowired
    private MethodRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        repository.deleteAll();

        // save a couple of customers
        repository.save(new Method("method", "method code","method.java"));

        methodExtractorService.printExtractedMethods(methodExtractorService.extractMethodsFromDirectory());
        if (args.length == 0) {
            System.err.println("Usage: java -jar method-extractor.jar <directory>");
            return;
        }


    }
}