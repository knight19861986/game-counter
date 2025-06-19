package com.comeon.gamecounter.core;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class CoreServerStarter {
    public static ConfigurableApplicationContext start(int port) {
        SpringApplication app = new SpringApplication(CoreServerApplication.class);
        app.setDefaultProperties(Map.of("server.port", port));
        return app.run();
    }
}
