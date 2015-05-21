package org.kepow.economysim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an Economy Sim shop.
 * 
 * @author Thomas Churchman
 *
 */
public class Shop implements ConfigurationSerializable
{
	private String name;
	private String displayName;
	private List<String> owners;
	private int numBuyRows;
	
	
	HashMap<Integer, ItemStack> items;
	
	/**
	 * Constructor.
	 * @param name The name of the shop.
	 */
	public Shop(String name)
	{
		this(name, name);
	}
	
	/**
	 * Constructor.
	 * @param name The name of the shop.
	 * @param displayName The display name of the shop.
	 */
	public Shop(String name, String displayName)
	{
		this.name = name;
		this.displayName = displayName;
		
		this.owners = new ArrayList<String>();
		
		//items = new ArrayList<ShopItem>();
		items = new HashMap<Integer, ItemStack>();
		numBuyRows = 4;
	}
	
	/**
	 * Constructor.
	 * @param mapO A configuration map to construct the ItemConfig out of.
	 */
	public Shop(Map mapO)
	{
		Map<String, Object> map = (Map<String, Object>) mapO;
		
		name = (String) map.get("name");
		displayName = (String) map.get("displayName");
		owners = (List<String>) map.get("owners");
		
		if(mapO.containsKey("numBuyRows"))
		{
			numBuyRows = (Integer) map.get("numBuyRows");
		}
		else
		{
			numBuyRows = 4;
		}
		
		items = (HashMap<Integer, ItemStack>) map.get("items");
	}
	
	/**
	 * Indicates whether the given player is allowed to manage the shop.
	 * @param player The player to check manage permission for.
	 * @return True of the player is allowed to manage the shop, false otherwise.
	 */
	public boolean canManage(Player player)
	{
		return player.hasPermission("economysim.admin") || owners.contains(player.getUniqueId().toString());
	}
	
	/**
	 * Add an owner to the shop.
	 * @param player The player to add as owner.
	 */
	public void addOwner(Player player)
	{
		if(!owners.contains(player.getUniqueId()))
		{
			owners.add(player.getUniqueId().toString());
		}
	}
	
	/**
	 * Remove an owner from the shop.
	 * @param sender The player that is attempting to remove an owner.
	 * @param target The player to remove as owner.
	 * @return True if the target player was removed, false otherwise.
	 */
	public boolean removeOwner(Player sender, Player target)
	{
		if(owners.size() <= 1 && !sender.hasPermission("economysim.admin"))
		{	// Cannot remove the last owner, unless player is an admin
			return false;
		}
		else
		{
			return owners.remove(target.getUniqueId().toString());			
		}
	}
	
	public HashMap<Integer, ItemStack> getGoods()
	{
		return items;
	}
	
	public void setGoods(HashMap<Integer, ItemStack> items)
	{
		this.items = items;
	}
	
	/**
	 * Add an item to the shop's stock.
	 * @param item The item to add.
	 */
	public void addShopItem(ItemStack item)
	{
		int addAtSlot = 0;
		for(int i = 0; i < numBuyRows*9; ++i)
		{
			if(!items.containsKey(i))
			{
				addAtSlot = i;
				break;
			}
		}
		
		addShopItem(addAtSlot, item);
	}
	
	/**
	 * Add an item to the shop's stock.
	 * @param slot The inventory slot number the item should be added at.
	 * @param item The item to add.
	 */
	public void addShopItem(int slot, ItemStack item)
	{
		items.put(slot, item);
	}
	
	/**
	 * Get the number of buy rows this shop has.
	 * @return The number of buy rows.
	 */
	public int getNumBuyRows()
	{
		return numBuyRows;
	}
	
	/**
	 * Set the number of buy rows this shop has.
	 * @param numBuyRows The number of buy rows.
	 */
	public void setNumBuyRows(int numBuyRows)
	{
		this.numBuyRows = numBuyRows;
	}
	
	/**
	 * Get the name of the shop.
	 * @return The name of the shop.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Set the display name of the shop.
	 * @param displayName The display name of the shop.
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
	
	/**
	 * Get the display name of the shop.
	 * @return The display name of the shop.
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
	 */
	public Map<String, Object> serialize() 
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", this.name);
		map.put("displayName", this.displayName);
		map.put("owners", this.owners);
		map.put("items", this.items);
		map.put("numBuyRows", this.numBuyRows);
		
		return map;
	}
	
}
