package com.comeon.gamecounter.core.service;

import com.comeon.gamecounter.core.DTO.request.GameHitRequest;
import com.comeon.gamecounter.core.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameHitQueueServiceTest {
    private SessionService sessionService;
    private GameHitQueueService gameHitQueueService;

    @BeforeEach
    void setUp() {
        sessionService = mock(SessionService.class);
        gameHitQueueService = new GameHitQueueService(sessionService);
    }

    @Test
    void testEnqueueHit_callSessionServiceHitGame() throws InterruptedException{
        String sessionId = "SessionABC123";
        String gameCode = "Game001";
        int originalHits = 5;
        GameHitRequest expectedRequest = new GameHitRequest(sessionId, gameCode);
        Game game001 = new Game();
        game001.setGameCode(gameCode);
        game001.setHits(originalHits);
        when(sessionService.hitGame(any())).thenReturn(Optional.of(game001));

        gameHitQueueService.enqueueHit(sessionId, gameCode);
        Thread.sleep(100);
        ArgumentCaptor<GameHitRequest>captor = ArgumentCaptor.forClass(GameHitRequest.class);
        verify(sessionService, timeout(500).times(1)).hitGame(captor.capture());

        GameHitRequest actualRequest = captor.getValue();
        assertEquals(expectedRequest.getSessionId(), actualRequest.getSessionId());
        assertEquals(expectedRequest.getGameCode(), actualRequest.getGameCode());
    }
}
