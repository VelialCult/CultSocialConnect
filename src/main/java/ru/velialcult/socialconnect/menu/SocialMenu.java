package ru.velialcult.socialconnect.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.library.bukkit.inventory.PlayerInputHandler;
import ru.velialcult.library.bukkit.utils.InventoryUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.socialconnect.CultSocialConnect;
import ru.velialcult.socialconnect.file.MessagesFile;
import ru.velialcult.socialconnect.link.AccountLinkingSession;
import ru.velialcult.socialconnect.link.SocialMediaAccountLinker;
import ru.velialcult.socialconnect.social.SocialAccountManager;
import ru.velialcult.socialconnect.social.types.SocialType;
import ru.velialcult.socialconnect.user.SocialUser;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Map;

/**
 * Written by Nilsson
 * 06.06.2024
 */
public class SocialMenu {

    public static void generateInventory(Player player) {

        FileConfiguration config = CultSocialConnect.getInstance().getInventoriesFile().getConfig();
        MessagesFile messagesFile = CultSocialConnect.getInstance().getMessagesFile();
        SocialAccountManager socialAccountManager = CultSocialConnect.getInstance().getAccountStorage();
        SocialMediaAccountLinker socialMediaAccountLinker = CultSocialConnect.getInstance().getSocialMediaAccountLinker();
        SocialUser socialUser = socialAccountManager.getSocialUser(player.getUniqueId());

        char vkChar = config.getString("inventories.main-menu.items.vk-connect.symbol").charAt(0);
        AutoUpdateItem vk = new AutoUpdateItem(20, () ->
                s -> {
            if (socialUser.hasSocialAccount(SocialType.VK) && socialUser.getMainSocialAccount() == socialUser.getSocialAccount(SocialType.VK)) {
                return InventoryUtil.createItem(config, "inventories.main-menu.items.main");
            } else if (socialUser.hasSocialAccount(SocialType.VK)) {
                return InventoryUtil.createItem(config, "inventories.main-menu.items.vk-connect");
            } else if (socialMediaAccountLinker.hasRequestLinkAccount(player.getUniqueId(), SocialType.VK)) {
                return InventoryUtil.createItem(config, "inventories.main-menu.items.wait-code",
                                                new ReplaceData("{social}", SocialType.VK));
            } else {
                return InventoryUtil.createItem(config, "inventories.main-menu.items.not-connect",
                                                       new ReplaceData("{social}", SocialType.VK));
            }
        }) {
            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                if (!socialUser.hasSocialAccount(SocialType.VK)) return;
                if (socialUser.hasSocialAccount(SocialType.VK) && socialUser.getMainSocialAccount() == socialUser.getSocialAccount(SocialType.VK)) return;
                if (socialUser.hasSocialAccount(SocialType.VK) && socialUser.getMainSocialAccount() != socialUser.getSocialAccount(SocialType.VK)) {
                    socialAccountManager.setMainAccount(socialUser, SocialType.VK);
                    VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.set-main-account",
                                                                                                                 new ReplaceData("{social}", SocialType.VK.getTranslationText())));
                    return;
                }
                if (socialMediaAccountLinker.hasRequestLinkAccount(player.getUniqueId(), SocialType.VK)) {
                    player.closeInventory();
                    VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString(
                            "messages.minecraft.connect.enter-code",
                            new ReplaceData("{social}", SocialType.VK.getTranslationText())
                    ));
                    PlayerInputHandler.addPlayer(player, (str) -> {
                        if (socialMediaAccountLinker.isValidRequestLinkCode(player.getUniqueId(), str, SocialType.VK)) {
                            AccountLinkingSession accountLinkingSession = socialMediaAccountLinker.getAccountLinkingSessionByUUID(player.getUniqueId());
                            if (accountLinkingSession != null) {
                                VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString(
                                        "messages.minecraft.connect.success",
                                        new ReplaceData("{social}", SocialType.VK.getTranslationText())
                                ));
                                socialAccountManager.linkSocial(player.getUniqueId(), accountLinkingSession.getUserUrl(), SocialType.VK);
                                socialMediaAccountLinker.removeAccountLinkingSession(player.getUniqueId(), SocialType.VK);
                                generateInventory(player);
                            }
                        }
                    });
                }
            }
        };

        char discordChar = config.getString("inventories.main-menu.items.discord-connect.symbol").charAt(0);
        AutoUpdateItem discord = new AutoUpdateItem(20, () ->
                s -> {
                    if (socialUser.hasSocialAccount(SocialType.DISCORD) && socialUser.getMainSocialAccount() == socialUser.getSocialAccount(SocialType.DISCORD)) {
                        return InventoryUtil.createItem(config, "inventories.main-menu.items.main");
                    } else if (socialUser.hasSocialAccount(SocialType.DISCORD)) {
                        return InventoryUtil.createItem(config, "inventories.main-menu.items.discord-connect");
                    } else if (socialMediaAccountLinker.hasRequestLinkAccount(player.getUniqueId(), SocialType.DISCORD)) {
                        return InventoryUtil.createItem(config, "inventories.main-menu.items.wait-code",
                                                        new ReplaceData("{social}", SocialType.DISCORD));
                    } else {
                        return InventoryUtil.createItem(config, "inventories.main-menu.items.not-connect",
                                                        new ReplaceData("{social}", SocialType.DISCORD));
                    }
                }) {
            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                if (!socialUser.hasSocialAccount(SocialType.DISCORD)) return;
                if (socialUser.hasSocialAccount(SocialType.DISCORD) && socialUser.getMainSocialAccount() == socialUser.getSocialAccount(SocialType.DISCORD)) return;
                if (socialUser.hasSocialAccount(SocialType.DISCORD) && socialUser.getMainSocialAccount() != socialUser.getSocialAccount(SocialType.DISCORD)) {
                    socialAccountManager.setMainAccount(socialUser, SocialType.DISCORD);
                    VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.set-main-account",
                                                                                                                 new ReplaceData("{social}", SocialType.DISCORD.getTranslationText())));
                    return;
                }
                if (socialMediaAccountLinker.hasRequestLinkAccount(player.getUniqueId(), SocialType.DISCORD)) {
                    player.closeInventory();
                    VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.connect.enter-code",
                                                                                                                 new ReplaceData("{social}", SocialType.DISCORD.getTranslationText())));
                }
                PlayerInputHandler.addPlayer(player, (str) -> {
                    if (socialMediaAccountLinker.isValidRequestLinkCode(player.getUniqueId(), str, SocialType.DISCORD)) {
                        AccountLinkingSession accountLinkingSession = socialMediaAccountLinker.getAccountLinkingSessionByUUID(player.getUniqueId());
                        if (accountLinkingSession != null) {
                            VersionAdapter.MessageUtils().sendMessage(player, messagesFile.getFileOperations().getString("messages.minecraft.connect.success",
                                                                                                                         new ReplaceData("{social}", SocialType.DISCORD.getTranslationText())));
                            socialAccountManager.linkSocial(player.getUniqueId(), accountLinkingSession.getUserUrl(), SocialType.DISCORD);
                            socialMediaAccountLinker.removeAccountLinkingSession(player.getUniqueId(), SocialType.DISCORD);
                        }
                    }
                });
            }
        };

        String[] structure = config.getList("inventories.main-menu.structure").toArray(new String[0]);

        Gui.Builder.Normal builder = Gui.normal()
                .setStructure(structure)
                .addIngredient(vkChar, vk)
                .addIngredient(discordChar, discord);

        Map<Character, SuppliedItem> customItemList = InventoryUtil.createItems(config,
                                                                                "inventories.main-menu.items",
                                                                                (event, path) -> {
                                                                                    List<String> commands = config.getStringList(path + ".actionOnClick");
                                                                                    for (String command : commands) {
                                                                                        if (command.startsWith("[message]")) {
                                                                                            String message = command.replace("[message]", "");
                                                                                            VersionAdapter.MessageUtils().sendMessage(player, message);
                                                                                        }

                                                                                        if (command.startsWith("[execute]")) {
                                                                                            String executeCommand = command.replace("[execute]", "");
                                                                                            Bukkit.dispatchCommand(player, executeCommand);
                                                                                        }

                                                                                        if (command.equals("[close]")) {
                                                                                            player.closeInventory();
                                                                                        }
                                                                                    }
                                                                                });

        InventoryUtil.setItems(builder, customItemList);

        Window window = Window.single()
                .setViewer(player)
                .setTitle(VersionAdapter.TextUtil().colorize(config.getString("inventories.main-menu.title")))
                .setGui(builder.build())
                .build();
        window.open();
    }
}
