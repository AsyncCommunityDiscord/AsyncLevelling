package fr.redstom.asynclevelling;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class GravenLevellingApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GravenLevellingApplication.class).build().run(args);
    }
}
