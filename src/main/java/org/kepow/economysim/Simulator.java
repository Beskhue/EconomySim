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
		
		for(String worldGroup : simulators.keySet())
		{
			simulators.get(worldGroup).setWorldGroup(worldGroup);
		}
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
			simulators.put(worldGroup, new WorldSimulator(worldGroup));
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
			simulators.put(worldGroup, new WorldSimulator(worldGroup));
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
			simulators.put(worldGroup, new WorldSimulator(worldGroup));
		}
		
		return simulators.get(worldGroup).getPrice(items, type);
	}
	
	/**
	 * Perform decay on the transaction amounts.
	 * @param buyDecay Factor to decay buy transactions with.
	 * @param sellDecay Factor to decay sale transactions with.
	 */
	public void performDecay(double buyDecay, double sellDecay)
	{
		for(WorldSimulator simulator : simulators.values())
		{
			simulator.performDecay(buyDecay, sellDecay);
		}
	}
	
	/**
	 * Get the price for a single transaction.
	 * @param type The transaction type.
	 * @param amount The transaction movement (e.g., 50 bought or 20 sold)
	 * @param demand The demand at the start of the transaction.
	 * @param basePrice The base price (i.e., price at 0 demand)
	 * @param sellPriceSteepness The logistic function steepness below 0.
	 * @param buyPriceSteepness The logistic function steepness above 0.
	 * @param buyPriceAsymptote The extra slope's steepness ("asymptote slope") above 0.
	 * @param sellPriceFactor Factor with which sale prices are multiplied.
	 * @param buyPriceFactor Factor with which buy prices are multiplied.
	 * @return The price for the transaction.
	 */
	public static double getTransactionPrice(
			TransactionType type, 
			double amount, 
			double demand, 
			double basePrice, 
			double sellPriceSteepness, double buyPriceSteepness,
			double buyPriceAsymptote,
			double sellPriceFactor, double buyPriceFactor)
	{
		double price;
		
		double startDemand = demand;
		double endDemand;
		if(type == TransactionType.BUY)
		{
			endDemand = startDemand + amount;
		}
		else
		{
			endDemand = startDemand - amount;
		}
		
		double lowDemand = Math.min(startDemand, endDemand);
		double highDemand = Math.max(startDemand, endDemand);
		
		if(startDemand * endDemand < 0)
		{	// Demand crosses the horizontal axis			
			price = 
					Simulator.getTransactionPrice(
						TransactionType.BUY,
						-lowDemand, // e.g. from -50 to 0 (start = -50, amount = --50 = 50)
						lowDemand,
						basePrice,
						sellPriceSteepness, buyPriceSteepness,
						buyPriceAsymptote,
						sellPriceFactor, buyPriceFactor
					)
					+
					Simulator.getTransactionPrice(
						TransactionType.BUY,
						highDemand,
						0, // e.g. from 0 to 50 (start = 0, amount = 50)
						basePrice,
						sellPriceSteepness, buyPriceSteepness,
						buyPriceAsymptote,
						sellPriceFactor, buyPriceFactor
					);
		}
		else
		{
			if(highDemand <= 0)
			{	// Perform the calculation with the "sell" part of the equation
				double e = Math.E;
				price = 
						Math.log(1+Math.pow(e, sellPriceSteepness * highDemand)) / sellPriceSteepness * (basePrice * 2)
						-
						Math.log(1+Math.pow(e, sellPriceSteepness * lowDemand)) / sellPriceSteepness * (basePrice * 2);	
			}
			else
			{	// Perform the calculation with the "buy" part of the equation				
				double e = Math.E;
				double eToXHigh = Math.pow(e, buyPriceSteepness * highDemand);
				double eToXLow = Math.pow(e, buyPriceSteepness * lowDemand);
				price = 
						(
								((eToXHigh + 1) * Math.log(eToXHigh + 1) + 1) / (buyPriceSteepness * (eToXHigh + 1)) * (basePrice * 2 * 2)
								+
								buyPriceAsymptote/2 * Math.pow(highDemand, 2)
						)
						-
						(
								((eToXLow + 1) * Math.log(eToXLow + 1) + 1) / (buyPriceSteepness * (eToXLow + 1)) * (basePrice * 2 * 2)
								+
								buyPriceAsymptote/2 * Math.pow(lowDemand, 2)
						);
				
				if(type == TransactionType.BUY)
				{
					price *= buyPriceFactor;
				}
				else
				{
					price *= sellPriceFactor;
				}
			}
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
		map.put("simulators", simulators);
		
		return map;
	}
}
