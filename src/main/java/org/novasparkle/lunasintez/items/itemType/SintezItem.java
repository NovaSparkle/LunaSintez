package org.novasparkle.lunasintez.items.itemType;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunaspring.API.menus.items.Item;

public class SintezItem extends Item {
    private final SintezMob sintezMob;
    public SintezItem(SintezMob sintezMob, int slot) {
        super(ConfigManager.getSection(String.format("entityTypes.%s", sintezMob.getEntityType().name().toUpperCase())), slot);
        this.sintezMob = sintezMob;
        sintezMob.getApplicator().apply(this);
        this.replaceLore(lr -> lr.replace("[category]", sintezMob.getCategory().getName()));
        this.setSlot((byte) slot);
    }

    @Override
    public Item onClick(InventoryClickEvent event) {
        this.sintezMob.getApplicator().accept(((Player) event.getWhoClicked()), this.sintezMob);
        return this;
    }
}
