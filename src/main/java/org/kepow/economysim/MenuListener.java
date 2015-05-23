package org.kepow.economysim;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Interface representing a listener attachable to menus.
 * 
 * @author Thomas Churchman
 *
 */
public interface MenuListener 
{
    /**
     * Called when the inventory contained in a menu was closed.
     * @param menu The menu that the inventory that was closed belongs to.
     * @param event The original event.
     */
    public void onMenuInventoryClose(Menu menu, InventoryCloseEvent event);

    /**
     * Called when the inventory contained in a menu is being opened.
     * @param menu The menu that the inventory that is being opened belongs to.
     * @param event The original event.
     */
    public void onMenuInventoryOpen(Menu menu, InventoryOpenEvent event);

    /**
     * Called when a menu is being updated.
     * @param event The menu update event.
     */
    public void onMenuUpdate(MenuUpdateEvent event);
}
