package com.comeon.gamecounter.core;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class CoreServerStarter {
    public static ConfigurableApplicationContext start(Map<String, String> props) {
        SpringApplication app = new SpringApplication(CoreServerApplication.class);
        String[] args = props.entrySet().stream()
                .map(entry -> "--" + entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
        return app.run(args);
    }
}
