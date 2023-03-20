package rc.personal.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rc.personal.app.service.MethodExtractorService;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private MethodExtractorService methodExtractorService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String filePath = "src/main/java/rc/personal/app/toread/";
        methodExtractorService.printExtractedMethods(methodExtractorService.extractMethodsFromDirectory(filePath));
        if (args.length == 0) {
            System.err.println("Usage: java -jar method-extractor.jar <directory>");
            return;
        }
    }
}