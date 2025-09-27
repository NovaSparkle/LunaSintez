package org.novasparkle.lunasintez.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunaspring.API.menus.items.Item;

public class CopyItem extends Item {
    private final Instruction instruction;

    public CopyItem(ConfigurationSection section, Instruction instruction) {
        super(section, false);
        this.instruction = instruction;
    }

    @Override
    public Item onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = ((Player) event.getWhoClicked());
        instruction.getInstructionList().forEach(i -> {
            ItemStack giveItem = i.getItemStack().clone();
            if (event.isShiftClick()) {
                giveItem.setAmount(64);
            }
            player.getInventory().addItem(giveItem);
        });
        return this;
    }
}
