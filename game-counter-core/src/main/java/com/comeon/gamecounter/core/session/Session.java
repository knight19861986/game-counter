package com.comeon.gamecounter.core.session;

import com.comeon.gamecounter.core.legacy.Utils;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Session {
    private final String sessionId;
    private final long playerId;
    private final long createdAt;

    public Session(long playerId) {
        this.playerId = playerId;
        this.sessionId = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder();
        res.append("SessionId: " + sessionId + "\n");
        res.append("PlayerId: " + playerId + "\n");
        res.append("CreatedTime: " + Utils.timestampToDatetime(createdAt) + "\n" + "\n");
        return res.toString();
    }
}