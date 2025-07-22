package org.novasparkle.lunasintez.menus.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunaspring.API.menus.items.Item;


public class SintezClickItem extends Item {
    private final SintezMob mob;
    public SintezClickItem(ConfigurationSection section, SintezMob mob) {
        super(section, false);
        this.mob = mob;
    }
    @Override
    public Item onClick(InventoryClickEvent event) {
        Inventory inventory = this.getMenu().getInventory();
        ItemStack sintezItem = inventory.getItem(49);
        if (sintezItem != null) {
            Instruction instruction = this.mob.getInstruction();
            if (instruction.getLine().checkItem(sintezItem, this.mob.getSintezSpawner().getLocation())) {
                inventory.setItem(49, null);
                if (instruction.getLine().isCompleted()) {
                    this.mob.unlockMobInConfig();
                    this.mob.getSintezSpawner().addCompletedMob();
                } else
                    this.mob.getSintezSpawner().saveConfig();
            }
        }
        return this;
    }
}
