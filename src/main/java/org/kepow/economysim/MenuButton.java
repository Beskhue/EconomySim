package org.kepow.economysim;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Class to represent a button placeable in a menu.
 * 
 * @author Thomas Churchman
 *
 */
public class MenuButton 
{
    protected ItemStack item;

    private MenuButtonListener listener = null;

    /**
     * Constructor.
     * @param material The material the button should use the icon of.
     * @param buttonName The name (title) of the button.
     */
    public MenuButton(Material material, String buttonName)
    {
        this(new ItemStack(material), buttonName);
    }

    /**
     * Constructor.
     * @param material The material the button should use the icon of.
     * @param buttonName The name (title) of the button.
     * @param buttonDescription The description (lore) of the button.
     */
    public MenuButton(Material material, String buttonName, String[] buttonDescription)
    {
        this(new ItemStack(material), buttonName, buttonDescription);
    }

    /**
     * Constructor.
     * @param buttonItem The item stack the button should use the icon of.
     * @param buttonName The name (title) of the button.
     */
    public MenuButton(ItemStack buttonItem, String buttonName)
    {
        this(buttonItem, buttonName, new String[0]);
    }

    /**
     * Constructor.
     * @param buttonItem The item stack the button should use the icon of.
     * @param buttonName The name (title) of the button.
     * @param buttonDescription The description (lore) of the button.
     */
    public MenuButton(ItemStack buttonItem, String buttonName, String[] buttonDescription)
    {
        // Make a copy of the item stack 
        item = new ItemStack(buttonItem);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(buttonName);
        meta.setLore(Arrays.asList(buttonDescription));

        item.setItemMeta(meta);
    }

    public void setName(String buttonName)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(buttonName);

        item.setItemMeta(meta);
    }

    public void setDescription(String[] buttonDescription)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(buttonDescription));

        item.setItemMeta(meta);
    }

    public void setStackAmount(int n)
    {
        item.setAmount(n);
    }

    public void setListener(MenuButtonListener func)
    {
        this.listener = func;
    }

    public ItemStack getItem()
    {
        return item;
    }

    /**
     * Called by a menu when this button has been clicked in it.
     * @param menu The menu that sent the event.
     * @param event The event.
     */
    public void onShopButtonClick(Menu menu, InventoryClickEvent event)
    {
        Player player = (Player)event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
        if(this.listener != null)
        {
            this.listener.onMenuButtonClick(menu, this, event);
        }
    }

    /**
     * Called by a menu when the menu is being updated and the menu contains this button.
     * @param menu The menu that is being updated.
     */
    public void onMenuUpdate(Menu menu)
    {
        if(this.listener != null)
        {
            this.listener.onMenuUpdate(menu, this);
        }
    }
}
