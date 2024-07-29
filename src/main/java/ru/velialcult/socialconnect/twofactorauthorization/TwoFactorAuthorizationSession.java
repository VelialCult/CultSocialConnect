package ru.velialcult.socialconnect.twofactorauthorization;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.social.SocialAccount;

import java.util.UUID;

public class TwoFactorAuthorizationSession {

    private final SocialAccount socialAccount;
    private final UUID uuid;

    private BukkitTask bukkitTask;

    public TwoFactorAuthorizationSession(SocialAccount socialAccount,
                                         UUID uuid) {
        this.socialAccount = socialAccount;
        this.uuid = uuid;
        runTimer();
    }

    public void runTimer() {
        this.bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.kickPlayer(CultSocialConnect.getInstance().getMessagesFile().getFileOperations().getString("messages.minecraft.authorization.timeout"));
                }
            }
        }.runTaskLater(CultSocialConnect.getInstance(), TimeUtil.parseStringToTime(CultSocialConnect.getInstance().getConfig().getString("settings.authorization.time")) * 20);
    }

    public UUID getUuid() {
        return uuid;
    }

    public SocialAccount getSocialAccount() {
        return socialAccount;
    }

    public BukkitTask getBukkitTask() {
        return bukkitTask;
    }
}
