package com.examples;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MagicConfig {
    @Bean
    public MagicBuilderServiceImpl getMagicService() {

        return new MagicBuilderServiceImpl();
    }

}
