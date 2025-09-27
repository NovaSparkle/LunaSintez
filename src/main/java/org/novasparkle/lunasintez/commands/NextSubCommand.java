package org.novasparkle.lunasintez.commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;

@SubCommand(appliedCommand = "lunasintez", commandIdentifiers = "next")
@Check(permissions = "lunasintez.next", flags = {NoArgCommand.AccessFlag.PLAYER_ONLY})
public class NextSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        if (sender instanceof Player player) {
            Block targetBlock = player.getTargetBlock(ConfigManager.getInt("settings.maxDistance"));
            if (targetBlock == null) {
                ConfigManager.send(player, "noBlock");
            } else if (!targetBlock.getType().equals(Material.SPAWNER)) {
                ConfigManager.send(player, "noSpawner", "block-%-" + targetBlock.getType().name());
            } else {
                SintezSpawner spawner = new SintezSpawner(targetBlock.getLocation());
                if (spawner.getConfig().getSection("mobs") == null) {
                    ConfigManager.send(sender, "needOpen");
                    return;
                }
                spawner.unlockMob(player);
                ConfigManager.send(player, "nextMobOpened");
            }
        }
    }
}
