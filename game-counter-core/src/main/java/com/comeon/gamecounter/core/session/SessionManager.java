package com.comeon.gamecounter.core.session;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Session createSession(long playerId) {
        Session session = new Session(playerId);
        sessions.put(session.getSessionId(), session);
        return session;
    }

    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public boolean isSessionValid(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public List<Session> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }
}
