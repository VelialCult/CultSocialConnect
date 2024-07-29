package ru.velialcult.socialconnect.user;

import ru.velialcult.socialconnect.social.SocialAccount;
import ru.velialcult.socialconnect.social.types.SocialType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SocialUser {

    private final UUID uuid;
    private final List<SocialAccount> socialAccounts;

    public SocialUser(UUID uuid) {
        this(uuid, new ArrayList<>());
    }

    public SocialUser(UUID uuid, List<SocialAccount> socialAccounts) {
        this.uuid = uuid;
        this.socialAccounts = socialAccounts;
    }

    public SocialAccount getMainSocialAccount() {
        return socialAccounts.stream()
                .filter(SocialAccount::isMainAccount)
                .findAny()
                .orElse(null);
    }

    public void setMainSocialAccount(SocialType socialType) {
        if (hasSocialAccount(socialType)) {
            SocialAccount socialAccount = getSocialAccount(socialType);

            SocialAccount mainAccount = getMainSocialAccount();
            if (mainAccount != null) {
                mainAccount.setMainAccount(false);
            }

            socialAccount.setMainAccount(true);
        }
    }

    public boolean hasSocialAccount() {
        return !socialAccounts.isEmpty();
    }

    public void linkSocialAccount(SocialAccount... socialAccounts) {
        for (SocialAccount socialAccount  : socialAccounts) {
            linkSocialAccount(socialAccount);
        }
    }

    public void linkSocialAccount(SocialAccount socialAccount) {
        this.socialAccounts.add(socialAccount);
    }

    public void unLinkSocialAccount(SocialType socialType) {
        if (hasSocialAccount(socialType)) {
            SocialAccount socialAccount = getSocialAccount(socialType);
            this.socialAccounts.remove(socialAccount);
        }
    }

    public boolean hasSocialAccount(SocialType socialType) {
        return socialAccounts.stream()
                .anyMatch(socialAccount -> socialAccount.getType() == socialType);
    }

    public SocialAccount getSocialAccount(SocialType socialType) {
        return socialAccounts.stream()
                .filter(socialAccount -> socialAccount.getType() == socialType)
                .findAny()
                .orElse(null);
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<SocialAccount> getSocialAccounts() {
        return socialAccounts;
    }
}
