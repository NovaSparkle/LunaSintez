package org.novasparkle.lunasintez.menus.items.itemType;

import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.sintez.mobs.MobType;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;

public class SintezItem extends MobItem {
    public SintezItem(MobType mobType, int slot) {
        super(ConfigManager.getSection(String.format("entityTypes.%s", mobType.getEntityType().name().toUpperCase())), mobType, slot);
        this.replaceLore(lr -> lr.replace("[category]", ((SintezMob) mobType).getCategory().getName()));
    }
}
