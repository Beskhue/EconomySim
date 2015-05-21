package org.kepow.economysim;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

/**
 * Class that represents the Citizens 2.0 merchant trait.
 * 
 * @author Thomas Churchman
 *
 */
public class EconomySimMerchantTrait extends Trait
{
	private String shopName = "TestShop";
	
	/**
	 * Constructor.
	 */
	public EconomySimMerchantTrait() 
	{
		super("economysim");
	}
	
	/*
	 * (non-Javadoc)
	 * Called after onAttach, before onSpawn.
	 * @see net.citizensnpcs.api.trait.Trait#load(net.citizensnpcs.api.util.DataKey)
	 */
	public void load(DataKey key) 
	{
		shopName = key.getString("shopName", "defaultShop");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.citizensnpcs.api.trait.Trait#save(net.citizensnpcs.api.util.DataKey)
	 */
	public void save(DataKey key) 
	{
		key.setString("shopName", shopName);
	}

    /**
     * Handle right-click event.
     * @param event
     */
	@EventHandler
	public void onRightClick(net.citizensnpcs.api.event.NPCRightClickEvent event)
	{
		if(this.npc != event.getNPC()) 
		{
			return;
		}
		
		Player player = event.getClicker();
		boolean shiftClick = player.isSneaking();
		Shop shop = PluginState.getShopList().get(this.shopName);
		if(shop != null)
		{
			if (shiftClick) 
			{	// Manage shop			
				if (shop.canManage(player)) 
				{
					Menu menu = new ManageMenu(shop, player);
					menu.show(player);
					return;
				}
				else
				{
					player.sendMessage(Utils.prepareMessage("commands.doNotOwnShop", 
							"%shop", shop.getName(),
							"%shopDisplayName", shop.getDisplayName()));
				}
			}
			else
			{	// Show customer menu
				Menu menu = new CustomerMenu(shop, player);
				menu.show(player);
			}
		}
	}
	
	/**
	 * Set the NPC's shop.
	 * 
	 * @param shopName The shop's name to set the NPC's shop to.
	 */
	public void setShop(String shopName)
	{
		this.shopName = shopName;
	}
}
