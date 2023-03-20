package rc.personal.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rc.personal.extractor.service.MethodExtractorService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private MethodExtractorService methodExtractorService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java -jar method-extractor.jar <directory>");
            return;
        }
        //String directoryPath = args[0];
        //methodExtractorService.extractMethodsFromDirectory(directoryPath);
    }
}