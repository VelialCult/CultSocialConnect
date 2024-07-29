package ru.velialcult.socialconnect.social.types;

public enum SocialType {
    DISCORD("Discord"),
    VK("ВКонтакте");

    private final String translationText;

    SocialType(String translationText) {
        this.translationText = translationText;
    }

    public String getTranslationText() {
        return translationText;
    }
}
