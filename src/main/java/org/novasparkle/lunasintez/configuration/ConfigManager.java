package org.novasparkle.lunasintez.configuration;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.menus.items.InstructionItem;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;

import java.util.List;
import java.util.stream.Collectors;

public final class ConfigManager {
    private final static IConfig config;
    static {
        config = new IConfig(LunaSintez.getInstance());
        System.out.println('+');
    }

    public static int getInt(String path) {
        return config.getInt(path);
    }


    public static void reload() {
        config.reload(LunaSintez.getInstance());
    }
    public static ConfigurationSection getSection(String path) {
        if (path.isEmpty()) return config.self();
        return config.getSection(path);
    }

    public static String getString(String path) {
        String text = config.getString(path);
        if (text != null)
            return ColorManager.color(text);
        return null;
    }

    public static List<InstructionItem> getItems(String path) {
        return config.getSection(path).getValues(false).values().stream().map(s -> new InstructionItem((ConfigurationSection) s)).collect(Collectors.toList());
    }
    public static void send(CommandSender sender, String id, String... replacements) {
        config.sendMessage(sender, id, replacements);
    }
}
