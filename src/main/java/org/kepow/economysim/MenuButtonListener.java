package org.kepow.economysim;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Interface representing a listener attachable to a button.
 * 
 * @author Thomas Churchman
 *
 */
public interface MenuButtonListener 
{
    /**
     * Called when the button this listener is attached to has been clicked.
     * @param menu The menu the button was clicked in.
     * @param sender The button that was clicked.
     * @param event The original event in the menu inventory.
     */
    public void onMenuButtonClick(Menu menu, MenuButton sender, InventoryClickEvent event);

    /**
     * Called when the button this listener is attached to is in a menu that is being updated.
     * @param menu The menu the button is in that is being updated.
     * @param sender The button that is in the menu that is being updated.
     */
    public void onMenuUpdate(Menu menu, MenuButton sender);
}
