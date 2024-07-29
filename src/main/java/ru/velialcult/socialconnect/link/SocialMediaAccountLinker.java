package ru.velialcult.socialconnect.link;

import ru.velialcult.socialconnect.generator.CodeGenerator;
import ru.velialcult.socialconnect.social.types.SocialType;

import java.util.*;

public class SocialMediaAccountLinker {

    private final List<AccountLinkingSession> accountLinkingSessions;

    public SocialMediaAccountLinker() {
        this.accountLinkingSessions = new ArrayList<>();
    }

    public boolean isValidRequestLinkCode(UUID uuid,
                                          String code,
                                          SocialType socialType
    ) {
        if (hasRequestLinkAccount(uuid, socialType)) {
            AccountLinkingSession accountLinkingSession = getAccountLinkingSessionByUUID(uuid);
            return accountLinkingSession.getLinkCode().equals(code);
        }

        return false;
    }

    public void removeAccountLinkingSession(UUID uuid,
                                            SocialType socialType
    ) {
        if (hasRequestLinkAccount(uuid, socialType)) {
            AccountLinkingSession accountLinkingSession = getAccountLinkingSessionByUUID(uuid);
            accountLinkingSessions.remove(accountLinkingSession);
        }
    }

    public boolean socialAccountIsEqual(UUID uuid,
                                        String userUrl,
                                        SocialType socialType
    ) {
        if (hasRequestLinkAccount(uuid, socialType)) {
            AccountLinkingSession accountLinkingSession = getAccountLinkingSessionByUUID(uuid);
            return accountLinkingSession.getUserUrl().equals(userUrl);
        }

        return false;
    }

    public AccountLinkingSession getAccountLinkingSessionByUUID(UUID uuid) {
        return accountLinkingSessions.stream()
                .filter(accountLinkingSession -> accountLinkingSession.getUserId().equals(uuid))
                .findAny()
                .orElse(null);
    }

    public boolean hasRequestLinkAccount(UUID uuid, SocialType socialType) {
        return accountLinkingSessions.stream()
                .anyMatch(accountLinkingSession -> accountLinkingSession.getUserId().equals(uuid) &&
                        accountLinkingSession.getLinkType() == socialType);
    }

    public String addNewAccountLinkingSession(UUID uuid,
                                            SocialType socialType,
                                            String userUrl) {
        String linkCode = CodeGenerator.generateLinkCode();
        AccountLinkingSession accountLinkingSession = new AccountLinkingSession(uuid, socialType, linkCode, userUrl);
        this.accountLinkingSessions.add(accountLinkingSession);
        return linkCode;
    }
}
