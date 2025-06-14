package com.comeon.gamecounter.core.controller;

import com.comeon.gamecounter.core.DTO.request.LoginRequest;
import com.comeon.gamecounter.core.model.Game;
import com.comeon.gamecounter.core.model.Player;
import com.comeon.gamecounter.core.service.GameHitQueueService;
import com.comeon.gamecounter.core.service.GameService;
import com.comeon.gamecounter.core.service.PlayerService;
import com.comeon.gamecounter.core.service.SessionService;
import com.comeon.gamecounter.core.session.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class CoreController {
    private final PlayerService playerService;
    private final GameService gameService;
    private final SessionService sessionService;
    private final GameHitQueueService gameHitQueueService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestParam @NotBlank String password) {
        Player newPlayer = playerService.createNewPlayer(password);
        return ResponseEntity.ok(Map.of("playerId", newPlayer.getPlayerId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<Session> session =
                sessionService.login(loginRequest.getPlayerId(), loginRequest.getPassword());
        if (session.isPresent()) {
            return ResponseEntity.ok(Map.of("sessionId", session.get().getSessionId()));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam @NotBlank String sessionId) {
        boolean success = sessionService.logout(sessionId);
        return success
                ? ResponseEntity.ok("Logout successful")
                : ResponseEntity.status(404).body("Invalid session ID");
    }

    @PostMapping("/hit")
    public ResponseEntity<?> hitGame(@RequestParam @NotBlank String sessionId,
                                     @RequestParam @NotBlank String gameCode) {
        gameHitQueueService.enqueueHit(sessionId, gameCode);
        return ResponseEntity.accepted().body("Hit request queued.");
    }

    @GetMapping("/admin/sessions")
    public ResponseEntity<List<Session>> getSessions() {
        List<Session> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/admin/games")
    public ResponseEntity<List<Game>> getGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }
}
