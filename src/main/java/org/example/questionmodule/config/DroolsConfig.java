package org.example.questionmodule.config;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Configuration
public class DroolsConfig {
    private static final String RULES_PATH = "rules/";

    private final KieServices kieServices = KieServices.Factory.get();

//    @Bean
    public KieContainer getKieContainer(String ruleFileName) throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Đọc tất cả file DRL trong thư mục rules
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] ruleFiles = resolver.getResources("classpath:" + RULES_PATH + ruleFileName);

        String content = new String(ruleFiles[0].getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        kieFileSystem.write("src/main/resources/" + RULES_PATH + ruleFileName, content);


        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }

}
