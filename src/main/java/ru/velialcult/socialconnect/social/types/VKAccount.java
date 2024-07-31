package ru.velialcult.socialconnect.social.types;

import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.social.SocialAccount;
import ru.velialcult.socialconnect.vk.VKService;

public class VKAccount implements SocialAccount {

    private final Integer userID;
    private final SocialType socialType = SocialType.VK;
    private boolean mainAccount;

    public VKAccount(Integer userID, boolean mainAccount) {
        this.userID = userID;
    }

    public void sendMessage(String text) {
        VKService vkService = CultSocialConnect.getInstance().getVkManager();
        if (vkService != null) {
            vkService.sendMessage(text, userID);
        }
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
        return Integer.toString(userID);
    }

    @Override
    public SocialType getType() {
        return socialType;
    }
}
