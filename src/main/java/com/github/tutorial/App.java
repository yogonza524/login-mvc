package com.github.tutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = {
        "com.github.tutorial"
})
@EnableJpaRepositories("com.github.tutorial.Persistence.dao")
@EntityScan(basePackages = {"com.github.tutorial.Persistence.model"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
