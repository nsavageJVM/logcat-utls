package com.fishbeans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Created by ubu on 8/23/16.
 */
@Lazy
@Configuration
@EnableAutoConfiguration
@ComponentScan({  "com.fishbeans.stream", "com.fishbeans.service", "com.fishbeans.producer"})
public class CommandLineInteface {

    public static void main(String[] args) {
        SpringApplication.run(CommandLineInteface.class, args);
    }
}
