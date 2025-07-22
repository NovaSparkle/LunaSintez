package org.novasparkle.lunasintez.menus.mainMenu;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.novasparkle.lunasintez.menus.items.DropItem;
import org.novasparkle.lunasintez.menus.items.itemType.EvoItem;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.SwitchItem;

import java.util.Iterator;
import java.util.List;

public class EvolutionMainMenu extends MainMenu {
    public EvolutionMainMenu(@NonNull Player player, SintezSpawner spawner) {
        super(player, spawner, "EvoMainMenu");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.addItems(
                new SwitchItem(this.configuration.getSection("items.clickable.SINTEZ"), true, new SintezMainMenu(getPlayer(), spawner)),
                new Item(this.configuration.getSection("items.clickable.INFO"), false),
                new DropItem(spawner, this.configuration.getSection("items.clickable.DROP"))
        );


        Iterator<Integer> order = this.configuration.getIntList("items.order").iterator();

        List<SintezMob> sintezMobs = this.spawner.getSpawnerMobs();

        for (SintezMob sintezMob : sintezMobs) {
            this.addItems(new EvoItem(sintezMob.getEvoMob(), order.next()));
        }
        this.insertAllItems();
    }
}
