package org.novasparkle.lunasintez.menus;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.items.CopyItem;
import org.novasparkle.lunasintez.items.InstructionItem;
import org.novasparkle.lunasintez.sintez.Category;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.SwitchItem;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;

import java.util.stream.Collectors;

public class InstructionMenu extends AMenu implements CloseableMenu {
    private final Instruction instruction;
    private final Configuration configuration;
    private final SintezMenu fromMenu;
    public InstructionMenu(Player player, Instruction instruction, SintezMenu fromMenu) {
        super(player);
        this.instruction = instruction;
        this.fromMenu = fromMenu;
        this.configuration = new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/InstructionMenu");
        this.initialize(configuration.getString("title").replace("[mob]", ChatColor.stripColor(fromMenu.getTitle())), (byte) configuration.getInt("size"), configuration.getSection("decoration"), true);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.addItems(true,
                new SwitchItem(this.configuration.getSection("items.BACK"), false, player -> SintezMenu.getSintezMenu(player, this.fromMenu.getSintezMob()))
        );

        int i = this.configuration.getInt("startSlot");

        if (!instruction.getCategory().equals(Category.UNREAL)) {
            for (InstructionItem iItem : this.instruction.getInstructionList()) {
                Item copyItem = new Item(iItem, (byte) i);

                copyItem.setAll(this.configuration.getSection("items.instructionItem"));
                copyItem.setSlot((byte) i);
                copyItem.insert(this);
                i++;
            }
        }

        if (getPlayer().hasPermission("showLine")) {
            ConfigurationSection section = this.configuration.getSection("items.SHOW");
            this.addItems(true,
                    (Item) new Item(section, false)
                            .setLore(this.instruction.getLine().getItems().stream()
                                    .map(li -> ColorManager.color(section.getString("color") + li.getConfigIdentifier()))
                                    .collect(Collectors.toList())),
                    new CopyItem(this.configuration.getSection("items.COPY"), this.instruction)
            );
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(event.getRawSlot() == event.getSlot() || event.isShiftClick() || event.getClick().equals(ClickType.DOUBLE_CLICK));
        if (currentItem != null) {
            this.itemClick(currentItem, event);
        }
    }

    @Override
    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(e.getRawSlots().stream().anyMatch(s -> s < this.getInventory().getSize() && s != 49));
    }
    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public boolean belongsToSpawner(SintezSpawner spawner) {
        return spawner.equals(this.fromMenu.getSintezMob().getSintezSpawner());
    }

}
