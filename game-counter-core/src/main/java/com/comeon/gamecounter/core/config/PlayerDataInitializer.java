package com.comeon.gamecounter.core.config;

import com.comeon.gamecounter.core.DAO.PlayerRepository;
import com.comeon.gamecounter.core.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PlayerDataInitializer implements CommandLineRunner {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerDataInitializer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (playerRepository.count() == 0) {
            playerRepository.save(new Player(null, "test123", System.currentTimeMillis(), System.currentTimeMillis()));
            playerRepository.save(new Player(null, "admin456", System.currentTimeMillis(), System.currentTimeMillis()));
            playerRepository.save(new Player(null, "pw789", System.currentTimeMillis(), System.currentTimeMillis()));
            System.out.println("Default players inserted.");
        }
    }
}
