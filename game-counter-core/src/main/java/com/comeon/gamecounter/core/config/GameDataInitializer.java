package com.comeon.gamecounter.core.config;

import com.comeon.gamecounter.core.DAO.GameRepository;
import com.comeon.gamecounter.core.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GameDataInitializer implements CommandLineRunner {
    private final GameRepository gameRepository;

    @Value("${game.codes}")
    private String[] defaultGameCodes;

    @Autowired
    public GameDataInitializer(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        for(String code : defaultGameCodes){
            insertIfNotExists(code);
        }
    }

    private void insertIfNotExists(String gameCode) {
        if (gameRepository.findById(gameCode).isEmpty()) {
            Game game = new Game();
            game.setGameCode(gameCode);
            game.setHits(0);
            game.setCreateTime(System.currentTimeMillis());
            game.setUpdateTime(System.currentTimeMillis());
            gameRepository.save(game);
            System.out.println("Default game code " + gameCode + " inserted.");
        }
    }
}
