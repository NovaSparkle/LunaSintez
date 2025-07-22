package org.novasparkle.lunasintez.menus.items.itemType;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.novasparkle.lunasintez.sintez.mobs.MobType;
import org.novasparkle.lunaspring.API.menus.items.Item;

@Getter
public abstract class MobItem extends Item {
    private final MobType mobType;

    public MobItem(ConfigurationSection section, MobType mobType, int slot) {
        super(section, false);
        this.mobType = mobType;
        this.mobType.getApplicator().apply(this);

        this.setSlot((byte) slot);
    }

    @Override
    public Item onClick(InventoryClickEvent event) {
        Player player = this.getMenu().getPlayer();
        this.mobType.getApplicator().accept(player, this.mobType);
        return this;
    }
}
