package clients.accounts;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    private final UUID sessionId;
    private final Accounts account;
    private final LocalDateTime creationTime;
    public Session(UUID sessionId, Accounts account) {
        this.sessionId = sessionId;
        this.account = account;
        this.creationTime = LocalDateTime.now();
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Accounts getAccount() {
        return account;
    }
    public void setRole(String role) {
        account.setRole(role);
    }
    public String getRole() {
        return account.getRole();
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}