package clients.accounts;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private static SessionManager instance;
    private final Map<UUID, Session> activeSessions;
    private final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();
    private SessionManager() {
        activeSessions = new HashMap<>();
    }

    // Singleton instance
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Log in a user and create a session
    public synchronized void login(UUID sessionId, Accounts account) {
        Session session = new Session(sessionId, account);
        activeSessions.put(sessionId, session);
        threadLocalSession.set(session); // Store the session in ThreadLocal
    }

    // Log out a user and remove their session
    public synchronized void logout(String sessionId) {
        activeSessions.remove(sessionId);
    }

    // Fetch session details by session ID
    public synchronized Session getSession(UUID sessionId) {
        return activeSessions.get(sessionId);
    }

    // Check if a session exists
    public synchronized boolean isLoggedIn(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }
    // Get the session for the current thread
    public Session getCurrentSession() {
        return threadLocalSession.get();
    }

    // Set a session for the current thread manually (if needed)
    public void setCurrentSession(Session session) {
        threadLocalSession.set(session);
    }
}