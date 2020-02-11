package battleships;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BattleshipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BattleshipsApplication.class, args);
    }

}
