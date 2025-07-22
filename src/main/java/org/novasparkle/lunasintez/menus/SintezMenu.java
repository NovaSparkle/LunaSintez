package org.novasparkle.lunasintez.menus;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.menus.items.SintezClickItem;
import org.novasparkle.lunasintez.menus.mainMenu.SintezMainMenu;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.SwitchItem;


public class SintezMenu extends AMenu {
    private final SintezMob mobType;
    private final Instruction instruction;
    private final Configuration configuration;
    private final int sintezSlot;
    public SintezMenu(Player player, SintezMob sintezMob) {
        super(player);
        this.configuration = new Configuration(LunaSintez.getInstance().getDataFolder(), "SintezMenu");
        this.initialize(sintezMob.getEntityType().name(), (byte) configuration.getInt("size"), configuration.getSection("decoration"), true);

        this.mobType = sintezMob;
        this.sintezSlot = this.configuration.getInt("sintezSlot");


        ConfigurationSection rootSection = this.mobType.getSintezSpawner().getConfig().getSection(String.format("mobs.%s", mobType.getEntityType().name()));
        this.instruction = new Instruction(mobType.getCategory(), rootSection);
        this.mobType.setInstruction(this.instruction);
        mobType.getSintezSpawner().saveConfig();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.addItems(
                new SwitchItem(this.configuration.getSection("items.BACK"), false, new SintezMainMenu(this.getPlayer(), this.mobType.getSintezSpawner())),
                new SwitchItem(this.configuration.getSection("items.INSTRUCTION"), false, new InstructionMenu(this.getPlayer(), this.instruction, this)),
                new SintezClickItem(this.configuration.getSection("items.SINTEZ"), this.mobType)
        );
        this.instruction.getLine().insert(this);

        this.getInventory().setItem(this.sintezSlot, new ItemStack(Material.AIR));
        this.mobType.getSintezSpawner().getConfig().save();
        this.insertAllItems();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            if (event.getRawSlot() != this.sintezSlot) {
                event.setCancelled(event.getSlot() == event.getRawSlot());
                Item item = this.findFirstItem(currentItem);
                if (item != null) item.onClick(event);
            }
        }
    }
    @Override
    public void onDrag(InventoryDragEvent event) {}
    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
