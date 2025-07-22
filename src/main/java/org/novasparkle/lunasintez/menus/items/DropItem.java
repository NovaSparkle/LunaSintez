package org.novasparkle.lunasintez.menus.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunaspring.API.menus.items.Item;


public class DropItem extends Item {
    private final SintezSpawner spawner;
    public DropItem(SintezSpawner spawner, ConfigurationSection section) {
        super(section, true);
        this.spawner = spawner;
    }

    @Override
    public Item onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        this.spawner.onDrop();
        event.getWhoClicked().closeInventory();
        return this;
    }
}
