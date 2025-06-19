package com.comeon.gamecounter.loadtest;

import com.comeon.gamecounter.core.CoreServerStarter;
import com.comeon.gamecounter.gateway.GatewayServerStarter;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadTestApplication {
    public static void main(String[] args) throws Exception {
        final int clientCount = 10;            // Number of simulated clients
        final int actionsPerClient = 100;      // Actions per client (e.g., hits)

        final int corePort = 9091;
        final int gatewayPort = 8081;
        String gatewayHost = "localhost";
        String gatewayBasePath = "/comeon/gateway/api/v1";

        System.out.println("Starting core server...");
        ConfigurableApplicationContext coreContext = CoreServerStarter.start(corePort);

        System.out.println("Starting gateway server...");
        GatewayServerStarter.start(gatewayPort, false);

        ExecutorService executorService = Executors.newFixedThreadPool(clientCount);
        List<ClientSimulator> simulators = new ArrayList<>();

        for (int i = 0; i < clientCount; i++) {
            ClientSimulator clientSimulator = new ClientSimulator(
                    gatewayHost,
                    gatewayPort,
                    gatewayBasePath,
                    actionsPerClient,
                    generatePassword(i));
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
