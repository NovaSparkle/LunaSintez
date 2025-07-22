package org.novasparkle.lunasintez.menus.items;

import org.novasparkle.lunaspring.API.menus.IMenu;

public interface LineItem {
    boolean isOpened();
    void insertItem(IMenu iMenu);
    String getConfigIdentifier();
}