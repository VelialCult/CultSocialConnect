package ru.velialcult.socialconnect.twofactorauthorization;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.velialcult.library.bukkit.notification.NotificationService;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.socialconnect.file.MessagesFile;
import ru.velialcult.socialconnect.social.SocialAccountManager;
import ru.velialcult.socialconnect.social.session.Session;
import ru.velialcult.socialconnect.social.session.SessionManager;
import ru.velialcult.socialconnect.user.SocialUser;

public class TwoFactorAuthorizationHandler implements Listener {

    private final SocialAccountManager socialAccountManager;
    private final TwoFactorAuthorizationService twoFactorAuthorizationService;
    private final NotificationService notificationService;
    private final MessagesFile messagesFile;
    private final SessionManager sessionManager;

    public TwoFactorAuthorizationHandler(
            SocialAccountManager socialAccountManager,
            TwoFactorAuthorizationService twoFactorAuthorizationService,
            MessagesFile messagesFile,
            SessionManager sessionManager
    ) {
        this.socialAccountManager = socialAccountManager;
        this.twoFactorAuthorizationService = twoFactorAuthorizationService;
        this.notificationService = VersionAdapter.getNotificationService();
        this.messagesFile = messagesFile;
        this.sessionManager = sessionManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String ip = player.getAddress().getAddress().getHostAddress();
        SocialUser socialUser = socialAccountManager.getSocialUser(player.getUniqueId());
        if (socialUser != null) {
            Session session = sessionManager.getSession(socialUser);
            if (sessionManager.sessionIsExpire(socialUser) || !session.getLastLoginIP().equalsIgnoreCase(ip)) {
                twoFactorAuthorizationService.authorize(socialUser);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        SocialUser socialUser = socialAccountManager.getSocialUser(player.getUniqueId());
        if (socialUser != null) {
            if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                notificationService.sendMessage(player.getUniqueId(), "social-move", 3, () -> {
                    VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.authorization.cant-move"));
                });
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SocialUser socialUser = socialAccountManager.getSocialUser(player.getUniqueId());
        if (socialUser != null) {
            if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.authorization.cant-break"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        SocialUser socialUser = socialAccountManager.getSocialUser(player.getUniqueId());
        if (socialUser != null) {
            if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.authorization.cant-place"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SocialUser socialUser = socialAccountManager.getSocialUser(player.getUniqueId());
        if (socialUser != null) {
            if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                notificationService.sendMessage(player.getUniqueId(), "social-interact", 3, () -> {
                    VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.authorization.cant-interact"));
                });
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        SocialUser socialUser = socialAccountManager.getSocialUser(player.getUniqueId());
        if (socialUser != null) {
            if (twoFactorAuthorizationService.isNeedAuthorized(socialUser)) {
                VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.authorization.cant-run-command"));
                event.setCancelled(true);
            }
        }
    }
}
