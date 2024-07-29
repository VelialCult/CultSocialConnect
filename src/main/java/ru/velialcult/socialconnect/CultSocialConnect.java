package ru.velialcult.socialconnect;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.library.java.database.DataBase;
import ru.velialcult.library.java.database.DataBaseType;
import ru.velialcult.socialconnect.command.SocialCommand;
import ru.velialcult.socialconnect.discord.DiscordService;
import ru.velialcult.socialconnect.file.InventoriesFile;
import ru.velialcult.socialconnect.file.MessagesFile;
import ru.velialcult.socialconnect.link.SocialMediaAccountLinker;
import ru.velialcult.socialconnect.providers.ProvidersManager;
import ru.velialcult.socialconnect.social.session.SessionManager;
import ru.velialcult.socialconnect.twofactorauthorization.TwoFactorAuthorizationHandler;
import ru.velialcult.socialconnect.twofactorauthorization.TwoFactorAuthorizationService;
import ru.velialcult.socialconnect.social.SocialAccountManager;
import ru.velialcult.socialconnect.vk.VKService;
import ru.velialcult.socialconnect.social.database.SocialDataBase;

public class CultSocialConnect extends JavaPlugin {

    private static CultSocialConnect instance;

    private DataBase dataBase;

    private SocialMediaAccountLinker socialMediaAccountLinker;

    private VKService vkService;
    private DiscordService discordService;

    private SessionManager sessionManager;

    private SocialAccountManager accountStorage;
    private SocialDataBase socialDataBase;

    private MessagesFile messagesFile;
    private InventoriesFile inventoriesFile;

    @Override
    public void onEnable() {
        instance = this;

        try {

            loadFiles();
            ProvidersManager providersManager = new ProvidersManager(this);
            providersManager.load();

            String dataBaseType = getConfig().getString("settings.database.type");
            if (dataBaseType.equalsIgnoreCase("mysql")) {
                this.dataBase = new DataBase(this, DataBaseType.MySQL);
                dataBase.connect(getConfig().getString("settings.database.mysql.user"),
                                 getConfig().getString("settings.database.mysql.password"),
                                 getConfig().getString("settings.database.mysql.url"));
            } else {
                this.dataBase  = new DataBase(this, DataBaseType.SQLite);
                dataBase.connect();
            }

            socialDataBase = new SocialDataBase(this);

            socialMediaAccountLinker = new SocialMediaAccountLinker();

            sessionManager = new SessionManager(this);

            TwoFactorAuthorizationService twoFactorAuthorizationService = new TwoFactorAuthorizationService(messagesFile, sessionManager);

            accountStorage = new SocialAccountManager(socialDataBase);

            if (providersManager.useVkAPI())
                vkService = new VKService(this,
                                          messagesFile,
                                          accountStorage,
                                          twoFactorAuthorizationService,
                                          socialMediaAccountLinker);

            if (getConfig().getBoolean("settings.discord.use", false)) {
                discordService = new DiscordService(this,
                                                    messagesFile,
                                                    accountStorage,
                                                    twoFactorAuthorizationService,
                                                    socialMediaAccountLinker);
            }

            Bukkit.getPluginManager().registerEvents(new TwoFactorAuthorizationHandler(accountStorage, twoFactorAuthorizationService, messagesFile, sessionManager), this);
            Bukkit.getPluginCommand("social").setExecutor(new SocialCommand());

        } catch (Exception e) {
            getLogger().severe("Произошла ошибка при инициализации плагина: " + e.getMessage());
        }
    }

    private void loadFiles() {
        this.saveDefaultConfig();
        ConfigurationUtil.loadConfigurations(this, "messages.yml", "inventories.yml");
        FileRepository.load(this);
        messagesFile = new MessagesFile(this);
        messagesFile.load();
        inventoriesFile = new InventoriesFile(this);
        inventoriesFile.load();
    }

    @Override
    public void onDisable() {
        sessionManager.saveAll();
        discordService.getJda().shutdown();

        if (dataBase != null && dataBase.getConnector().isConnected()) {
            dataBase.getConnector().close();
        }
    }

    public static CultSocialConnect getInstance() {
        return instance;
    }

    public SocialMediaAccountLinker getSocialMediaAccountLinker() {
        return socialMediaAccountLinker;
    }

    public SocialDataBase getSocialDataBase() {
        return socialDataBase;
    }

    public VKService getVkManager() {
        return vkService;
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    public SocialAccountManager getAccountStorage() {
        return accountStorage;
    }

    public MessagesFile getMessagesFile() {
        return messagesFile;
    }

    public InventoriesFile getInventoriesFile() {
        return inventoriesFile;
    }

    public DiscordService getDiscordManager() {
        return discordService;
    }
}
