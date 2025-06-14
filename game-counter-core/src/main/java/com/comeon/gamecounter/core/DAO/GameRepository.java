package com.comeon.gamecounter.core.DAO;

import com.comeon.gamecounter.core.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, String> {
}
