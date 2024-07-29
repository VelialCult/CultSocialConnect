package ru.velialcult.socialconnect.social.session;

import ru.velialcult.library.java.database.Connector;
import ru.velialcult.library.java.database.DataBase;
import ru.velialcult.library.java.database.query.QuerySymbol;
import ru.velialcult.library.java.database.query.SQLQuery;
import ru.velialcult.library.java.database.table.ColumnType;
import ru.velialcult.library.java.database.table.TableColumn;
import ru.velialcult.library.java.database.table.TableConstructor;
import ru.velialcult.socialconnect.user.SocialUser;

/**
 * Written by Nilsson
 * 30.05.2024
 */
public class SessionDataBase {

    private final DataBase dataBase;
    private final Connector connector;

    public SessionDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
        this.connector = dataBase.getConnector();
        createTables();
    }

    public void save(Session session) {
        connector.execute(SQLQuery.insertOrUpdate(dataBase, "users_session")
                                  .set("lastLogin", session.getLastLoginInSeconds())
                                  .set("lastLoginIp", session.getLastLoginIP())
                                  .set("uuid", session.getSocialUser())
                                  .where("uuid", QuerySymbol.EQUALLY, session.getSocialUser()),
                          true);
    }

    public Session loadSession(SocialUser socialUser) {
        return connector.executeQuery(SQLQuery.selectFrom("users_session").where("uuid", QuerySymbol.EQUALLY, socialUser.getUuid().toString()),
                               rs -> {
                                    if (rs.next()) {
                                        long lastLogin = rs.getLong("lastLogin");
                                        String lastLoginIp = rs.getString("lastLoginIp");
                                        return new Session(socialUser.getUuid(), lastLoginIp, lastLogin);
                                    }
                                    return null;
                               }, false);
    }

    private void createTables() {
        new TableConstructor("users_session",
                             new TableColumn("uuid", ColumnType.VARCHAR_32).primaryKey(true),
                             new TableColumn("lastLogin", ColumnType.BIG_INT),
                             new TableColumn("lastLoginIp", ColumnType.VARCHAR_16)
        ).create(dataBase);
    }
}
