package rc.personal.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rc.personal.app.controller.MethodController;
import rc.personal.app.model.Method;
import rc.personal.app.repository.MethodRepository;
import rc.personal.app.service.MethodExtractorService;
import rc.personal.app.service.MethodService;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private MethodService methodService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        methodService.updateMethodsFromDirectory();

       if (args.length == 0) {
            System.err.println("Usage: java -jar method-extractor.jar <directory>");
            return;
        }


    }
}