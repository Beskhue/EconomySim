package org.kepow.economysim;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Convenience class that wraps around to doubles to represent
 * transaction movement.
 * 
 * @author Thomas Churchman
 *
 */
public class TransactionMovement implements ConfigurationSerializable
{
	private double buyMovement;
	private double saleMovement;
	
	/**
	 * Constructor. Initializes transaction movement to 0.
	 */
	public TransactionMovement()
	{
		this(0.0, 0.0);
	}
	
	/**
	 * Constructor.
	 * @param buyMovement The buy movement to initialize to.
	 * @param saleMovement The sale movement to initialize to.
	 */
	public TransactionMovement(double buyMovement, double saleMovement)
	{
		this.buyMovement = buyMovement;
		this.saleMovement = saleMovement;
	}
	
	/**
	 * Constructor.
	 * @param map A configuration map to construct the WorldSimulator out of.
	 */
	public TransactionMovement(Map map)
	{
		this.buyMovement = (Double) map.get("buyMovement");
		this.saleMovement = (Double) map.get("saleMovement");
	}
	
	/**
	 * Copy constructor.
	 * @param transactionMovement Object to copy.
	 */
	public TransactionMovement(TransactionMovement transactionMovement)
	{
		this.buyMovement = transactionMovement.buyMovement;
		this.saleMovement = transactionMovement.saleMovement;
	}
	
	/**
	 * Add buy movement.
	 * @param buyMovement The amount to add.
	 */
	public void addBuyMovement(double buyMovement)
	{
		this.buyMovement += buyMovement;
	}
	
	/**
	 * Add sale movement.
	 * @param saleMovement The amount to add.
	 */
	public void addSaleMovement(double saleMovement)
	{
		this.saleMovement += saleMovement;
	}
	
	/**
	 * Add transaction movement.
	 * @param movement The amount to add.
	 * @param type The movement type (BUY/SELL).
	 */
	public void addMovement(double movement, Simulator.TransactionType type)
	{
		if(type == Simulator.TransactionType.BUY)
		{
			this.addBuyMovement(movement);
		}
		else
		{
			this.addSaleMovement(movement);
		}
	}
	
	/**
	 * Decay buy movement by a factor. For example, if decay is 0.05, then
	 * 5% of the buy movement will be removed.
	 * @param decay The amount to decay with.
	 */
	public void decayBuyMovement(double decay)
	{
		this.buyMovement *= (1-decay);
	}
	
	/**
	 * Decay sale movement by a factor. For example, if decay is 0.05, then
	 * 5% of the sale movement will be removed.
	 * @param decay The amount to decay with.
	 */
	public void decaySaleMovement(double decay)
	{
		this.saleMovement *= (1-decay);
	}
	
	/**
	 * Get the buy movement.
	 * @return The buy movement.
	 */
	public double getBuyMovement()
	{
		return this.buyMovement;
	}
	
	/**
	 * Get the sale movement.
	 * @return The sale movement.
	 */
	public double getSaleMovement()
	{
		return this.saleMovement;
	}
	
	/**
	 * Get the transaction movement.
	 * @return A tuple containing the buy movement as first element, and
	 * the sale movement as second element.
	 */
	public Tuple<Double, Double> getTransactionMovement()
	{
		return new Tuple<Double, Double>(this.buyMovement, this.saleMovement);
	}

	/**
	 * Get the stock (i.e., supply - demand; saleMovement - buyMovement).
	 * @return The supply.
	 */
	public double getSupply()
	{
		return this.saleMovement - this.buyMovement;
	}
	
	/**
	 * Get the demand (i.e., demand - supply; buyMovement - saleMovement).
	 * @return
	 */
	public double getDemand()
	{
		return -this.getSupply();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
	 */
	@Override
	public Map<String, Object> serialize() 
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("buyMovement", this.buyMovement);
		map.put("saleMovement", this.saleMovement);
		
		return map;
	}
}
