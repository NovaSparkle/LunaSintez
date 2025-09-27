package org.novasparkle.lunasintez.items;

import lombok.Getter;
import org.jetbrains.annotations.Range;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.ItemListMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;

import java.util.ArrayList;

@Getter
public class OpenedItem extends Item implements LineItem {
    private final InstructionItem instructionItem;
    public OpenedItem(InstructionItem instructionItem, @Range(from = 0L, to = 54L) byte slot, Integer amount) {
        super(instructionItem, slot);
        this.instructionItem = instructionItem;

        this.setAmount(amount);
        this.setAll(new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/SintezMenu").getSection("items.OPENED_ITEM"));
        this.setLore(new ArrayList<>(this.getDefaultLore()));
        this.replaceLore(l -> l.replace("[amount]", String.valueOf(this.getAmount())));
        this.setSlot(slot);
    }

    public void increase(int amount) {
        this.setAmount(this.getAmount() + amount);
        this.setLore(new ArrayList<>(this.getDefaultLore()));
        this.replaceLore(l -> l.replace("[amount]", String.valueOf(this.getAmount())));
        this.setGlowing(this.getAmount() > 64);
        this.insert();
    }

    @Override
    public boolean isOpened() {
        return true;
    }

    @Override
    public void insertItem(ItemListMenu iMenu) {
        this.insert(iMenu);
    }

    @Override
    public String getConfigIdentifier() {
        return this.instructionItem.configIdentifier();
    }

    @Override
    public NonMenuItem decrease() {
        this.setAmount(this.getAmount() - 1);
        this.setLore(new ArrayList<>(this.getDefaultLore()));
        this.replaceLore(l -> l.replace("[amount]", String.valueOf(this.getAmount())));
        this.setGlowing(this.getAmount() > 64);
        this.insert();
        return null;
    }
}
