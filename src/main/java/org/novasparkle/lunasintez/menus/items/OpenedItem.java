package org.novasparkle.lunasintez.menus.items;

import lombok.Getter;
import org.jetbrains.annotations.Range;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.IMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;

@Getter
public class OpenedItem extends Item implements LineItem {
    private final InstructionItem instructionItem;
    public OpenedItem(InstructionItem instructionItem, @Range(from = 0L, to = 54L) byte slot, Integer amount) {
        super(instructionItem, slot);
        this.instructionItem = instructionItem;
        this.setAmount(amount);
        this.setAll(new Configuration(LunaSintez.getInstance().getDataFolder(), "SintezMenu").getSection("items.OPENED_ITEM"));
    }

    public void increase(int amount) {
        this.setAmount(this.getAmount() + amount);
        this.insert();
    }

    @Override
    public boolean isOpened() {
        return true;
    }

    @Override
    public void insertItem(IMenu iMenu) {
        this.insert(iMenu);
    }

    @Override
    public String getConfigIdentifier() {
        return this.instructionItem.configIdentifier();
    }

    @Override
    public NonMenuItem decrease() {
        this.setAmount(this.getAmount() - 1);
        this.insert();
        return null;
    }
}
