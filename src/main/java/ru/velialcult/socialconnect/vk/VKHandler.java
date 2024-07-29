package ru.velialcult.socialconnect.vk;

import com.ubivashka.vk.bukkit.events.VKMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.socialconnect.file.MessagesFile;
import ru.velialcult.socialconnect.twofactorauthorization.TwoFactorAuthorizationService;
import ru.velialcult.socialconnect.social.SocialAccountManager;
import ru.velialcult.socialconnect.social.types.SocialType;
import ru.velialcult.socialconnect.user.SocialUser;

import java.util.UUID;

public class VKHandler implements Listener {

    private final VKService vkService;
    private final MessagesFile messagesFile;
    private final SocialAccountManager accountStorage;
    private final TwoFactorAuthorizationService twoFactorAuthorizationService;

    public VKHandler(VKService vkService, MessagesFile messagesFile, SocialAccountManager accountStorage, TwoFactorAuthorizationService twoFactorAuthorizationService) {
        this.vkService = vkService;
        this.messagesFile = messagesFile;
        this.accountStorage = accountStorage;
        this.twoFactorAuthorizationService = twoFactorAuthorizationService;
    }

    @EventHandler
    public void onMessage(VKMessageEvent e) {
        if (e.getMessage().getText().startsWith("!привязать")) {
            String[] parts = e.getMessage().getText().split(" ");
            if (parts.length < 2) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.link.usage"), e.getPeer());
                return;
            }

            String playerName = parts[1];
            Player player = Bukkit.getPlayer(playerName);

            if (player == null) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.link.player-not-found"), e.getPeer());
                return;
            }

            SocialUser socialUser = accountStorage.getSocialUser(Integer.toString(e.getUserId()), SocialType.VK);

            if (socialUser == null) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), e.getPeer());
                return;
            }

            if (!socialUser.hasSocialAccount(SocialType.VK)) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), e.getPeer());
                return;
            }

            if (vkService.hasLinkingRequest(player.getUniqueId())) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.already-request"), e.getPeer());
                return;
            }

            String code = vkService.createAccountLinkingVkSession(player.getUniqueId(), e.getUserId());
            vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.link.enter-code-in-game",
                                                                             new ReplaceData("{code}", code)
            ), e.getPeer());

            VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.connect.info",
                                                                                                         new ReplaceData("{social}", SocialType.VK.getTranslationText())));

        } else if (e.getMessage().getText().startsWith("!отвязать")) {
            String[] parts = e.getMessage().getText().split(" ");

            if (parts.length != 1) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.unlink.usage"), e.getPeer());
                return;
            }

            SocialUser socialUser = accountStorage.getSocialUser(Integer.toString(e.getUserId()), SocialType.VK);

            if (socialUser == null) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), e.getPeer());
                return;
            }

            if (!socialUser.hasSocialAccount(SocialType.VK)) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), e.getPeer());
                return;
            }

            UUID uuid = socialUser.getUuid();

            vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.unlink.unlink",
                                                                             new ReplaceData("{uuid}", uuid.toString())), e.getPeer());

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();

                VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.unlink"));
            }

            accountStorage.unLinkSocial(uuid, SocialType.VK);
        } else if (e.getMessage().getText().startsWith("!информация")) {
            SocialUser socialUser = accountStorage.getSocialUser(Integer.toString(e.getUserId()), SocialType.VK);

            if (socialUser == null) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), e.getPeer());
                return;
            }

            if (!socialUser.hasSocialAccount(SocialType.VK)) {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), e.getPeer());
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(socialUser.getUuid());

            vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.info.info",
                                                                             new ReplaceData("{online}", offlinePlayer.isOnline() ? "Да" : "Нет"),
                                                                             new ReplaceData("{name}", offlinePlayer.getName()),
                                                                             new ReplaceData("{discord}", socialUser.hasSocialAccount(SocialType.DISCORD) ? "Да" : "Нет"),
                                                                             new ReplaceData("{vk}", socialUser.hasSocialAccount(SocialType.VK) ? "Да" : "Нет")),
                                  e.getPeer());
        } else if (e.getMessage().getText().startsWith("!разрешить")) {
            SocialUser socialUser = accountStorage.getSocialUser(Integer.toString(e.getUserId()), SocialType.VK);
            if (socialUser != null) {
                if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                    twoFactorAuthorizationService.verifyAuthorization(socialUser);
                }
                else {
                    vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.allow.not-need"), e.getPeer());
                }
            } else {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.account-not-found"), e.getPeer());
            }
        } else if (e.getMessage().getText().startsWith("!запретить")) {
            SocialUser socialUser = accountStorage.getSocialUser(Integer.toString(e.getUserId()), SocialType.VK);
            if (socialUser != null) {
                if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                    twoFactorAuthorizationService.declineAuthorization(socialUser);
                }
                else {
                    vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), e.getPeer());
                }
            } else {
                vkService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), e.getPeer());
            }
        }
    }
}
