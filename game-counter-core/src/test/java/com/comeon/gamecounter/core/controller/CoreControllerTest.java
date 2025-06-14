package com.comeon.gamecounter.core.controller;

import com.comeon.gamecounter.core.CoreServerApplication;
import com.comeon.gamecounter.core.DAO.GameRepository;
import com.comeon.gamecounter.core.DAO.PlayerRepository;
import com.comeon.gamecounter.core.DTO.request.LoginRequest;
import com.comeon.gamecounter.core.model.Game;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(
        classes = CoreServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestDatabase
public class CoreControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/comeon/core/api/v1";
    }

    @AfterEach
    void cleanup() {
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void testLogout() throws JsonProcessingException {
        String password = "123";
        String sessionId = signupAndLogin(password);

        // Logout (assuming logout uses sessionId in header)
        HttpHeaders logoutHeaders = new HttpHeaders();
        HttpEntity<Void> logoutEntity = new HttpEntity<>(null, logoutHeaders);
        ResponseEntity<Void> logoutResponse = restTemplate.postForEntity(
                baseUrl + "/logout?sessionId=" + sessionId,
                logoutEntity,
                Void.class
        );

        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testHit() throws InterruptedException, JsonProcessingException {
        String password = "123";
        String sessionId = signupAndLogin(password);
        String gameCode = "Game001";
        insertTestGame(gameCode);

        // Hit the game
        String hitUrl = baseUrl + "/hit?sessionId=" + sessionId + "&gameCode=" + gameCode;
        ResponseEntity<String> hitResponse = restTemplate.postForEntity(hitUrl, null, String.class);
        assertThat(hitResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(hitResponse.getBody()).isEqualTo("Hit request queued.");

        // Wait for hit to be processed (adjust time if needed)
        Thread.sleep(300);

        // Check game hits via admin endpoint
        String getGamesUrl = baseUrl + "/admin/games";
        ResponseEntity<List<Game>> gamesResponse = restTemplate.exchange(
                getGamesUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Game>>() {}
        );
        assertThat(gamesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Game> games = gamesResponse.getBody();
        assertThat(games).isNotNull();

        Optional<Game> createdGame = games.stream()
                .filter(g -> g.getGameCode().equals(gameCode))
                .findFirst();

        assertThat(createdGame).isPresent();
        assertThat(createdGame.get().getHits()).isEqualTo(1);
    }


    private String signupAndLogin(String password) throws JsonProcessingException {
        // Signup
        ResponseEntity<Map> signupResponse = restTemplate.postForEntity(
                baseUrl + "/signup?password=" + password,
                null,
                Map.class
        );
        assertThat(signupResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long playerId = Long.valueOf(signupResponse.getBody().get("playerId").toString());

        // Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPlayerId(playerId);
        loginRequest.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                baseUrl + "/login",
                loginEntity,
                Map.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return loginResponse.getBody().get("sessionId").toString();
    }

    private Game insertTestGame(String gameCode) {
        Game game = new Game();
        game.setGameCode(gameCode);
        game.setHits(0);
        game.setCreateTime(System.currentTimeMillis());
        game.setUpdateTime(System.currentTimeMillis());
        return gameRepository.save(game);
    }
}
