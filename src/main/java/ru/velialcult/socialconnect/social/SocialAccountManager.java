package ru.velialcult.socialconnect.social;

import org.jetbrains.annotations.Nullable;
import ru.velialcult.socialconnect.social.database.SocialDataBase;
import ru.velialcult.socialconnect.social.types.DiscordAccount;
import ru.velialcult.socialconnect.social.types.SocialType;
import ru.velialcult.socialconnect.social.types.VKAccount;
import ru.velialcult.socialconnect.user.SocialUser;

import java.util.*;

public class SocialAccountManager {

    private final Map<UUID, SocialUser> socialUserList;
    private final SocialDataBase socialDataBase;

    public SocialAccountManager(SocialDataBase socialDataBase) {
        this.socialDataBase = socialDataBase;
        this.socialUserList = new HashMap<>();
    }

    public SocialUser getSocialUser(UUID uuid) {
        return socialUserList.computeIfAbsent(uuid, socialDataBase::loadSocialUser);
    }

    public void setMainAccount(SocialUser socialUser, SocialType socialType) {
        SocialAccount mainAccount = socialUser.getMainSocialAccount();

        if (mainAccount != null)
            socialDataBase.setMainAccount(socialUser.getUuid(), mainAccount.getType(), false);

        socialUser.setMainSocialAccount(socialType);
        socialDataBase.setMainAccount(socialUser.getUuid(), socialType, true);
    }

    @Nullable
    public SocialUser getSocialUser(String id, SocialType socialType) {
        return socialUserList.values().stream()
                .filter(user -> user.getSocialAccount(socialType) != null)
                .filter(user -> id.equals(user.getSocialAccount(socialType).getUserID()))
                .findAny()
                .orElse(null);
    }

    public void linkSocial(UUID uuid, String socialId ,SocialType socialType) {
        SocialUser socialUser = getSocialUser(uuid);
        if (socialUser != null) {
            switch (socialType) {

                case VK: {
                    SocialAccount socialAccount = new VKAccount(Integer.parseInt(socialId), false);
                    socialUser.linkSocialAccount(socialAccount);
                    socialDataBase.saveToDataBase(uuid, socialType, socialId);
                    break;
                }

                case DISCORD: {
                    SocialAccount socialAccount = new DiscordAccount(socialId, false);
                    socialUser.linkSocialAccount(socialAccount);
                    socialDataBase.saveToDataBase(uuid, socialType, socialId);
                    break;
                }
            }
        }
    }

    public void unLinkSocial(UUID uuid, SocialType socialType) {
        SocialUser socialUser = getSocialUser(uuid);
        if (socialUser != null) {
            socialUser.unLinkSocialAccount(socialType);
            socialDataBase.deleteFromDataBase(uuid, socialType);
        }
    }
}

