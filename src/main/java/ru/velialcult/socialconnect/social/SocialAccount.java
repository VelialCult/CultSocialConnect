package ru.velialcult.socialconnect.social;

import ru.velialcult.socialconnect.social.types.SocialType;

public interface SocialAccount {

    String getUserID();

    SocialType getType();

    void sendMessage(String text);

    boolean isMainAccount();

    void setMainAccount(boolean b);
}
