package com.comeon.gamecounter.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GatewayServerApplication {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (InputStream in = GatewayServerApplication
                .class
                .getClassLoader()
                .getResourceAsStream("gateway.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load gateway config", e);
        }

        String coreHost = props.getProperty("core.host", "localhost");
        String corePort = props.getProperty("core.port", "9090");
        String coreBasePath = props.getProperty("core.base-path", "/comeon/core/api/v1");
        GatewayServerStarter.start(8080, coreHost, Integer.parseInt(corePort), coreBasePath, true);
    }
}
