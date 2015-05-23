package org.kepow.economysim;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Class to represent the menu a player sees when they
 * manage a shop's inventory.
 * 
 * @author Thomas Churchman
 *
 */
public class ManageItemsMenu extends ShopMenu
{
    /**
     * Listener class for the "Finish"-button.
     * 
     * @author Thomas Churchman
     *
     */
    private class FinishMenuButtonListener implements MenuButtonListener
    {
        public void onMenuButtonClick(Menu menu, MenuButton sender,
            InventoryClickEvent event) 
        {
            HashMap<Integer, ItemStack> items = menu.getPlacedItemsAndSlots();
            shop.setGoods(items);

            Player player = (Player) event.getWhoClicked();
            player.sendMessage("You successfully updated the shop's goods.");

            menu.close();
        }

        public void onMenuUpdate(Menu menu, MenuButton sender) 
        {
        }
    }

    /**
     * Constructor.
     * @param shop The shop this menu is for.
     * @param player The player this menu is for.
     */
    public ManageItemsMenu(Shop shop, Player player)
    {
        super(shop, player);

        this.setName(Utils.prepareMessage("inventoryHeaders.manageItems", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
        try
        {
            this.setNumRows(shop.getNumBuyRows());
        }
        catch(Exception e)
        {
            PluginState.getPlugin().getLogger().severe("Could not set number of inventory rows.");
        }
        this.setAllowPlaceItems(true);
        this.prepareInventory();

        HashMap<Integer, ItemStack> items = shop.getGoods();
        for(int slot : items.keySet())
        {
            this.inventory.setItem(slot, items.get(slot));
        }

        MenuButton finishButton = new MenuButton(Material.EMERALD_BLOCK, "Finish", new String[]{"Finish managing and close."});
        finishButton.setListener(new FinishMenuButtonListener());
        this.setButton(shop.getNumBuyRows()*9-1, finishButton);
    }
}
