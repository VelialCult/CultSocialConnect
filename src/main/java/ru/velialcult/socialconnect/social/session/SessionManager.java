package ru.velialcult.socialconnect.social.session;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.user.SocialUser;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * Written by Nilsson
 * 30.05.2024
 */
public class SessionManager {

    private final List<Session> sessions;

    private final FileConfiguration config;
    private final SessionDataBase sessionDataBase;

    public SessionManager(CultSocialConnect cultSocialConnect) {
        this.sessions = new ArrayList<>();
        this.config = cultSocialConnect.getConfig();
        this.sessionDataBase = new SessionDataBase(cultSocialConnect.getDataBase());
    }

    @Nullable
    public Session getSessionBySocialUser(SocialUser socialUser) {
        return sessions.stream()
                .filter(session -> session.getSocialUser().equals(socialUser.getUuid()))
                .findAny()
                .orElse(null);
    }

    @NotNull
    public Session getSession(SocialUser socialUser) {
        Session session = getSessionBySocialUser(socialUser);
        if (session == null) {
            session = sessionDataBase.loadSession(socialUser);
            if (session == null) {
                session = new Session(socialUser.getUuid(), Bukkit.getPlayer(socialUser.getUuid()).getAddress().getAddress().getHostAddress(), LocalDateTime.now().toEpochSecond(ZoneOffset.ofHoursMinutes(3, 0)));
            }

            sessions.add(session);
        }

        return session;
    }

    public void saveAll() {
        sessions.forEach(sessionDataBase::save);
    }

    public boolean sessionIsExpire(SocialUser socialUser) {
        long sessionTime = TimeUtil.parseStringToTime(config.getString("settings.session.session-time"));
        Session session = getSession(socialUser);
        long lastLogin = session.getLastLoginInSeconds();
        Instant instant = Instant.ofEpochSecond(lastLogin);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Moscow")).plusSeconds(sessionTime);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        return Duration.between(now, zonedDateTime).isNegative();
    }

    public SessionDataBase getSessionDataBase() {
        return sessionDataBase;
    }
}
