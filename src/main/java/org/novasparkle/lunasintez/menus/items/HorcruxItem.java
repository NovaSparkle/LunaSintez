package org.novasparkle.lunasintez.menus.items;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Range;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.menus.EvoMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.Localization;

public class HorcruxItem extends Item {
    private final String entityName;
    public HorcruxItem(@NonNull ConfigurationSection section, @Range(from = 0L, to = 54L) int slot, String entityName) {
        super(section, slot);
        this.entityName = entityName;
    }

    @Override
    public Item onClick(InventoryClickEvent event) {
        ItemStack item = event.getCursor();
        if (item != null && !item.getType().equals(Material.AIR)) {
            String nbtKey = NBTManager.getString(item, "EvoHorcrux");
            if (nbtKey != null) {
                if (nbtKey.equals(this.entityName)) {
                    ItemStack inItem = item.clone();
                    inItem.setAmount(1);
                    item.setAmount(item.getAmount() - 1);
                    event.getInventory().setItem(event.getRawSlot(), inItem);
                    ConfigManager.send(event.getWhoClicked(), "horcruxPassed");
                    EvoMenu closedMenu = (EvoMenu) this.getMenu();
                    closedMenu.decreaseHorcrux(event.getRawSlot());

                } else
                    ConfigManager.send(event.getWhoClicked(), "invalidHorcrux", "requiredType-%-" + Localization.localize(EntityType.valueOf(this.entityName)));
            }
        }
        return this;
    }
}
