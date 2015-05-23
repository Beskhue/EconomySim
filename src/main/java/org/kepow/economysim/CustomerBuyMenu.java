package org.kepow.economysim;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents the menu a player sees when they
 * buy items from the shop.
 * 
 * @author Thomas Churchman
 *
 */
public class CustomerBuyMenu extends ShopMenu implements MenuListener
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
    public CustomerBuyMenu(Shop shop, Player player)
    {
        super(shop, player);
        this.addListener(this);

        this.setName(Utils.prepareMessage("inventoryHeaders.buy", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
        try
        {
            this.setNumRows(shop.getNumBuyRows());
        }
        catch(Exception e)
        {
            PluginState.getPlugin().getLogger().severe("Could not set number of inventory rows.");
        }
        this.prepareInventory();

        HashMap<Integer, ItemStack> items = shop.getGoods();
        for(int slot : items.keySet())
        {
            MenuBuyItemButton button = new MenuBuyItemButton(this, items.get(slot));
            this.setButton(slot, button);
        }

        MenuButton finishButton = new MenuButton(Material.REDSTONE_BLOCK, "Finish", new String[]{"Finish buying and close", "the shop."});
        finishButton.setListener(new FinishMenuButtonListener());
        this.setButton(shop.getNumBuyRows()*9-1, finishButton);
    }

    /*
     * (non-Javadoc)
     * @see org.kepow.economysim.MenuListener#onMenuInventoryClose(org.kepow.economysim.Menu, org.bukkit.event.inventory.InventoryCloseEvent)
     */
    public void onMenuInventoryClose(Menu menu, InventoryCloseEvent event) 
    {
        Player player = menu.getPlayer();
        player.sendMessage(Utils.prepareMessage("transactions.closeBuy", "%player", player.getName()));
    }

    /*
     * (non-Javadoc)
     * @see org.kepow.economysim.MenuListener#onMenuInventoryOpen(org.kepow.economysim.Menu, org.bukkit.event.inventory.InventoryOpenEvent)
     */
    public void onMenuInventoryOpen(Menu menu, InventoryOpenEvent event) 
    {
    }

    /*
     * (non-Javadoc)
     * @see org.kepow.economysim.MenuListener#onMenuUpdate(org.kepow.economysim.MenuUpdateEvent)
     */
    public void onMenuUpdate(MenuUpdateEvent event) 
    {
    }

}
