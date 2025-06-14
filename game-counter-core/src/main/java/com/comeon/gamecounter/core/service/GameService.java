package com.comeon.gamecounter.core.service;

import com.comeon.gamecounter.core.DAO.GameRepository;
import com.comeon.gamecounter.core.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    public Optional<Game> findByGameCode(String gameCode) {
        return gameRepository.findById(gameCode);
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Transactional
    public Game incrementGameHits(String gameCode) {
        Game game = gameRepository.findById(gameCode)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameCode));
        game.incrementHits();
        return gameRepository.save(game);
    }

}
