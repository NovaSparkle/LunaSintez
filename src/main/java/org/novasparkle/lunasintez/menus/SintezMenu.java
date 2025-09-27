package org.novasparkle.lunasintez.menus;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.items.SintezClickItem;
import org.novasparkle.lunasintez.menus.mainMenu.SintezMainMenu;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.SwitchItem;
import org.novasparkle.lunaspring.API.util.utilities.Localization;

import java.util.List;


public class SintezMenu extends AMenu implements CloseableMenu {
    @Getter
    private final SintezMob sintezMob;
    private final Instruction instruction;
    private final Configuration configuration;
    private final int sintezSlot;
    public SintezMenu(Player player, SintezMob sintezMob) {
        super(player);
        this.configuration = new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/SintezMenu");
        this.initialize(Localization.localize(sintezMob.getEntityType()), (byte) configuration.getInt("size"), configuration.getSection("decoration"), true);

        this.sintezMob = sintezMob;
        this.sintezSlot = this.configuration.getInt("sintezSlot");

        ConfigurationSection rootSection = this.sintezMob.getSintezSpawner().getConfig().getSection(String.format("mobs.%s", this.sintezMob.getEntityType().name()));
        this.instruction = new Instruction(this.sintezMob.getCategory(), rootSection);
        this.sintezMob.setInstruction(this.instruction);
        this.sintezMob.getSintezSpawner().saveConfig();
    }

    public static SintezMenu getSintezMenu(Player player, SintezMob sintezMob) {
        List<Player> viewers = MenuManager.getActiveViewers(SintezMenu.class, true).toList();
        if (!viewers.isEmpty()) {
            for (Player viewer : viewers) {
                SintezMenu activeMenu = (SintezMenu) MenuManager.getActiveMenu(viewer);
                if (activeMenu.sintezMob.getSintezSpawner().equals(sintezMob.getSintezSpawner()) && activeMenu.sintezMob.getEntityType().equals(sintezMob.getEntityType()))
                    return (SintezMenu) activeMenu.copy(player);
            }
        }
        return new SintezMenu(player, sintezMob);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.addItems(true,
                new SwitchItem(this.configuration.getSection("items.BACK"), false, player -> new SintezMainMenu(player, this.sintezMob.getSintezSpawner())),
                new SwitchItem(this.configuration.getSection("items.INSTRUCTION"), false, player -> new InstructionMenu(player, this.instruction, this)),
                new SintezClickItem(this.configuration.getSection("items.SINTEZ"), this.sintezMob)
        );
        this.instruction.getLine().insert(this);
        this.sintezMob.getSintezSpawner().getConfig().save();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (event.getRawSlot() != sintezSlot) {
            event.setCancelled(event.getRawSlot() == event.getSlot() || event.isShiftClick() || event.getClick().equals(ClickType.DOUBLE_CLICK));
            if (currentItem != null) {
                Item item = this.findFirstItem(currentItem);
                if (item != null) item.onClick(event);
            }
        }
    }
    @Override
    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(e.getRawSlots().stream().anyMatch(s -> s < this.getInventory().getSize() && s != sintezSlot));
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        ItemStack itemStack = this.getInventory().getItem(this.sintezSlot);

        if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
            List<SintezMenu> activeMenus =
                    MenuManager.getActiveViewers(SintezMenu.class, true)
                    .map(player -> (SintezMenu) MenuManager.getActiveMenu(player))
                    .filter(menu -> menu.getSintezMob().equals(this.sintezMob) && menu.sintezMob.getSintezSpawner().equals(this.sintezMob.getSintezSpawner()))
                    .toList();

            if (activeMenus.size() == 1) {
                this.getPlayer().getInventory().addItem(itemStack);
                ConfigManager.send(this.getPlayer(), "itemsReturned", "item-%-" + itemStack.getType().name(), "amount-%-" + itemStack.getAmount());
            }
        }
    }

    @Override
    public boolean belongsToSpawner(SintezSpawner spawner) {
        return spawner.equals(this.sintezMob.getSintezSpawner());
    }

}
