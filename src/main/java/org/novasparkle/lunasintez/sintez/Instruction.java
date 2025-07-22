package org.novasparkle.lunasintez.sintez;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunasintez.menus.items.InstructionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class Instruction {
    private final List<InstructionItem> instructionList;
    private final Line line;
    private final Category category;
    public Instruction(Category category, ConfigurationSection rootSection) {
        this.category = category;
        if (rootSection.getStringList("instruction").isEmpty()) {
            this.instructionList = new ArrayList<>();
            this.generateInstruction();
            this.line = new Line(this.instructionList, this.category, rootSection);

            rootSection.set("instruction", instructionList.stream().map(InstructionItem::configIdentifier).collect(Collectors.toList()));
            this.line.saveAll();
        } else {
            this.instructionList = rootSection.getStringList("instruction").stream().map(id -> InstructionItem.getFromIdentifier(this.category, id)).collect(Collectors.toList());
            this.line = new Line(rootSection, this.category);
        }
    }

    private void generateInstruction() {
        int i = 0;
        while (i < category.getInstructionSize()) {
            InstructionItem instructionItem = category.getItems().get(ThreadLocalRandom.current().nextInt(category.getItems().size()));
            if (!this.instructionList.contains(instructionItem)) {
                this.instructionList.add(instructionItem);
                i++;
            }
        }
    }
}
