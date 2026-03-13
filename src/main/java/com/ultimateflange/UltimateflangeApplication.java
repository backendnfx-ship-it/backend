package com.ultimateflange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UltimateflangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(UltimateflangeApplication.class, args);
    }

}
