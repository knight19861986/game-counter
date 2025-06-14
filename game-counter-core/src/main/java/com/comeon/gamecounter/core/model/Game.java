package com.comeon.gamecounter.core.model;

import com.comeon.gamecounter.core.utility.Updatable;
import com.comeon.gamecounter.core.utility.UpdateTracker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Entity
@Table(name = "games")
@NoArgsConstructor
@AllArgsConstructor
public class Game implements Updatable {
    @Id
    @Column(name = "game_code", nullable = false, unique = true)
    private String gameCode;

    @Column(nullable = false)
    private int hits;

    @Column(nullable = false, updatable = false)
    private long createTime = System.currentTimeMillis();

    @Column(nullable = false)
    private long updateTime;

    @Transient
    private final UpdateTracker updateTracker = new UpdateTracker();

    public void setHits(int hits) {
        this.hits = hits;
        touch();
    }

    public void incrementHits() {
        this.hits++;
        touch();
    }

    @Override
    public void touch() {
        updateTracker.touch();
        this.updateTime = updateTracker.getUpdateTimestamp();
    }

    @Override
    public long getUpdateTimestamp() {
        return updateTime;
    }
}
