package ru.velialcult.socialconnect.social.types;

import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.social.SocialAccount;

/**
 * Written by Nilsson
 * 06.06.2024
 */
public class DiscordAccount implements SocialAccount {

    private final String userID;
    private final SocialType socialType = SocialType.DISCORD;
    private boolean mainAccount;

    public DiscordAccount(String userID, boolean mainAccount) {
        this.userID = userID;
        this.mainAccount = mainAccount;
    }

    public void sendMessage(String text) {
        CultSocialConnect.getInstance().getDiscordManager().sendMessage(text, userID);
    }

    @Override
    public boolean isMainAccount() {
        return mainAccount;
    }

    @Override
    public void setMainAccount(boolean b) {
        this.mainAccount = b;
    }

    public SocialType getSocialType() {
        return socialType;
    }

    @Override
    public String getUserID() {
        return userID;
    }

    @Override
    public SocialType getType() {
        return socialType;
    }
}
