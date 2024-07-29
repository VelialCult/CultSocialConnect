package ru.velialcult.socialconnect.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.library.bukkit.utils.PlayerUtil;
import ru.velialcult.socialconnect.menu.SocialMenu;

/**
 * Written by Nilsson
 * 08.06.2024
 */
public class SocialCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (PlayerUtil.senderIsPlayer(sender)) {
            Player player = (Player) sender;
            SocialMenu.generateInventory(player);
        }
        return false;
    }
}
