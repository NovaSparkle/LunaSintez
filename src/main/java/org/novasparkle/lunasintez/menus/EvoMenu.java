package org.novasparkle.lunasintez.menus;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.items.HorcruxItem;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.EvoMob;
import org.novasparkle.lunasintez.sintez.mobs.status.EvoStatus;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.util.utilities.Localization;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EvoMenu extends AMenu implements CloseableMenu {
    private final EvoMob evoMob;
    @Getter
    private final Configuration configuration;
    private final AtomicInteger horcruxLeft;
    private ConfigurationSection horcruxSlots;

    public EvoMenu(@NonNull Player player, EvoMob evoMob) {
        super(player);
        this.evoMob = evoMob;
        this.configuration = new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/EvoMenu");
        this.horcruxLeft = new AtomicInteger();

        SintezSpawner spawner = evoMob.getSintezSpawner();
        String path = String.format("evolutions.%s.horcruxLeft", this.evoMob.getSintezEntity().name());
        this.horcruxLeft.set(spawner.getConfig().getInt(path));
        this.horcruxSlots = spawner.getConfig().getSection(String.format("evolutions.%s.horcruxSlots", this.evoMob.getSintezEntity().name()));

        if (this.horcruxSlots == null) {
            this.generateFigure();
            spawner.saveConfig();
        }

        this.initialize(
                this.configuration.getString("title").replace("[mobType]", Localization.localize(evoMob.getEntityType())),
                ((byte) this.configuration.getInt("size")),
                this.configuration.getSection("decoration"), true
        );
        this.setClickCooldown(100);
    }


    public static EvoMenu getEvoMenu(Player player, EvoMob evoMob) {
        List<Player> viewers = MenuManager.getActiveViewers(EvoMenu.class, true).toList();

        if (!viewers.isEmpty()) {
            for (Player viewer : viewers) {
                EvoMenu activeMenu = (EvoMenu) MenuManager.getActiveMenu(viewer);
                if (activeMenu.evoMob.getSintezSpawner().equals(evoMob.getSintezSpawner()) && activeMenu.evoMob.equals(evoMob))
                    return (EvoMenu) activeMenu.copy(player);
            }
        }
        return new EvoMenu(player, evoMob);

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ConfigurationSection itemSection = this.configuration.getSection("evoFigures.item");

        for (String key : this.horcruxSlots.getKeys(false)) {
            if (!this.horcruxSlots.getBoolean(key))
                this.addItems(true, new HorcruxItem(itemSection, Integer.parseInt(key), this.evoMob.getEntityType().name()));
            else
                this.addItems(true, new Item(this.configuration.getSection("evoFigures.unlockedHorcrux"), Integer.parseInt(key)));
        }
    }

    private void generateFigure() {
        ConfigurationSection figuresSection = this.configuration.getSection("evoFigures.figures");

        int rdmInt = LunaMath.getRandomInt(0, figuresSection.getKeys(false).size());
        List<String> keys = new ArrayList<>(figuresSection.getKeys(false));
        this.horcruxSlots = this.evoMob.getSintezSpawner().getConfig().createSection("evolutions." + this.evoMob.getSintezEntity().name(), "horcruxSlots");

        for (int slot : Utils.getSlotList(figuresSection.getStringList(keys.get(rdmInt)))) {
            this.horcruxSlots.set(String.valueOf(slot), false);
        }
        this.horcruxLeft.set(this.horcruxSlots.getKeys(false).size());
        this.setHorcruxLeft();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(event.getRawSlot() == event.getSlot() || event.isShiftClick() || event.getClick().equals(ClickType.DOUBLE_CLICK));
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {

            Item item =  this.findFirstItem(clickedItem);
            if (item != null) item.onClick(event);
        }
    }
    public void decreaseHorcrux(int slot) {
        if (this.horcruxLeft.get() == 1) {
            this.openEvo();
            return;
        }
        this.horcruxLeft.decrementAndGet();
        String path = String.format("evolutions.%s.horcruxSlots.%d", this.evoMob.getSintezEntity().name(), slot);
        this.evoMob.getSintezSpawner().getConfig().set(path, true);
        this.setHorcruxLeft();
        this.evoMob.getSintezSpawner().saveConfig();
    }

    private void setHorcruxLeft() {
        this.evoMob.getSintezSpawner().getConfig().set(String.format("evolutions.%s.horcruxLeft", this.evoMob.getSintezEntity().name()), this.horcruxLeft);
    }

    private void openEvo() {
        this.evoMob.setApplicator(EvoStatus.EVOLUTED);
        Configuration spawnerConfig = this.evoMob.getSintezSpawner().getConfig();
        String path = String.format("evolutions.%s", this.evoMob.getSintezEntity().name());
        spawnerConfig.set((path + ".status"), EvoStatus.EVOLUTED.name());
        ConfigManager.send(this.getPlayer(), "evoOpened");
        spawnerConfig.save();
    }
    @Override
    public void onClose(InventoryCloseEvent event) { }

    @Override
    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(e.getRawSlots().stream().anyMatch(s -> s < this.getInventory().getSize() && s != 49));
    }

    @Override
    public boolean belongsToSpawner(SintezSpawner spawner) {
        return spawner.equals(this.evoMob.getSintezSpawner());
    }
}
