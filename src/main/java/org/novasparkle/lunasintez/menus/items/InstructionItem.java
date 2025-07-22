package org.novasparkle.lunasintez.menus.items;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunasintez.sintez.Category;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;

@Getter
@Accessors(fluent = true)
public class InstructionItem extends NonMenuItem {
    private final String configIdentifier;
    public InstructionItem(Material material) {
        super(material);
        this.configIdentifier = material.name();
    }

    public InstructionItem(ConfigurationSection section) {
        super(section);
        this.configIdentifier = section.getName();
    }

    public static InstructionItem getFromIdentifier(Category category, String configIdentifier) {
        return category.getItems().stream().filter(item -> item.configIdentifier().equals(configIdentifier)).findFirst().orElse(null);
    }
}