package ru.velialcult.socialconnect.social.database;

import ru.velialcult.library.java.database.Connector;
import ru.velialcult.library.java.database.DataBase;
import ru.velialcult.library.java.database.query.QuerySymbol;
import ru.velialcult.library.java.database.query.SQLQuery;
import ru.velialcult.library.java.database.table.ColumnType;
import ru.velialcult.library.java.database.table.TableColumn;
import ru.velialcult.library.java.database.table.TableConstructor;
import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.social.SocialAccount;
import ru.velialcult.socialconnect.social.types.DiscordAccount;
import ru.velialcult.socialconnect.social.types.SocialType;
import ru.velialcult.socialconnect.social.types.VKAccount;
import ru.velialcult.socialconnect.user.SocialUser;

import java.util.*;

public class SocialDataBase {

    private final Connector connector;
    private final DataBase dataBase;

    public SocialDataBase(CultSocialConnect nightPSAddon) {
        this.dataBase = nightPSAddon.getDataBase();
        this.connector = dataBase.getConnector();
        createTable();
    }

    public void setMainAccount(UUID uuid, SocialType socialType, boolean mainAccount) {
        connector.execute(SQLQuery.insertOrUpdate(dataBase, "user_accounts")
                                  .set("main", mainAccount ? 1 : 0)
                                  .where("uuid", QuerySymbol.EQUALLY, uuid.toString())
                                  .where("type", QuerySymbol.EQUALLY, socialType.toString()),
                          true);
    }

    public void saveToDataBase(UUID uuid, SocialType socialType, String id) {
        connector.execute(SQLQuery.insertOrUpdate(dataBase, "user_accounts")
                                  .set("uuid", uuid.toString())
                                  .set("type", socialType.toString())
                                  .set("id", id)
                                  .set("main", 0)
                                  .where("uuid", QuerySymbol.EQUALLY, uuid.toString())
                                  .where("type", QuerySymbol.EQUALLY, socialType.toString()),
                          true);
    }

    private List<SocialAccount> loadSocialAccount(UUID uuid) {
        List<SocialAccount> socialAccounts = new ArrayList<>();
        connector.executeQuery(SQLQuery.selectFrom("user_accounts")
                                       .where("uuid", QuerySymbol.EQUALLY, uuid.toString()),
                               rs -> {
                                   if (rs.next()) {
                                       SocialType socialType = SocialType.valueOf(rs.getString("type"));
                                       String id = rs.getString("id");
                                       boolean mainAccount = rs.getInt("main") == 1;

                                       switch (socialType) {

                                           case VK: {
                                               socialAccounts.add(new VKAccount(Integer.parseInt(id), mainAccount));
                                               break;
                                           }

                                           case DISCORD:
                                               socialAccounts.add(new DiscordAccount(id, mainAccount));
                                               break;
                                       }
                                   }

                                   return Void.TYPE;
                               }, false);
        return socialAccounts;
    }

    public SocialUser loadSocialUser(UUID uuid) {
        List<SocialAccount> socialAccounts = loadSocialAccount(uuid);
        return new SocialUser(uuid, socialAccounts);
    }

    public void deleteFromDataBase(UUID uuid, SocialType socialType) {
        connector.execute(SQLQuery.deleteFrom("user_accounts")
                                  .where("uuid", QuerySymbol.EQUALLY, uuid.toString())
                                  .where("type", QuerySymbol.EQUALLY, socialType.toString()),
                          true);
    }

    private void createTable() {
        new TableConstructor("user_accounts",
                             new TableColumn("uuid", ColumnType.VARCHAR_32),
                             new TableColumn("type", ColumnType.VARCHAR_16),
                             new TableColumn("main", ColumnType.BOOLEAN),
                             new TableColumn("id", ColumnType.VARCHAR_128)
        ).create(dataBase);
    }
}
