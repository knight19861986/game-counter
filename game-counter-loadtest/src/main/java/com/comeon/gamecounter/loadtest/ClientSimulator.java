package com.comeon.gamecounter.loadtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ClientSimulator implements Runnable {
    private final String gatewayHost;
    private final int gatewayPort;
    private final String gatewayBasePath;
    private final int actionCount;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    private String sessionId;
    private long playerId;

    private int successCount = 0;
    private int failureCount = 0;

    private static final String[] GAME_CODES = {"SANGUO2", "HERO3", "RICH4", "FIFA2000"};

    public ClientSimulator(String gatewayHost, int gatewayPort, String gatewayBasePath, int actionCount) {
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
        this.gatewayBasePath = gatewayBasePath;
        this.actionCount = actionCount;
    }

    private boolean signupAndLogin() throws IOException, InterruptedException {
        // Sign-up
        String signupUrl = String.format("http://%s:%d%s/signup?password=%s",
                gatewayHost, gatewayPort, gatewayBasePath,
                URLEncoder.encode("password123", StandardCharsets.UTF_8));

        HttpRequest signupRequest = HttpRequest.newBuilder()
                .uri(URI.create(signupUrl))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> signupResponse = httpClient.send(signupRequest, HttpResponse.BodyHandlers.ofString());

        if (signupResponse.statusCode() != 200)
            return false;

        JsonNode signupJson = objectMapper.readTree(signupResponse.body());
        playerId = signupJson.get("playerId").asLong();

        // Login
        String loginUrl = String.format("http://%s:%d%s/login", gatewayHost, gatewayPort, gatewayBasePath);
        String loginBody = String.format("{\"playerId\": %d, \"password\": \"%s\"}", playerId, "password123");

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .build();

        HttpResponse<String> loginResponse = httpClient.send(loginRequest, HttpResponse.BodyHandlers.ofString());

        if (loginResponse.statusCode() != 200)
            return false;

        JsonNode loginJson = objectMapper.readTree(loginResponse.body());
        sessionId = loginJson.get("sessionId").asText();

        return true;
    }

    private boolean hitGame(String gameCode) throws IOException, InterruptedException {
        String hitUrl = String.format("http://%s:%d%s/hit?sessionId=%s&gameCode=%s",
                gatewayHost, gatewayPort, gatewayBasePath,
                URLEncoder.encode(sessionId, StandardCharsets.UTF_8),
                URLEncoder.encode(gameCode, StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(hitUrl))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 202;
    }

    private void logout() throws IOException, InterruptedException {
        String logoutUrl = String.format("http://%s:%d%s/logout/%s",
                gatewayHost, gatewayPort, gatewayBasePath,
                URLEncoder.encode(sessionId, StandardCharsets.UTF_8));

        HttpRequest logoutRequest = HttpRequest.newBuilder()
                .uri(URI.create(logoutUrl))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        httpClient.send(logoutRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public void run() {
        try {
            if (!signupAndLogin()) {
                failureCount++;
                return;
            }

            for (int i = 0; i < actionCount; i++) {
                String gameCode = getRandomGameCode();
                if (hitGame(gameCode)) {
                    successCount++;
                } else {
                    failureCount++;
                }
            }

            Thread.sleep(1000);
            logout();

        } catch (Exception e) {
            failureCount++;
            System.err.println("Client encountered error: " + e.getMessage());
        } finally {
            System.out.printf(
                    "Client %d finished. Success: %d, Failure: %d%n",
                    playerId, successCount, failureCount
            );
        }
    }

    private String getRandomGameCode() {
        return GAME_CODES[random.nextInt(GAME_CODES.length)];
    }
}
