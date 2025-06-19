package com.comeon.gamecounter.loadtest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IntegrationTestApplication {
    public static void main(String[] args) {
        String gatewayHost = "localhost";
        int gatewayPort = 8080;
        String gatewayBasePath = "/comeon/gateway/api/v1";

        final int clientCount = 10;            // Number of simulated clients
        final int actionsPerClient = 100;      // Actions per client (e.g., hits)

        ExecutorService executorService = Executors.newFixedThreadPool(clientCount);
        List<ClientSimulator> simulators = new ArrayList<>();

        for (int i = 0; i < clientCount; i++) {
            ClientSimulator clientSimulator = new ClientSimulator(gatewayHost, gatewayPort, gatewayBasePath, actionsPerClient);
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

        System.out.println("Integration test complete.");
    }
}
