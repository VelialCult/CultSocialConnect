package ru.velialcult.socialconnect.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.file.MessagesFile;
import ru.velialcult.socialconnect.link.SocialMediaAccountLinker;
import ru.velialcult.socialconnect.social.SocialAccountManager;
import ru.velialcult.socialconnect.social.types.SocialType;
import ru.velialcult.socialconnect.twofactorauthorization.TwoFactorAuthorizationService;

import javax.security.auth.login.LoginException;
import java.util.UUID;

/**
 * Written by Nilsson
 * 06.06.2024
 */
public class DiscordService {

    private final JDA jda;
    private final SocialMediaAccountLinker socialMediaAccountLinker;


    public DiscordService(CultSocialConnect cultSocialConnect,
                          MessagesFile messagesFile,
                          SocialAccountManager socialAccountManager,
                          TwoFactorAuthorizationService twoFactorAuthorizationService,
                          SocialMediaAccountLinker socialMediaAccountLinker) throws LoginException {
        this.jda = JDABuilder.createDefault(cultSocialConnect.getConfig().getString("settings.discord.token"))
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing(cultSocialConnect.getConfig().getString("settings.discord.activity")))
                .build();

        try {
            jda.awaitReady();
            jda.removeEventListener(jda.getRegisteredListeners().toArray());
        } catch (InterruptedException e) {
            cultSocialConnect.getLogger().warning("Произошла ошибка при подключении к Discord: " + e.getMessage());
        }

        jda.addEventListener(new DiscordHandler(this, messagesFile, socialAccountManager, twoFactorAuthorizationService));

        this.socialMediaAccountLinker = socialMediaAccountLinker;
    }

    public String createAccountLinkingVkSession(UUID uuid, String id) {
        return socialMediaAccountLinker.addNewAccountLinkingSession(uuid, SocialType.DISCORD, id);
    }

    public boolean hasLinkingRequest(UUID uuid) {
        return socialMediaAccountLinker.hasRequestLinkAccount(uuid, SocialType.DISCORD);
    }

    public void sendMessage(String message, String userId) {
        jda.retrieveUserById(userId).queue(user -> {
            user.openPrivateChannel().queue((channel) -> {
                channel.sendMessage(message).queue();
            }, (ex) -> {
                System.out.println("Произошла ошибка при отправке сообщения юзеру " + userId + ", текст сообщения: " + message + ", ошибка: " + ex.getMessage());
            });
        }, (ex) -> {
            System.out.println("Не удалось отправить сообщение " + userId + ", потому что пользователь == null");
        });
    }

    public JDA getJda() {
        return jda;
    }
}
