package com.comeon.gamecounter.core.utility;

public class UpdateTracker {
    private long updateTimestamp;

    public void touch() {
        updateTimestamp = System.currentTimeMillis();
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }
}
