package com.comeon.gamecounter.core.model;


import com.comeon.gamecounter.core.utility.Updatable;
import com.comeon.gamecounter.core.utility.UpdateTracker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Entity
@Table(name = "players")
@AllArgsConstructor
@NoArgsConstructor
public class Player implements Updatable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private long createTime = System.currentTimeMillis();

    @Column(nullable = false)
    private long updateTime;

    @Transient
    private final UpdateTracker updateTracker = new UpdateTracker();

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
        touch();
    }

    public void setPassword(String password) {
        this.password = password;
        touch();
    }

    @Override
    public void touch() {
        updateTracker.touch();
        this.updateTime = updateTracker.getUpdateTimestamp();
    }

    @Override
    public long getUpdateTimestamp() {
        return updateTracker.getUpdateTimestamp();
    }
}
