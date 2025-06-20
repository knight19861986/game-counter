package com.comeon.gamecounter.loadtest;

import com.comeon.gamecounter.core.CoreServerStarter;
import com.comeon.gamecounter.gateway.GatewayServerStarter;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadTestApplication {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (InputStream in = LoadTestApplication
                .class
                .getClassLoader()
                .getResourceAsStream("loadtest.properties")) {
            props.load(in);
        }

        final int clientCount = Integer.parseInt(props.getProperty("client.count"));
        final int actionsPerClient = Integer.parseInt(props.getProperty("actions.per.client"));

        final String coreHost = props.getProperty("core.host");
        final int corePort = Integer.parseInt(props.getProperty("core.port"));
        final String coreBasePath = props.getProperty("core.base.path");
        final String gatewayHost = props.getProperty("gateway.host");
        final int gatewayPort = Integer.parseInt(props.getProperty("gateway.port"));
        final String gatewayBasePath = props.getProperty("gateway.base.path");
        final String[] gameCodes = props.getProperty("game.codes").split(",");

        System.out.println("Starting core server...");
        Map<String, String> argsOfRunCore = Map.of(
                "server.port", String.valueOf(corePort),
                "spring.datasource.url", "jdbc:h2:mem:testdb",
                "spring.jpa.show-sql", "false"
        );
        ConfigurableApplicationContext coreContext = CoreServerStarter.start(argsOfRunCore);

        String port = coreContext.getEnvironment().getProperty("server.port");
        System.out.println("Core server running on port: " + port);

        System.out.println("Starting gateway server...");
        GatewayServerStarter.start(gatewayPort, coreHost, corePort, coreBasePath, false);

        ExecutorService executorService = Executors.newFixedThreadPool(clientCount);
        List<ClientSimulator> simulators = new ArrayList<>();

        for (int i = 0; i < clientCount; i++) {
            ClientSimulator clientSimulator = new ClientSimulator(
                    gatewayHost,
                    gatewayPort,
                    gatewayBasePath,
                    actionsPerClient,
                    generatePassword(i),
                    gameCodes);
            simulators.add(clientSimulator);
            executorService.submit(clientSimulator);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
                System.err.println("Timeout reached. Some tasks did not complete.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Integration test complete. Shutting down servers...");
        coreContext.close();
        GatewayServerStarter.stop();
    }

    private static String generatePassword(int i) {
        return "password" + i + i + i;
    }
}
