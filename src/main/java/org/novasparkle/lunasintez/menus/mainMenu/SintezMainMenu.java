package org.novasparkle.lunasintez.menus.mainMenu;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.novasparkle.lunasintez.menus.items.DropItem;
import org.novasparkle.lunasintez.menus.items.itemType.SintezItem;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.SwitchItem;

import java.util.Iterator;
import java.util.List;

public class SintezMainMenu extends MainMenu {
    public SintezMainMenu(Player player, SintezSpawner spawner) {
        super(player, spawner, "MainMenu");
    }

    @SneakyThrows
    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {

        Iterator<Integer> slotIter = configuration.getIntList("items.order").iterator();

        List<SintezMob> mobList = this.spawner.getSpawnerMobs();
        mobList.forEach(mob -> this.addItems(new SintezItem(mob, slotIter.next())));

        this.addItems(
                new Item(configuration.getSection("items.clickable.INFO"), false),
                new DropItem(this.spawner, this.configuration.getSection("items.clickable.DROP")),
                new SwitchItem(configuration.getSection("items.clickable.EVOLUTIONS"), true, new EvolutionMainMenu(getPlayer(), spawner))
        );
        this.insertAllItems();
    }
}
