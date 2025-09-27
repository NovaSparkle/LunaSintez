package org.novasparkle.lunasintez.items;

import org.novasparkle.lunaspring.API.menus.ItemListMenu;

public interface LineItem {
    boolean isOpened();
    void insertItem(ItemListMenu iMenu);
    String getConfigIdentifier();
}