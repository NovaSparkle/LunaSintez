package org.novasparkle.lunasintez.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
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
            SintezSpawner spawner = this.mob.getSintezSpawner();

            if (instruction.getLine().checkItem(event, spawner.getLocation())) {
                inventory.setItem(49, null);
                spawner.saveConfig();
                if (instruction.getLine().isCompleted())
                    spawner.unlockMob(this.mob, (Player) event.getWhoClicked());
            }
        }
        return this;
    }
}
