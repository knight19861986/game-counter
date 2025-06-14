package com.comeon.gamecounter.core.legacy;

import com.comeon.gamecounter.core.session.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private Map<String, Session> sessions;
    public SessionManager() {
        this.sessions = new ConcurrentHashMap<String, Session>();
    }

    public boolean isSessionValid(String sessionId) {

        return this.sessions.containsKey(sessionId);
    }

    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public String createSession(long playerId) {
        Session session = new Session(playerId);
        this.sessions.put(session.getSessionId(), session);
        return session.getSessionId();
    }

    public boolean removeSession(String sessionId) {
        boolean res = false;
        if (this.isSessionValid(sessionId)) {
            this.sessions.remove(sessionId);
            res = true;
        }
        return res;
    }
}
