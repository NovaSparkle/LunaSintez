package org.novasparkle.lunasintez.sintez.mobs;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunasintez.sintez.mobs.status.Applicator;

@Getter
public abstract class MobType {
    protected final EntityType entityType;
    protected final SintezSpawner sintezSpawner;
    @Setter
    protected Applicator applicator;

    protected MobType(EntityType entityType, SintezSpawner sintezSpawner, Applicator applicator) {
        this.entityType = entityType;
        this.sintezSpawner = sintezSpawner;
        this.applicator = applicator;
    }
    public void setMob() {
        this.sintezSpawner.setMob(this.entityType);
    }
}
