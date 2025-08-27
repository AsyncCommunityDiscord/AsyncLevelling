package fr.redstom.asynclevelling;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = "fr.redstom.asynclevelling.config")
public class AsyncLevellingApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AsyncLevellingApplication.class).build().run(args);
    }
}
