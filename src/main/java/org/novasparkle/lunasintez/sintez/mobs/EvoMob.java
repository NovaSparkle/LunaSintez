package org.novasparkle.lunasintez.sintez.mobs;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.status.EvoStatus;
import org.novasparkle.lunaspring.API.configuration.Configuration;

@Getter
public class EvoMob extends MobType {
    private final EntityType sintezEntity;
    public EvoMob(@Nullable EntityType entityType, EntityType sintezEntity, SintezSpawner sintezSpawner, EvoStatus status) {
        super(entityType, sintezSpawner, status);
        this.sintezEntity = sintezEntity;
        Configuration spawnerConfig = sintezSpawner.getConfig();

        ConfigurationSection evoSection = spawnerConfig.getSection("evolutions");
        if (evoSection == null) {
            evoSection = spawnerConfig.self().createSection("evolutions");
        }
        EvoStatus evoStatus = ((EvoStatus) this.getApplicator());
        evoSection.set(String.format("%s.status", sintezEntity.name().toUpperCase()), evoStatus.name());
        if (!evoStatus.equals(EvoStatus.NO_EVO))
            evoSection.set(String.format("%s.entityType", sintezEntity.name().toUpperCase()), this.getEntityType().name());
        spawnerConfig.save();
    }
}



