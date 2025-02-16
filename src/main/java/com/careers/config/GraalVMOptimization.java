package com.careers.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;

@Configuration
public class GraalVMOptimization {

    @Bean
    public RuntimeHints runtimeHints() {
        RuntimeHints hints = new RuntimeHints();
        hints.resources().registerPattern("db/changelog/*.xml");
        hints.reflection().registerType(User.class,
            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
            MemberCategory.DECLARED_FIELDS
        );
        return hints;
    }
}