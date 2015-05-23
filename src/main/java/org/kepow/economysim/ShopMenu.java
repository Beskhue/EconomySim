package org.kepow.economysim;

import org.bukkit.entity.Player;

/**
 * A menu for a shop.
 * 
 * @author Thomas Churchman
 *
 */
public class ShopMenu extends Menu
{
    protected final Shop shop;

    /**
     * Constructor.
     * @param shop The shop the menu is for.
     * @param player The player the menu is for.
     */
    public ShopMenu(Shop shop, Player player)
    {
        super(player);

        this.shop = shop;
    }

    /**
     * Get the shop this menu is for.
     * @return The shop this menu is for.
     */
    public Shop getShop()
    {
        return this.shop;
    }
}
