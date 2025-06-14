package com.comeon.gamecounter.core.service;

import com.comeon.gamecounter.core.DAO.PlayerRepository;
import com.comeon.gamecounter.core.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Player createNewPlayer(String password) {
        Player newPlayer = new Player();
        newPlayer.setPassword(password);
        return playerRepository.save(newPlayer);
    }

    public Optional<Player> findByPlayerIdAndPassword(Long playerId, String password) {
        return playerRepository.findByPlayerIdAndPassword(playerId, password);
    }

    public Optional<Player> findById(Long playerId) {
        return playerRepository.findById(playerId);
    }
}
