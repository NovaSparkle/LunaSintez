package org.novasparkle.lunasintez.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;

import java.util.Objects;

@SubCommand(appliedCommand = "lunasintez", commandIdentifiers = {"reload", "rl"})
@Check(permissions = "lunasintez.reload", flags = {})
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        ConfigManager.reload();
        sender.sendMessage(Objects.requireNonNull(ConfigManager.getString("messages.reloaded")));
    }
}
