package org.novasparkle.lunasintez.sintez.mobs;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.sintez.Category;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.status.EvoStatus;
import org.novasparkle.lunasintez.sintez.mobs.status.MobStatus;
import org.novasparkle.lunaspring.API.configuration.Configuration;

import java.util.List;

@Getter
public class SintezMob extends MobType implements Comparable<SintezMob> {
    private Category category;
    @Setter
    private Instruction instruction;
    private final EvoMob evoMob;
    public SintezMob(EntityType entityType, SintezSpawner spawner, MobStatus status) {
        super(entityType, spawner, status);
        String entityTypeStr = ConfigManager.getString(String.format("entityTypes.%s.evolution.entityType", this.getEntityType()));
        EvoStatus evoStatus;
        if (status == MobStatus.SINTEZ) {
            String evoStatusStr = spawner.getConfig().getString(String.format("evolutions.%s.status", entityTypeStr));
            if (evoStatusStr != null && !evoStatusStr.isEmpty())
                evoStatus = EvoStatus.valueOf(evoStatusStr);
            else evoStatus = EvoStatus.EVO_OPENED;


        } else evoStatus = EvoStatus.LOCKED;

        this.evoMob = new EvoMob(
                entityTypeStr == null ? null : EntityType.valueOf(entityTypeStr),
                this.getEntityType(),
                this.getSintezSpawner(),
                evoStatus
        );

        try {
            this.category = Category.valueOf(ConfigManager.getString(String.format("entityTypes.%s.category", entityType)));
        } catch (IllegalArgumentException | NullPointerException e) {
            this.category = Category.COMMON;
        }
    }
    public void unlockMobInConfig() {
        Configuration config = this.getSintezSpawner().getConfig();
        config.set(String.format("mobs.%s", this.entityType.name()), null);
        ConfigurationSection section = config.getSection("mobs");
        for (String key : section.getKeys(false)) {
            section.set(String.format("%s.status", key), "NEXT");
            config.set("evolutions.%s.status", EvoStatus.EVO_OPENED.name());
            break;
        }
        List<String> completedList = config.getStringList("completed");
        completedList.add(this.entityType.name());
        config.setStringList("completed", completedList);

        this.getSintezSpawner().saveConfig();
    }
    @Override
    public String toString() {
        return "SintezMob{" +
                "category=" + category +
                ", status=" + applicator +
                ", entityType=" + entityType +
                '}';
    }
    @Override
    public int compareTo(@NotNull SintezMob o) {
        return this.category.getPriority() - o.category.getPriority();
    }
}
