package ru.velialcult.socialconnect.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.socialconnect.file.MessagesFile;
import ru.velialcult.socialconnect.social.SocialAccount;
import ru.velialcult.socialconnect.social.SocialAccountManager;
import ru.velialcult.socialconnect.social.types.SocialType;
import ru.velialcult.socialconnect.twofactorauthorization.TwoFactorAuthorizationService;
import ru.velialcult.socialconnect.user.SocialUser;

import java.util.UUID;

/**
 * Written by Nilsson
 * 06.06.2024
 */
public class DiscordHandler extends ListenerAdapter {

    private final DiscordService discordService;
    private final MessagesFile messagesFile;
    private final SocialAccountManager accountStorage;
    private final TwoFactorAuthorizationService twoFactorAuthorizationService;

    public DiscordHandler(DiscordService discordService, MessagesFile messagesFile, SocialAccountManager accountStorage, TwoFactorAuthorizationService twoFactorAuthorizationService) {
        this.discordService = discordService;
        this.messagesFile = messagesFile;
        this.accountStorage = accountStorage;
        this.twoFactorAuthorizationService = twoFactorAuthorizationService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String id = event.getAuthor().getId();
        String message = event.getMessage().getContentDisplay();
        if (message.startsWith("!привязать")) {
            String[] parts = message.split(" ");
            if (parts.length < 2) {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.link.usage"), id);
                return;
            }

            String playerName = parts[1];
            Player player = Bukkit.getPlayer(playerName);

            if (player == null) {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.link.player-not-found"), id);
                return;
            }

            SocialUser socialUser = accountStorage.getSocialUser(id, SocialType.DISCORD);

            if (socialUser != null && socialUser.hasSocialAccount(SocialType.DISCORD))   {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.link.already-linked"), id);
                return;
            }

            if (discordService.hasLinkingRequest(player.getUniqueId())) {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.already-request"), id);
                return;
            }

            String code = discordService.createAccountLinkingVkSession(player.getUniqueId(), id);
            discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.link.enter-code-in-game",
                                                                                  new ReplaceData("{code}", code)
            ), id);

            VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.connect.info",
                                                                                                         new ReplaceData("{social}", SocialType.DISCORD.getTranslationText())));

        } else if (message.startsWith("!отвязать")) {
            String[] parts = message.split(" ");

            if (parts.length != 1) {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.unlink.usage"), id);
                return;
            }

            SocialUser socialUser = accountStorage.getSocialUser(id, SocialType.DISCORD);

            if (socialUser == null || !socialUser.hasSocialAccount(SocialType.DISCORD))   {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), id);
                return;
            }

            UUID uuid = socialUser.getUuid();

            discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.unlink.unlink",
                                                                                  new ReplaceData("{uuid}", uuid.toString())), id);

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();

                VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.unlink"));
            }

            accountStorage.unLinkSocial(uuid, SocialType.DISCORD);
        } else if (message.startsWith("!информация")) {
            SocialUser socialUser = accountStorage.getSocialUser(id, SocialType.DISCORD);

            if (socialUser == null || !socialUser.hasSocialAccount(SocialType.DISCORD)) {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), id);
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(socialUser.getUuid());

            discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.info.info",
                                                                                  new ReplaceData("{online}", offlinePlayer.isOnline() ? "Да" : "Нет"),
                                                                                  new ReplaceData("{name}", offlinePlayer.getName()),
                                                                                  new ReplaceData("{discord}", socialUser.hasSocialAccount(SocialType.DISCORD) ? "Да" : "Нет"),
                                                                                  new ReplaceData("{vk}", socialUser.hasSocialAccount(SocialType.VK) ? "Да" : "Нет")),
                                       id);
        } else if (message.startsWith("!разрешить")) {
            SocialUser socialUser = accountStorage.getSocialUser(id, SocialType.DISCORD);
            if (socialUser != null) {
                if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                    twoFactorAuthorizationService.verifyAuthorization(socialUser);
                }
                else {
                    discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.allow.not-need"), id);
                }
            } else {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.account-not-found"), id);
            }
        } else if (message.startsWith("!запретить")) {
            SocialUser socialUser = accountStorage.getSocialUser(id, SocialType.DISCORD);
            if (socialUser != null) {
                if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                    twoFactorAuthorizationService.declineAuthorization(socialUser);
                }
                else {
                    discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), id);
                }
            } else {
                discordService.sendMessage(messagesFile.getFileOperations().getString("messages.social.commands.account-minecraft-not-found"), id);
            }
        }
    }
}
