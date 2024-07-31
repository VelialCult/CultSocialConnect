package ru.velialcult.socialconnect.social.types;

public enum SocialType {
    DISCORD("&dDiscord"),
    VK("&9ВКонтакте");

    private final String translationText;

    SocialType(String translationText) {
        this.translationText = translationText;
    }

    public String getTranslationText() {
        return translationText;
    }
}
