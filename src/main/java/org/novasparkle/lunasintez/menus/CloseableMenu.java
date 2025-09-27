package org.novasparkle.lunasintez.menus;

import org.novasparkle.lunasintez.sintez.SintezSpawner;

public interface CloseableMenu {
    boolean belongsToSpawner(SintezSpawner spawner);
}
