package ru.velialcult.socialconnect.social.session;

import ru.velialcult.socialconnect.user.SocialUser;

import java.util.UUID;

/**
 * Written by Nilsson
 * 30.05.2024
 */
public class Session {

    private final UUID socialUser;

    private String lastLoginIP;
    private long lastLoginInSeconds;

    public Session(UUID uuid, String lastLoginIP, long lastLoginInSeconds) {
        this.socialUser = uuid;
        this.lastLoginIP = lastLoginIP;
        this.lastLoginInSeconds = lastLoginInSeconds;
    }

    public UUID getSocialUser() {
        return socialUser;
    }

    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    public void setLastLoginInSeconds(long lastLoginInSeconds) {
        this.lastLoginInSeconds = lastLoginInSeconds;
    }

    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public long getLastLoginInSeconds() {
        return lastLoginInSeconds;
    }
}
