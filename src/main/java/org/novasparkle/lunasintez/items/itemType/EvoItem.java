package org.novasparkle.lunasintez.items.itemType;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.sintez.mobs.EvoMob;
import org.novasparkle.lunasintez.sintez.mobs.status.EvoStatus;
import org.novasparkle.lunaspring.API.menus.items.Item;

public class EvoItem extends Item {
    private final EvoMob evoMob;
    public EvoItem(EvoMob evoMob, int slot) {
        super(Material.STONE);
        this.evoMob = evoMob;
        if (!evoMob.getApplicator().equals(EvoStatus.NO_EVO) && !evoMob.getApplicator().equals(EvoStatus.LOCKED)) {
            this.setAll(ConfigManager.getSection(String.format("entityTypes.%s.evolution", this.evoMob.getSintezEntity().name().toUpperCase())));
        }

        this.evoMob.getApplicator().apply(this);
        this.setSlot((byte) slot);
    }

    @Override
    public Item onClick(InventoryClickEvent event) {
        this.evoMob.getApplicator().accept(((Player) event.getWhoClicked()), this.evoMob);
        return this;
    }
}
