package org.novasparkle.lunasintez.sintez.mobs;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.status.EvoStatus;
import org.novasparkle.lunaspring.API.configuration.Configuration;

@Getter
public class EvoMob extends MobType {
    private final EntityType sintezEntity;
    public EvoMob(EntityType entityType, EntityType sintezEntity, SintezSpawner sintezSpawner, EvoStatus status) {
        super(entityType, sintezSpawner, status);
        this.sintezEntity = sintezEntity;
        Configuration spawnerConfig = sintezSpawner.getConfig();
        if (this.getEntityType() != null && spawnerConfig.getSection("evolutions") == null) {
            ConfigurationSection section = spawnerConfig.createSection("evolutions", entityType.name());
            section.set("status", ((EvoStatus) this.getApplicator()).name());
            section.set("entityType", this.getEntityType().name());
        }
    }
}



