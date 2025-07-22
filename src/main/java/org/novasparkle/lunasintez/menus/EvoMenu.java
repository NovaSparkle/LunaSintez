package org.novasparkle.lunasintez.menus;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.menus.items.HorcruxItem;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.EvoMob;
import org.novasparkle.lunasintez.sintez.mobs.status.EvoStatus;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.util.utilities.Localization;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

public class EvoMenu extends AMenu {
    private final EvoMob evoMob;
    private final Configuration configuration;
    private int horcruxLeft;
    private ConfigurationSection horcruxSlots;
    public EvoMenu(@NonNull Player player, EvoMob evoMob) {
        super(player);
        this.evoMob = evoMob;
        this.configuration = new Configuration(LunaSintez.getInstance().getDataFolder(), "EvoMenu");

        SintezSpawner spawner = evoMob.getSintezSpawner();
        String path = String.format("evolutions.%s.horcruxLeft", this.evoMob.getEntityType().name());
        this.horcruxLeft = spawner.getConfig().getInt(path);
        this.horcruxSlots = spawner.getConfig().getSection(String.format("evolutions.%s.horcruxSlots", this.evoMob.getEntityType().name()));
        if (this.horcruxSlots == null) {
            this.generateFigure();
            spawner.saveConfig();
        }

        this.initialize(
                this.configuration.getString("title").replace("[mobType]", Localization.localize(evoMob.getEntityType())),
                ((byte) this.configuration.getInt("size")),
                this.configuration.getSection("decoration"),
                true
                );
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ConfigurationSection itemSection = this.configuration.getSection("evoFigures.item");

        for (String key : this.horcruxSlots.getKeys(false)) {
            if (!this.horcruxSlots.getBoolean(key))
                this.addItems(new HorcruxItem(itemSection, Integer.parseInt(key), this.evoMob.getEntityType().name()));
            else this.addItems(new Item(this.configuration.getSection("evoFigures.unlockedHorcrux"), Integer.parseInt(key)));
        }
        this.insertAllItems();
    }

    private void generateFigure() {
        ConfigurationSection figuresSection = this.configuration.getSection("evoFigures.figures");

        int rdmInt = LunaMath.getRandomInt(0, figuresSection.getKeys(false).size());
        List<String> keys = new ArrayList<>(figuresSection.getKeys(false));
        this.horcruxSlots = this.evoMob.getSintezSpawner().getConfig().createSection("evolutions." + this.evoMob.getEntityType().name(), "horcruxSlots");

        for (int slot : Utils.getSlotList(figuresSection.getStringList(keys.get(rdmInt)))) {
            this.horcruxSlots.set(String.valueOf(slot), false);
        }
        this.horcruxLeft = this.horcruxSlots.getKeys(false).size();
        this.setHorcruxLeft();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            event.setCancelled(event.getRawSlot() == event.getSlot());
            Item item =  this.findFirstItem(clickedItem);
            if (item != null) item.onClick(event);
        }
    }
    public void decreaseHorcrux(int slot) {
        if (this.horcruxLeft == 1) {
            this.openEvo();
        }
        this.horcruxLeft--;
        String path = String.format("evolutions.%s.horcruxSlots.%d", this.evoMob.getEntityType().name(), slot);
        this.evoMob.getSintezSpawner().getConfig().set(path, true);
        this.setHorcruxLeft();
        this.evoMob.getSintezSpawner().saveConfig();
    }

    private void setHorcruxLeft() {
        this.evoMob.getSintezSpawner().getConfig().set(String.format("evolutions.%s.horcruxLeft", this.evoMob.getEntityType().name()), this.horcruxLeft);
    }

    private void openEvo() {
        this.evoMob.setApplicator(EvoStatus.EVOLUTED);
        Configuration spawnerConfig = this.evoMob.getSintezSpawner().getConfig();
        String path = String.format("evolutions.%s", this.evoMob.getEntityType().name());
        spawnerConfig.set((path + ".status"), EvoStatus.EVOLUTED.name());
        spawnerConfig.set((path + ".horcruxLeft"), null);
        spawnerConfig.set((path + ".horcruxSlots"), null);
        ConfigManager.send(this.getPlayer(), "evoOpened");
    }
    @Override
    public void onClose(InventoryCloseEvent event) { }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }
}
