package com.superlawva.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        for (DotenvEntry entry : dotenv.entries()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
