package ru.velialcult.socialconnect.vk;

import com.ubivashka.vk.bukkit.BukkitVkApiPlugin;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Keyboard;
import org.bukkit.Bukkit;
import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.file.MessagesFile;
import ru.velialcult.socialconnect.social.SocialAccountManager;
import ru.velialcult.socialconnect.social.types.SocialType;
import ru.velialcult.socialconnect.link.SocialMediaAccountLinker;
import ru.velialcult.socialconnect.twofactorauthorization.TwoFactorAuthorizationService;

import java.util.Random;
import java.util.UUID;

public class VKService {

    private final CultSocialConnect cultSocialConnect;

    private static final VkApiClient CLIENT = BukkitVkApiPlugin.getPlugin(BukkitVkApiPlugin.class).getVkApiProvider()
            .getVkApiClient();
    private static final GroupActor ACTOR = BukkitVkApiPlugin.getPlugin(BukkitVkApiPlugin.class).getVkApiProvider()
            .getActor();
    private final static Random RANDOM = new Random();

    private final SocialMediaAccountLinker socialMediaAccountLinker;

    public VKService(CultSocialConnect cultSocialConnect,
                     MessagesFile messagesFile,
                     SocialAccountManager socialAccountManager,
                     TwoFactorAuthorizationService twoFactorAuthorizationService,
                     SocialMediaAccountLinker socialMediaAccountLinker) {
        this.cultSocialConnect = cultSocialConnect;
        this.socialMediaAccountLinker = socialMediaAccountLinker;
        Bukkit.getPluginManager().registerEvents(new VKHandler(this, messagesFile, socialAccountManager, twoFactorAuthorizationService), cultSocialConnect);
    }

    public String createAccountLinkingVkSession(UUID uuid, int id) {
        return socialMediaAccountLinker.addNewAccountLinkingSession(uuid, SocialType.VK, Integer.toString(id));
    }

    public boolean hasLinkingRequest(UUID uuid) {
        return socialMediaAccountLinker.hasRequestLinkAccount(uuid, SocialType.VK);
    }

    public void sendMessage(String message, int userId) {
        try {
            CLIENT.messages().send(ACTOR)
                    .randomId(RANDOM.nextInt())
                    .peerId(userId)
                    .message(message)
                    .execute();
        } catch (ApiException | ClientException ex) {
            cultSocialConnect.getLogger().warning("Произошла ошибка при отправке сообщения юзеру " + userId + ", текст сообщения: " + message + ", ошибка: " + ex.getMessage());
        }
    }

    public void sendKeyboard(Keyboard keyboard, String message, int userId) {
        try {
            CLIENT.messages().send(ACTOR)
                    .randomId(RANDOM.nextInt())
                    .peerId(userId)
                    .message(message)
                    .keyboard(keyboard)
                    .execute();
        } catch (ApiException | ClientException ex) {
            cultSocialConnect.getLogger().warning("Произошла ошибка при отправке клиавиатуры юзеру " + userId + ", текст сообщения: " + message + ", ошибка: " + ex.getMessage());
        }
    }
}
