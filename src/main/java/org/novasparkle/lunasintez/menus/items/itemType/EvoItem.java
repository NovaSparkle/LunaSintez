package org.novasparkle.lunasintez.menus.items.itemType;

import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.sintez.mobs.EvoMob;

public class EvoItem extends MobItem {
    public EvoItem(EvoMob evoMob, int slot) {
        super(ConfigManager.getSection(String.format("entityTypes.%s.evolution", evoMob.getSintezEntity().name().toUpperCase())), evoMob, slot);
    }
}
