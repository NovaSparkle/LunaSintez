package org.novasparkle.lunasintez.menus.mainMenu;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.items.DropItem;
import org.novasparkle.lunasintez.items.itemType.SintezItem;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunasintez.sintez.mobs.status.SintezStatus;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.SwitchItem;
import org.novasparkle.lunaspring.API.util.utilities.Localization;

import java.util.Iterator;
import java.util.List;

public class SintezMainMenu extends MainMenu {
    public SintezMainMenu(Player player, SintezSpawner spawner) {
        super(player, spawner, "menus/MainMenu");
    }

    @SneakyThrows
    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {

        Iterator<Integer> slotIter = configuration.getIntList("items.order").iterator();

        List<SintezMob> mobList = this.spawner.getSpawnerMobs();
        mobList.forEach(mob -> this.addItems(true, new SintezItem(mob, slotIter.next())));

        Item infoItem = new Item(configuration.getSection("items.clickable.INFO"), false);
        infoItem.replaceLore(lore -> lore.replace("[currentMob]", Localization.localize(this.spawner.getWorldSpawner().getSpawnedType())));

        this.addItems(true, infoItem,
                new SwitchItem(configuration.getSection("items.clickable.EVOLUTIONS"), true, player -> new EvolutionMainMenu(player, this.spawner))
        );

        if (this.spawner.getSpawnerMobs().stream().filter(mob -> mob.getApplicator().equals(SintezStatus.SINTEZ)).count() >= ConfigManager.getInt("settings.sintezToDrop")) {
            this.addItems(true, new DropItem(this.spawner, this.configuration.getSection("items.clickable.DROP")));
        }
    }
}
