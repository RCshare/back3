package rc.personal.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import rc.personal.app.service.MethodService;

@SpringBootApplication
//@EnableDiscoveryClient
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