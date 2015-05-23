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
	private Map<ItemStack, TransactionMovement> transactionMovement;
	private String worldGroup = "default"; 
	
	/**
	 * Constructor.
	 * @param map A configuration map to construct the WorldSimulator out of.
	 */
	public WorldSimulator(Map<String, Object> map)
	{
		transactionMovement = (Map<ItemStack, TransactionMovement>) map.get("transactionMovement");
	}
	
	/**
	 * Constructor.
	 */
	public WorldSimulator(String worldGroup)
	{
		transactionMovement = new HashMap<ItemStack, TransactionMovement>();
		this.worldGroup = worldGroup;
	}
	
	/**
	 * Set the world group this simulator is for.
	 * @param worldGroup The world group.
	 */
	public void setWorldGroup(String worldGroup)
	{
		this.worldGroup = worldGroup;
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
			int amount = mapped.get(map);
			double movement = amount * map.getRelativeValue();
			
			if(transactionMovement.containsKey(map.getItem()))
			{
				transactionMovement.get(map.getItem()).addBuyMovement(movement);
			}
			else
			{
				TransactionMovement trMovement = new TransactionMovement();
				trMovement.addBuyMovement(movement);
				transactionMovement.put(map.getItem(), trMovement);
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
			int amount = mapped.get(map);
			double movement = amount * map.getRelativeValue();
			
			if(transactionMovement.containsKey(map.getItem()))
			{
				transactionMovement.get(map.getItem()).addSaleMovement(movement);
			}
			else
			{
				TransactionMovement trMovement = new TransactionMovement();
				trMovement.addSaleMovement(movement);
				transactionMovement.put(map.getItem(), trMovement);
			}
		}
	}
	
	/**
	 * Perform decay on the transaction amounts.
	 * @param buyDecay Factor to decay buy transactions with.
	 * @param sellDecay Factor to decay sale transactions with.
	 */
	public void performDecay(double buyDecay, double sellDecay)
	{
		for(TransactionMovement transactionMovement : this.transactionMovement.values())
		{
			transactionMovement.decayBuyMovement(buyDecay);
			transactionMovement.decaySaleMovement(sellDecay);
		}
	}
	
	/**
	 * Get the total price of an array of item stacks for a given transaction type.
	 * @param items The array of item stacks to get the total price for.
	 * @return The total price.
	 */
	public double getPrice(ItemStack[] items, TransactionType type)
	{
		// Make a deep copy to keep track of simulated amounts
		Map<ItemStack, TransactionMovement> transactionMovementCopy = new HashMap<ItemStack, TransactionMovement>();
		for(ItemStack item : this.transactionMovement.keySet())
		{
			TransactionMovement copy = new TransactionMovement(this.transactionMovement.get(item));
			transactionMovementCopy.put(item, copy);
		}
		
		Map<ItemConfig.ItemMap, Integer> mapped = getMappedItemStacks(items);
		
		double price = 0;
		//double basePrice = PluginState.getPlugin().getConfig().getDouble("simulator.basePrice");
		double basePrice = PluginState.getWorldConfig().getBasePrice(this.worldGroup);
		
		//double sellPriceSteepness = PluginState.getPlugin().getConfig().getDouble("simulator.sellPriceSteepness");
		double sellPriceSteepness = PluginState.getWorldConfig().getSellPriceSteepness(this.worldGroup);
		
		//double buyPriceSteepness = PluginState.getPlugin().getConfig().getDouble("simulator.buyPriceSteepness");
		double buyPriceSteepness = PluginState.getWorldConfig().getBuyPriceSteepness(this.worldGroup);
		
		//double buyPriceAsymptote = PluginState.getPlugin().getConfig().getDouble("simulator.buyPriceAsymptoteSlope");
		double buyPriceAsymptote = PluginState.getWorldConfig().getBuyPriceAsymptoteSlope(this.worldGroup);
		
		//double sellPriceFactor = PluginState.getPlugin().getConfig().getDouble("simulator.sellPriceFactor");
		double sellPriceFactor = PluginState.getWorldConfig().getSellPriceFactor(this.worldGroup);
		
		//double buyPriceFactor = PluginState.getPlugin().getConfig().getDouble("simulator.buyPriceFactor");
		double buyPriceFactor = PluginState.getWorldConfig().getBuyPriceFactor(this.worldGroup);
		
		
		for(ItemConfig.ItemMap map : mapped.keySet())
		{
			double amount = mapped.get(map) * map.getRelativeValue();
			
			TransactionMovement movement;
			if(transactionMovementCopy.containsKey(map.getItem()))
			{
				movement = transactionMovementCopy.get(map.getItem());
			}
			else
			{
				movement = new TransactionMovement();
			}
			
			price += Simulator.getTransactionPrice(type, 
					amount, 
					movement.getDemand(), 
					basePrice, 
					sellPriceSteepness, buyPriceSteepness, 
					buyPriceAsymptote, 
					sellPriceFactor, buyPriceFactor);
			
			movement.addMovement(amount, type);
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
		map.put("transactionMovement", transactionMovement);
		
		return map;
	}
}