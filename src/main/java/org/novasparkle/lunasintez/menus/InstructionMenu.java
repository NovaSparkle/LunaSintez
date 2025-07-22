package org.novasparkle.lunasintez.menus;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.menus.items.CopyItem;
import org.novasparkle.lunasintez.menus.items.InstructionItem;
import org.novasparkle.lunasintez.sintez.Category;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.SwitchItem;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;

import java.util.stream.Collectors;

public class InstructionMenu extends AMenu {
    private final Instruction instruction;
    private final Configuration configuration;
    private final SintezMenu fromMenu;
    public InstructionMenu(Player player, Instruction instruction, SintezMenu fromMenu) {
        super(player);
        this.instruction = instruction;
        this.fromMenu = fromMenu;
        this.configuration = new Configuration(LunaSintez.getInstance().getDataFolder(), "InstructionMenu");
        this.initialize(configuration.getString("title").replace("[mob]", ChatColor.stripColor(fromMenu.getTitle())), (byte) configuration.getInt("size"), configuration.getSection("decoration"), true);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.addItems(
                new SwitchItem(this.configuration.getSection("items.BACK"), false, this.fromMenu)
        );

        int i = this.configuration.getInt("startSlot");

        if (!instruction.getCategory().equals(Category.UNREAL)) {
            for (InstructionItem iItem : this.instruction.getInstructionList()) {
                this.getInventory().setItem(i, iItem.getItemStack());
                i++;
            }
        }

        if (getPlayer().hasPermission("showLine")) {
            ConfigurationSection section = this.configuration.getSection("items.SHOW");
            this.addItems(
                    (Item) new Item(section, false)
                            .setLore(this.instruction.getLine().getItems().stream()
                                    .map(li -> ColorManager.color(section.getString("color") + li.getConfigIdentifier()))
                                    .collect(Collectors.toList())),
                    new CopyItem(this.configuration.getSection("items.COPY"), this.instruction)
            );
        }

        this.insertAllItems();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();

        if (currentItem != null) {
            event.setCancelled(event.getRawSlot() == event.getSlot());
            Item item = this.findFirstItem(currentItem);

            if (item != null) item.onClick(event);
        }
    }

    @Override
    public void onDrag(InventoryDragEvent inventoryDragEvent) {}
    @Override
    public void onClose(InventoryCloseEvent event) {}
}
