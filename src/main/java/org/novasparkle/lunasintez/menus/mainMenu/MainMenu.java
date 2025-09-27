package org.novasparkle.lunasintez.menus.mainMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.menus.CloseableMenu;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;


public abstract class MainMenu extends AMenu implements CloseableMenu {
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
        event.setCancelled(event.getRawSlot() == event.getSlot() || event.isShiftClick() || event.getClick().equals(ClickType.DOUBLE_CLICK));
        ItemStack item = event.getCurrentItem();
        if (item != null)
            this.itemClick(event);
    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {}

    @Override
    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(e.getRawSlots().stream().anyMatch(s -> s < this.getInventory().getSize() && s != 49));
    }

    @Override
    public boolean belongsToSpawner(SintezSpawner spawner) {
        return spawner.equals(this.spawner);
    }
}