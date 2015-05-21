package org.kepow.economysim;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.kepow.economysim.Simulator.TransactionType;

/**
 * Class representing a simulator for a world group.
 * 
 * @author Thomas Churchman
 *
 */
public class WorldSimulator implements ConfigurationSerializable
{
	Map<ItemStack, Integer> stock;
	
	/**
	 * Constructor.
	 * @param map A configuration map to construct the WorldSimulator out of.
	 */
	public WorldSimulator(Map<String, Object> map)
	{
		stock = (Map<ItemStack, Integer>) map.get("stock");
	}
	
	/**
	 * Constructor.
	 */
	public WorldSimulator()
	{
		stock = new HashMap<ItemStack, Integer>();
	}
	
	/**
	 * Maps and compresses the entered item stacks. 
	 * @param items The item stacks to compress.
	 * @return A map containing mapped items as keys and how many of them are present as values.
	 */
	private Map<ItemConfig.ItemMap, Integer> getMappedItemStacks(ItemStack[] items)
	{
		Map<ItemConfig.ItemMap, Integer> mapped = new HashMap<ItemConfig.ItemMap, Integer>();
		
		for(ItemStack item : items)
		{
			ItemStack checkItem = new ItemStack(item);
			checkItem.setAmount(1);
			
			ItemConfig.ItemMap map = PluginState.getItemConfig().getMapping(checkItem);
			
			if(mapped.containsKey(map))
			{
				mapped.put(map, mapped.get(map) + item.getAmount());
			}
			else
			{
				mapped.put(map, item.getAmount());					
			}
		}
		
		return mapped;
	}
	
	/**
	 * Record buy movement of an item in the simulator.
	 * @param items The item to add the buy movement for.
	 */
	public void addBuyMovement(ItemStack[] items)
	{
		Map<ItemConfig.ItemMap, Integer> mapped = getMappedItemStacks(items);
		
		for(ItemConfig.ItemMap map : mapped.keySet())
		{
			int movement = mapped.get(map) * map.getRelativeValue();
			
			if(stock.containsKey(map))
			{
				stock.put(map.getItem(), stock.get(map) - movement);
			}
			else
			{
				stock.put(map.getItem(), -movement);
			}
		}
	}
	
	/**
	 * Record sale movement of an item in the simulator.
	 * @param items The item to add the sale movement for.
	 */
	public void addSaleMovement(ItemStack[] items)
	{
		Map<ItemConfig.ItemMap, Integer> mapped = getMappedItemStacks(items);
		
		for(ItemConfig.ItemMap map : mapped.keySet())
		{
			int movement = mapped.get(map) * map.getRelativeValue();
			
			if(stock.containsKey(map.getItem()))
			{
				stock.put(map.getItem(), stock.get(map.getItem()) + movement);
			}
			else
			{
				stock.put(map.getItem(), movement);
			}
		}
	}
	
	/**
	 * Get the total price of an array of item stacks for a given transaction type.
	 * @param items The array of item stacks to get the total price for.
	 * @return The total price.
	 */
	public double getPrice(ItemStack[] items, TransactionType type)
	{
		// Make a copy to keep track of simulated amounts
		Map<ItemStack, Integer> stockCopy = new HashMap<ItemStack, Integer>(stock);
		
		Map<ItemConfig.ItemMap, Integer> mapped = getMappedItemStacks(items);
		
		double price = 0;
		double maxPrice = PluginState.getPlugin().getConfig().getDouble("maxPrice");
		double steepness = PluginState.getPlugin().getConfig().getDouble("priceUpdateSteepness");
		
		
		for(ItemConfig.ItemMap map : mapped.keySet())
		{
			int amount = mapped.get(map) * map.getRelativeValue();
			
			int stock = 0;
			if(stockCopy.containsKey(map.getItem()))
			{
				stock = stockCopy.get(map.getItem());
			}
			
			for(int i = 0; i < amount; ++i)
			{
				if(type == TransactionType.BUY)
				{
					price += (maxPrice * Math.pow(Utils.logisticFunction(1, steepness, -stock, 0), 2)) * map.getHealth();
					--stock;
				}
				else
				{
					++stock;
					price += (maxPrice * Math.pow(Utils.logisticFunction(1, steepness, -stock, 0), 2)) * map.getHealth();
				}
			}
			
			stockCopy.put(map.getItem(), stock);
		}
		
		return price;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
	 */
	public Map<String, Object> serialize() 
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stock", stock);
		
		return map;
	}
}