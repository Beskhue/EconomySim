package org.kepow.economysim;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Class representing a list of shops.
 * 
 * @author Thomas Churchman
 *
 */
public class ShopList implements ConfigurationSerializable
{
    HashMap<String, Shop> shops;

    /**
     * Constructor.
     */
    public ShopList()
    {
        shops = new HashMap<String, Shop>();
    }

    /**
     * Constructor.
     * @param map A configuration map to construct the ShopList out of.
     */
    public ShopList(Map map)
    {
        Object shopsObj = map.get("shops");
        if(shopsObj != null)
        {
            shops = new HashMap<String, Shop>((Map<String, Shop>) shopsObj);
        }
        else
        {
            shops = new HashMap<String, Shop>();
        }
    }

    /**
     * Add a shop to the shop list.
     * @param shop The shop to add.
     * @return true if shop name was unique and shop was added, false otherwise.
     */
    public boolean add(Shop shop)
    {
        return shops.put(shop.getName(), shop) == null;
    }

    /**
     * Remove a shop from the shop list.
     * @param shopName The shop's name to remove.
     * @return true if the shop was found and removed, false otherwise.
     */
    public boolean remove(String shopName)
    {
        Shop shop = get(shopName);
        if(shop != null)
        {
            return remove(shop);
        }
        else
        {
            return false;
        }
    }

    /**
     * Remove a shop from the shop list.
     * @param shop The shop to remove.
     * @return true if the shop was found and removed, false otherwise.
     */
    public boolean remove(Shop shop)
    {
        if(contains(shop))
        {
            shops.remove(shop.getName());
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Test if the list contains a shop with the given name.
     * @param shopName The name of the shop to search for.
     * @return True if the list contains the shop, false otherwise.
     */
    public boolean contains(String shopName)
    {
        return shops.containsKey(shopName);
    }

    /**
     * Test if the list contains the given shop.
     * @param shop The shop to search for.
     * @return True if the list contains the shop, false otherwise.
     */
    public boolean contains(Shop shop)
    {
        return shops.containsValue(shop);
    }

    /**
     * Get the shop with the given name.
     * @param shopName The shop name to search for.
     * @return A shop if the shop was found, null otherwise.
     */
    public Shop get(String shopName)
    {
        return shops.get(shopName);
    }

    /**
     * Get the shops contained in the shop list.
     * @return The shops contained in the shop list.
     */
    public HashMap<String, Shop> getShops()
    {
        return new HashMap<String, Shop>(shops);
    }

    /*
     * (non-Javadoc)
     * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
     */
    public Map<String, Object> serialize() 
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("shops", shops);
        return map;
    }
}
