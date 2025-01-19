package mindpath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MindPathApplication {

    public static void main(String[] args) {
        SpringApplication.run(MindPathApplication.class, args);
    }

}
