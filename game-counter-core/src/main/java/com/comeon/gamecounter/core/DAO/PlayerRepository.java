package com.comeon.gamecounter.core.DAO;

import com.comeon.gamecounter.core.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByPlayerIdAndPassword(Long playerId, String password);
}
