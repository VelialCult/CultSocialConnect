package ru.velialcult.socialconnect.twofactorauthorization;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.file.MessagesFile;
import ru.velialcult.socialconnect.social.SocialAccount;
import ru.velialcult.socialconnect.social.session.Session;
import ru.velialcult.socialconnect.social.session.SessionManager;
import ru.velialcult.socialconnect.social.types.SocialType;
import ru.velialcult.socialconnect.user.SocialUser;

import java.io.IOException;
import java.util.*;

public class TwoFactorAuthorizationService {

    private final List<TwoFactorAuthorizationSession> authorizationSessions;
    private final MessagesFile messagesFile;
    private final SessionManager sessionManager;

    public TwoFactorAuthorizationService(MessagesFile messagesFile,
                                         SessionManager sessionManager
    ) {
        this.authorizationSessions = new ArrayList<>();
        this.messagesFile = messagesFile;
        this.sessionManager = sessionManager;
    }

    public boolean isNeedAuthorized(SocialUser socialUser) {
        return getTwoFactorAuthorizationSessionByUser(socialUser) != null;
    }

    public void authorize(SocialUser socialUser){
        if (socialUser.hasSocialAccount()) {
            List<SocialAccount> socialAccountList = socialUser.getSocialAccounts();
            OfflinePlayer player = Bukkit.getOfflinePlayer(socialUser.getUuid());

            if (player.isOnline()) {
                VersionAdapter.MessageUtils().sendMessage(player.getPlayer(), messagesFile.getFileOperations().getString("messages.minecraft.authorization.confirm-join"));
            }

            SocialAccount mainSocialAccount = socialUser.getMainSocialAccount();
            if (mainSocialAccount != null) {
                mainSocialAccount.sendMessage(messagesFile.getFileOperations().getString(
                        "messages.social.confirm-join",
                        new ReplaceData("{player}", player.getName())));
                authorizationSessions.add(new TwoFactorAuthorizationSession(mainSocialAccount, socialUser.getUuid()));
            } else {
                SocialAccount socialAccount = socialUser.getSocialAccounts()
                        .stream().findFirst().orElse(null);

                if (socialAccount != null) {
                    mainSocialAccount.sendMessage(messagesFile.getFileOperations().getString(
                            "messages.social.confirm-join",
                            new ReplaceData("{player}", player.getName())));
                    authorizationSessions.add(new TwoFactorAuthorizationSession(mainSocialAccount, socialUser.getUuid()));
                }
            }
        }
    }

    private TwoFactorAuthorizationSession getTwoFactorAuthorizationSessionByUser(SocialUser socialUser) {
        return authorizationSessions.stream()
                .filter(authorizationSessions -> authorizationSessions.getUuid().equals(socialUser.getUuid()))
                .findAny()
                .orElse(null);
    }

    public void verifyAuthorization(SocialUser socialUser) {
        TwoFactorAuthorizationSession twoFactorAuthorizationSession = getTwoFactorAuthorizationSessionByUser(socialUser);
        if (twoFactorAuthorizationSession != null) {
            twoFactorAuthorizationSession.getBukkitTask().cancel();
            authorizationSessions.remove(twoFactorAuthorizationSession);
            twoFactorAuthorizationSession.getSocialAccount().sendMessage(messagesFile.getFileOperations().getString(
                    "messages.social.commands.allow.allow"));
            Player player = Bukkit.getPlayer(socialUser.getUuid());
            if (player != null && player.isOnline()) {
                VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.authorization.allow"));
            }

            Session session = sessionManager.getSession(socialUser);
            session.setLastLoginInSeconds(System.currentTimeMillis() / 1000);
            session.setLastLoginIP(player.getAddress().getAddress().getHostAddress());
        }
    }

    public void declineAuthorization(SocialUser socialUser) {
        TwoFactorAuthorizationSession session = getTwoFactorAuthorizationSessionByUser(socialUser);
        if (session != null) {
            authorizationSessions.remove(session);
            session.getSocialAccount().sendMessage(messagesFile.getFileOperations().getString(
                    "messages.social.commands.allow.blocked"));
            List<String> commands = CultSocialConnect.getInstance().getConfig().getStringList("settings.authorization.blocked");
            for (String command : commands) {
                Bukkit.getScheduler().runTask(CultSocialConnect.getInstance(),
                                              () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", Bukkit.getOfflinePlayer(socialUser.getUuid()).getName())));
            }
        }
    }
}
