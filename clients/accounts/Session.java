package clients.accounts;

import clients.accounts.Accounts;
import java.time.LocalDateTime;

public class Session {
    private final String sessionId;
    private final Accounts account;
    private final LocalDateTime creationTime;

    public Session(String sessionId, Accounts account) {
        this.sessionId = sessionId;
        this.account = account;
        this.creationTime = LocalDateTime.now();
    }

    public String getSessionId() {
        return sessionId;
    }

    public Accounts getAccount() {
        return account;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}