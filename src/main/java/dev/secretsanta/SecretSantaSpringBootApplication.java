package dev.secretsanta;

import dev.secretsanta.config.SecretSantaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecretSantaProperties.class)
public class SecretSantaSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecretSantaSpringBootApplication.class, args);
    }
}
