package org.novasparkle.lunasintez.menus.mainMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;


public abstract class MainMenu extends AMenu {
    protected final SintezSpawner spawner;
    protected final Configuration configuration;
    public MainMenu(Player player, SintezSpawner spawner, String menuFileName) {
        super(player);
        this.configuration = new Configuration(LunaSintez.getInstance().getDataFolder(), menuFileName);
        this.initialize(configuration.self(), true);
        this.spawner = spawner;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            event.setCancelled(event.getRawSlot() == event.getSlot());
            Item clickItem = this.findFirstItem(item);
            if (clickItem != null) clickItem.onClick(event);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {}

    @Override
    public void onDrag(InventoryDragEvent inventoryDragEvent) {}
}