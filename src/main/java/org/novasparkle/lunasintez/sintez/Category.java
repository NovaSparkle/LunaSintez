package org.novasparkle.lunasintez.sintez;

import lombok.Getter;
import org.bukkit.Material;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.items.InstructionItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum Category {
    COMMON(),
    RARE(),
    EPIC(),
    UNREAL(Arrays.stream(Material.values()).filter(m -> !m.name().endsWith("_SPAWN_EGG") && m.isItem() && !m.isAir()).map(InstructionItem::new).collect(Collectors.toList()));
    private final String name;
    private final int instructionSize;
    private final int lineSize;
    private final List<InstructionItem> items;
    private final int priority;
    Category() {
        this.priority = ConfigManager.getInt(String.format("categories.%s.priority", this.name()));
        this.instructionSize = ConfigManager.getInt(String.format("categories.%s.instructionSize", this.name()));
        this.lineSize = ConfigManager.getInt(String.format("categories.%s.lineSize", this.name()));
        this.name = ConfigManager.getString(String.format("categories.%s.name", this.name()));
        this.items = ConfigManager.getItems(String.format("categories.%s.items", this.name()));
    }
    Category(List<InstructionItem> items) {
        this.priority = ConfigManager.getInt(String.format("categories.%s.priority", this.name()));
        this.instructionSize = ConfigManager.getInt(String.format("categories.%s.instructionSize", this.name()));
        this.lineSize = ConfigManager.getInt(String.format("categories.%s.lineSize", this.name()));
        this.name = ConfigManager.getString(String.format("categories.%s.name", this.name()));
        this.items = items;
        this.items.addAll(ConfigManager.getItems(String.format("categories.%s.items", this.name())));
    }
}
