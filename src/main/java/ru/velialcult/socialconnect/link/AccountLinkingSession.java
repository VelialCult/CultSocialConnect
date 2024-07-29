package ru.velialcult.socialconnect.link;

import ru.velialcult.socialconnect.social.types.SocialType;

import java.util.UUID;

public class AccountLinkingSession {

    private final UUID userId;
    private final SocialType socialType;
    private final String linkCode;
    private final String userUrl;

    public AccountLinkingSession(UUID uuid,
                                 SocialType socialType,
                                 String linkCode,
                                 String userUrl) {
        this.userId = uuid;
        this.socialType = socialType;
        this.linkCode = linkCode;
        this.userUrl = userUrl;
    }

    public SocialType getLinkType() {
        return socialType;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public UUID getUserId() {
        return userId;
    }
}
