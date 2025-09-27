package org.novasparkle.lunasintez.sintez.mobs.status;

import org.bukkit.entity.Player;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.menus.SintezMenu;
import org.novasparkle.lunasintez.sintez.mobs.MobType;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.Item;

import java.util.function.BiConsumer;


public enum SintezStatus implements Applicator {
    LOCKED((player, mobType) -> ConfigManager.send(player, "SintezLocked")),
    NEXT((player, mobType) -> MenuManager.openInventory(SintezMenu.getSintezMenu(player, (SintezMob) mobType))),
    SINTEZ((player, mobType) -> {
        mobType.setMob();
        player.closeInventory();
    });

    private final BiConsumer<Player, MobType> action;

    SintezStatus(BiConsumer<Player, MobType> action) {
        this.action = action;
    }

    @Override
    public void apply(Item item) {
        Configuration configuration = new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/MainMenu");
        item.setAll(configuration.getSection(String.format("items.%s", this.name())));
        item.setGlowing(configuration.getSection(String.format("items.%s", this.name())).getBoolean("enchanted"));
    }



    @Override
    public void accept(Player player, MobType mobType) {
        this.action.accept(player, mobType);
    }
}
