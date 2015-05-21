package org.kepow.economysim;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents the economy simulation.
 * 
 * @author Thomas Churchman
 *
 */
public class Simulator implements ConfigurationSerializable
{
	/**
	 * Enum enumerating the possible transaction types.
	 * 
	 * @author Thomas Churchman
	 *
	 */
	public enum TransactionType
	{
		SELL, BUY
	}
	
	/**
	 * Class that represents the economy simulation for a single world group.
	 * 
	 * @author Thomas Churchman
	 *
	 */

	private Map<String, WorldSimulator> simulators;
	
	/**
	 * Constructor.
	 * @param map A configuration map to construct the Simulator out of.
	 */
	public Simulator(Map<String, Object> map)
	{
		simulators = (Map<String, WorldSimulator>) map.get("simulators");
	}
	
	/**
	 * Constructor.
	 */
	public Simulator()
	{
		simulators = new HashMap<String, WorldSimulator>();
	}
	
	/**
	 * Record buy movement of an item in the simulator.
	 * @param worldGroup The world group to add the buy movement for.
	 * @param items The item to add the buy movement for.
	 */
	public void addBuyMovement(String worldGroup, ItemStack[] items)
	{
		if(!simulators.containsKey(worldGroup))
		{
			simulators.put(worldGroup, new WorldSimulator());
		}
		
		simulators.get(worldGroup).addBuyMovement(items);
	}
	
	/**
	 * Record sale movement of an item in the simulator.
	 * @param worldGroup The world group to add the buy movement for.
	 * @param items The item to add the buy movement for.
	 */
	public void addSaleMovement(String worldGroup, ItemStack[] items)
	{
		if(!simulators.containsKey(worldGroup))
		{
			simulators.put(worldGroup, new WorldSimulator());
		}
		
		simulators.get(worldGroup).addSaleMovement(items);
	}
	
	/**
	 * Get the total price of an array of item stacks for a given transaction type and 
	 * a given world group.
	 * @param worldGroup The world group to get the total price for.
	 * @param items The array of item stacks to get the total price for.
	 * @param type The transaction type of the transaction to get the price for.
	 * @return The total price.
	 */
	public double getTotalPrice(String worldGroup, ItemStack[] items, TransactionType type)
	{
		if(!simulators.containsKey(worldGroup))
		{
			simulators.put(worldGroup, new WorldSimulator());
		}
		
		return simulators.get(worldGroup).getPrice(items, type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
	 */
	public Map<String, Object> serialize() 
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("simulators", simulators);
		
		return map;
	}
}
