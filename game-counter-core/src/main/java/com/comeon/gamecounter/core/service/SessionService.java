package com.comeon.gamecounter.core.service;

import com.comeon.gamecounter.core.DTO.request.GameHitRequest;
import com.comeon.gamecounter.core.model.Game;
import com.comeon.gamecounter.core.model.Player;
import com.comeon.gamecounter.core.session.Session;
import com.comeon.gamecounter.core.session.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final PlayerService playerService;
    private final GameService gameService;
    private final SessionManager sessionManager;

    public Optional<Session> login(Long playerId, String password) {
        Optional<Player> playerOpt = playerService.findByPlayerIdAndPassword(playerId, password);
        return playerOpt.map(player -> sessionManager.createSession(player.getPlayerId()));
    }

    public boolean logout(String sessionId) {
        if (sessionManager.isSessionValid(sessionId)) {
            sessionManager.removeSession(sessionId);
            return true;
        }
        return false;
    }

    public Optional<Game> hitGame(GameHitRequest gameHitRequest) {
        if (!sessionManager.isSessionValid(gameHitRequest.getSessionId())) {
            return Optional.empty();
        }
        try {
            Game updatedGame = gameService.incrementGameHits(gameHitRequest.getGameCode());
            return Optional.of(updatedGame);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public List<Session> getAllSessions() {
        return sessionManager.getAllSessions();
    }
}
