package org.novasparkle.lunasintez.sintez;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.items.InstructionItem;
import org.novasparkle.lunasintez.items.LineItem;
import org.novasparkle.lunasintez.items.LockedItem;
import org.novasparkle.lunasintez.items.OpenedItem;
import org.novasparkle.lunasintez.particles.ParticleTask;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Line implements Iterable<LineItem> {
    @Getter
    private final List<LineItem> items;
    private final ConfigurationSection mobSection;

    public Line(List<InstructionItem> instructionList, Category category, ConfigurationSection mobSection) {
        this.mobSection = mobSection;
        this.items = new ArrayList<>();
        int i = 0;
        List<InstructionItem> usedMaterials = new ArrayList<>();
        Configuration sintezMenu = new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/SintezMenu");
        Iterator<Integer> orderIter = Utils.getSlotList(sintezMenu.getStringList(String.format("lockedQueue.%s", category.name()))).iterator();

        while (i++ < category.getLineSize()) {
            InstructionItem iItem;
            do {
                iItem = instructionList.get(ThreadLocalRandom.current().nextInt(instructionList.size()));
            } while (usedMaterials.contains(iItem));
            usedMaterials.add(iItem);
            this.items.add(new LockedItem(new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/SintezMenu"), iItem, orderIter.next().byteValue()));
        }
    }
    public Line(ConfigurationSection section, Category category) {
        this.mobSection = section;
        this.items = new ArrayList<>();
        Configuration sintezMenu =  new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/SintezMenu");
        Iterator<Integer> orderIter = Utils.getSlotList(sintezMenu.getStringList(String.format("lockedQueue.%s", category.name()))).iterator();
        AtomicReference<String> sKey = new AtomicReference<>();
        try {
            Objects.requireNonNull(section.getConfigurationSection("line")).getValues(false).forEach((key, value) -> {
                sKey.set(key);
                int amount = (int) value;
                LineItem lineItem;
                InstructionItem instructionItem = InstructionItem.getFromIdentifier(category, key);
                if (amount > 0)
                     lineItem = new OpenedItem(instructionItem, orderIter.next().byteValue(), amount);
                else lineItem = new LockedItem(sintezMenu, instructionItem, orderIter.next().byteValue());
                this.items.add(lineItem);

            });
        } catch (ClassCastException e) {
            LunaSintez.getInstance().warning(String.format("Ошибка конфигурации %s: В предмете %s.line.%s указан неверный тип данных, необходим Integer. Если вы не меняли данные конфигурации или не помните значение, замените его на 0 и переоткройте рассадник!", Objects.requireNonNull(section.getRoot()).getName(), section.getName(), sKey.get()));
            throw e;
        }
    }

    private LockedItem getNext() {
        return (LockedItem) this.items.stream().filter(lineItem -> !lineItem.isOpened()).findFirst().orElse(null);
    }

    public List<OpenedItem> getOpened() {
        return this.items.stream().filter(LineItem::isOpened).map(lineItem -> ((OpenedItem) lineItem)).collect(Collectors.toList());
    }
    public boolean checkItem(InventoryClickEvent event, Location location) {
        ItemStack itemStack = event.getInventory().getItem(49);
        assert itemStack != null;

        LockedItem nextItem = this.getNext();
        if (nextItem != null) {
            for (OpenedItem openedItem : this.getOpened()) {
                if (this.compare(openedItem.getInstructionItem(), itemStack)) {
                    openedItem.increase(itemStack.getAmount());
                    this.save(openedItem);
                    return true;

                } else if (openedItem.getAmount() == 1) {
                    ConfigManager.send(event.getWhoClicked(), "notEnoughItems", "slot-%-" + (this.getItems().indexOf(openedItem) + 1));
                    return false;
                }
            }
            if (this.compare(nextItem.getInstructionItem(), itemStack)) {

                OpenedItem openedItem = this.openItem(itemStack.getAmount());
                assert openedItem != null;
                this.save(openedItem);
                new ParticleTask(location, ConfigManager.getSection("settings.particles.onItemOpened")).runTaskAsynchronously(LunaSintez.getInstance());
                ConfigManager.send(event.getWhoClicked(), "openedItem");
            } else {
                this.getOpened().forEach(item -> {
                    item.decrease();
                    this.save(item);
                });
            }
        } else return false;
        return true;
    }
    private boolean compare(NonMenuItem item, ItemStack stack) {
        return item.getMaterial().equals(stack.getType()) && NBTManager.isSimilar(item.getItemStack(), stack);
    }

    private OpenedItem openItem(int amount) {
        ListIterator<LineItem> iter = this.getItems().listIterator();
        while (iter.hasNext()) {
            LineItem lineItem  = iter.next();
            if (lineItem instanceof LockedItem lockedItem) {
                OpenedItem openedItem = lockedItem.open(amount);
                iter.set(openedItem);
                openedItem.insert(lockedItem.getMenu());
                return openedItem;
            }
        }
        return null;
    }

    private void save(OpenedItem openedItem) {
        this.mobSection.set(String.format("line.%s", openedItem.getConfigIdentifier()), openedItem.getAmount());

    }

    public void saveAll() {
        this.items.forEach(l -> this.mobSection.set(String.format("line.%s", l.getConfigIdentifier()), l.isOpened() ? ((OpenedItem) l).getAmount() : 0));
    }

    public boolean isCompleted() {
        return this.getOpened().size() == this.items.size();
    }

    @Override
    public void forEach(Consumer<? super LineItem> action) {
        this.items.forEach(action);
    }

    @NotNull
    @Override
    public Iterator<LineItem> iterator() {
        return this.items.iterator();
    }

    public void insert(AMenu aMenu) {
        this.forEach(i -> i.insertItem(aMenu));
    }


}
