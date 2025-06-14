package com.comeon.gamecounter.core.service;

import com.comeon.gamecounter.core.DTO.request.GameHitRequest;
import com.comeon.gamecounter.core.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class GameHitQueueService {
    private final BlockingQueue<GameHitRequest> queue = new LinkedBlockingQueue<>();
    private final SessionService sessionService;
    private volatile boolean running = true;

    @Autowired
    public GameHitQueueService(SessionService sessionService) {
        this.sessionService = sessionService;
        startWorker();
    }

    public void enqueueHit(String sessionId, String gameCode) {
        queue.offer(new GameHitRequest(sessionId, gameCode));
    }

    @PreDestroy
    public void shutdown() {
        running = false;
    }

    private void startWorker() {
        Thread worker = new Thread(() -> {
            while (running) {
                try {
                    GameHitRequest request = queue.take();
                    Optional<Game> result = sessionService.hitGame(request);
                    if (result.isEmpty()) {
                        System.err.printf("Hit failed: invalid session or gameCode (%s, %s)%n",
                                request.getSessionId(), request.getGameCode());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }
}
