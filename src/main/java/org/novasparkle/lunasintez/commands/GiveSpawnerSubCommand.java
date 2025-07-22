package org.novasparkle.lunasintez.commands;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@SubCommand(appliedCommand = "lunasintez", commandIdentifiers = "give")
@Check(permissions = "lunasintez.give", flags = {})
public class GiveSpawnerSubCommand implements LunaCompleter {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1, 2 -> ConfigManager.send(sender, "InvalidArgs");
            case 3 -> this.giveItem(1, args, sender);
            case 4 -> this.giveItem(Integer.parseInt(args[3]), args, sender);
        }
    }

    private void giveItem(int amount, String[] args, CommandSender sender) {
        String nick = args[1];
        Player player = Bukkit.getPlayerExact(nick);
        if (player == null) {
            ConfigManager.send(sender, "noSuchPlayer", "player-%-" + nick);
            return;
        }
        try {
            String entityType = args[2];
            if (!ConfigManager.getSection("entityTypes").getKeys(false).contains(entityType)) {
                ConfigManager.send(sender, "notSintezable", "mob-%-" + entityType);
                return;
            }
            ItemStack item = SintezSpawner.getSpawnerItem(entityType);
            item.setAmount(amount);
            player.getInventory().addItem(item);
            ConfigManager.send(
                    sender, "spawnerGiven",
                    "player-%-" + player.getName(),
                    "mob-%-" + entityType,
                    "amount-%-" + amount
            );
        } catch (NullPointerException | NoSuchElementException e) {
            ConfigManager.send(sender, "noSuchMob", "mob-%-" + args[2]);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> args) {
        switch (args.size()) {
            case 1 -> {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            case 2 -> {
                return Lists.newArrayList(ConfigManager.getSection("entityTypes").getKeys(false)).stream().filter(m -> m.startsWith(args.get(1))).collect(Collectors.toList());
            }
            case 3 -> {
                return Utils.getSlotList(Lists.newArrayList("1-10")).stream().map(String::valueOf).collect(Collectors.toList());
            }
        }
        return null;
    }
}
