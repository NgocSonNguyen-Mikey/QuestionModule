package org.example.questionmodule;

import org.example.questionmodule.config.HintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ImportRuntimeHints(HintsRegistrar.class)
@EnableAsync
public class QuestionModuleApplication {

    public static void main(String[] args) {
        System.out.println("✅ Total Memory (MB): " + Runtime.getRuntime().maxMemory() / (1024 * 1024));
        SpringApplication.run(QuestionModuleApplication.class, args);
    }

}
