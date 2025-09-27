package org.novasparkle.lunasintez.sintez.mobs;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.sintez.Category;
import org.novasparkle.lunasintez.sintez.Instruction;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.status.EvoStatus;
import org.novasparkle.lunasintez.sintez.mobs.status.SintezStatus;
import org.novasparkle.lunaspring.API.configuration.Configuration;

@Getter
public class SintezMob extends MobType implements Comparable<SintezMob> {
    private Category category;
    @Setter
    private Instruction instruction;
    public SintezMob(EntityType entityType, SintezSpawner spawner, SintezStatus status) {
        super(entityType, spawner, status);
        try {
            this.category = Category.valueOf(ConfigManager.getString(String.format("entityTypes.%s.category", entityType)));
        } catch (IllegalArgumentException | NullPointerException e) {
            this.category = Category.COMMON;
        }
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

    public EvoMob toEvoMob() {
        String evoEntityTypeStr = ConfigManager.getString(String.format("entityTypes.%s.evolution.entityType", this.entityType));
        EvoStatus evoStatus;
        EntityType evoEntityType = null;
        if (evoEntityTypeStr == null || evoEntityTypeStr.isEmpty()) {
            evoStatus = EvoStatus.NO_EVO;

        } else if (this.getApplicator().equals(SintezStatus.SINTEZ)) {
            Configuration spawnerConfig = this.sintezSpawner.getConfig();
            String status = spawnerConfig.getString(String.format("evolutions.%s.status", this.entityType.name().toUpperCase()));
            evoStatus = status != null && !status.isEmpty() ? EvoStatus.valueOf(status) : EvoStatus.EVO_OPENED;
            evoEntityType = EntityType.valueOf(evoEntityTypeStr);

        } else {
            evoStatus = EvoStatus.LOCKED;
            evoEntityType = EntityType.valueOf(evoEntityTypeStr);
        }
        return new EvoMob(evoEntityType, this.entityType, this.sintezSpawner, evoStatus);
    }
}
