package org.novasparkle.lunasintez.sintez.mobs.status;

import org.bukkit.entity.Player;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.menus.EvoMenu;
import org.novasparkle.lunasintez.sintez.mobs.EvoMob;
import org.novasparkle.lunasintez.sintez.mobs.MobType;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.Item;

import java.util.function.BiConsumer;

public enum EvoStatus implements Applicator {
    LOCKED((player, mobType) -> ConfigManager.send(player, "EvoLocked")),
    EVO_OPENED((player, mobType) -> MenuManager.openInventory(EvoMenu.getEvoMenu(player, (EvoMob) mobType))),
    EVOLUTED((player, mobType) -> {
        mobType.setMob();
        player.closeInventory();
    }),
    NO_EVO(((player, mobType) -> ConfigManager.send(player, "noEvo")));

    private final BiConsumer<Player, MobType> action;

    EvoStatus(BiConsumer<Player, MobType> action) {
        this.action = action;
    }

    @Override
    public void apply(Item item) {
        Configuration evoMainMenu = new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/EvoMainMenu");
        item.setAll(evoMainMenu.getSection(String.format("items.%s", this.name())));
        item.setGlowing(evoMainMenu.getBoolean(String.format("items.%s.enchanted", this.name())));
    }
    @Override
    public void accept(Player player, MobType mobType) {
        this.action.accept(player, mobType);
    }
}
