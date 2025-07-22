package org.novasparkle.lunasintez.menus.items;

import lombok.Getter;
import org.jetbrains.annotations.Range;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.IMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;

@Getter
public class LockedItem extends Item implements LineItem {
    private final InstructionItem instructionItem;
    public LockedItem(Configuration sintezMenu, InstructionItem instructionItem, @Range(from = 0L, to = 54L) byte slot) {
        super(sintezMenu.getMaterial("items.defaultMaterial"), slot);
        this.instructionItem = instructionItem;
        this.setAll(sintezMenu.getSection("items.LOCKED_ITEM"));
    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public void insertItem(IMenu iMenu) {
        this.insert(iMenu);
    }

    @Override
    public String getConfigIdentifier() {
        return this.instructionItem.configIdentifier();
    }

    public OpenedItem open(int amount) {
        return new OpenedItem(this.instructionItem, this.getSlot(), amount);
    }
}
