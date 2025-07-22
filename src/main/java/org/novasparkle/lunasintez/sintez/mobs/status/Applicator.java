package org.novasparkle.lunasintez.sintez.mobs.status;

import org.bukkit.entity.Player;
import org.novasparkle.lunasintez.sintez.mobs.MobType;
import org.novasparkle.lunaspring.API.menus.items.Item;

import java.util.function.BiConsumer;

public interface Applicator extends BiConsumer<Player, MobType> {
    void apply(Item item);
}
