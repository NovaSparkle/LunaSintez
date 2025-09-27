package org.novasparkle.lunasintez.menus.mainMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.items.DropItem;
import org.novasparkle.lunasintez.items.itemType.EvoItem;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunasintez.sintez.mobs.status.SintezStatus;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.SwitchItem;
import org.novasparkle.lunaspring.API.util.utilities.Localization;

import java.util.Iterator;
import java.util.List;

public class EvolutionMainMenu extends MainMenu {
    public EvolutionMainMenu(Player player, SintezSpawner spawner) {
        super(player, spawner, "menus/EvoMainMenu");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        Item infoItem = new Item(configuration.getSection("items.clickable.INFO"), false);
        infoItem.replaceLore(lore -> lore.replace("[currentMob]", Localization.localize(this.spawner.getWorldSpawner().getSpawnedType())));


        this.addItems(true, infoItem,
                new SwitchItem(this.configuration.getSection("items.clickable.SINTEZ"), true, player -> new SintezMainMenu(player, spawner))
        );

        if (this.spawner.getSpawnerMobs().stream().filter(mob -> mob.getApplicator().equals(SintezStatus.SINTEZ)).count() >= ConfigManager.getInt("settings.sintezToDrop")) {
            this.addItems(true, new DropItem(this.spawner, this.configuration.getSection("items.clickable.DROP")));
        }

        Iterator<Integer> order = this.configuration.getIntList("items.order").iterator();

        List<SintezMob> sintezMobs = this.spawner.getSpawnerMobs();

        for (SintezMob sintezMob : sintezMobs) {
            this.addItems(true, new EvoItem(sintezMob.toEvoMob(), order.next()));
        }
    }
}
