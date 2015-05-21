package org.kepow.economysim;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Class to represent the first menu a player sees when
 * they open the shop menu.
 * 
 * @author Thomas Churchman
 *
 */
public class CustomerMenu extends ShopMenu implements MenuListener
{
	/**
	 * Listener class for the "Buy"-button.
	 * 
	 * @author Thomas Churchman
	 *
	 */
	private class BuyMenuButtonListener implements MenuButtonListener
	{
		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuButtonClick(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton, org.bukkit.event.inventory.InventoryClickEvent)
		 */
		public void onMenuButtonClick(Menu menu, MenuButton sender,
				InventoryClickEvent event) 
		{
			CustomerMenu.this.continued = true;
			
			Menu buyMenu = new CustomerBuyMenu(shop, menu.getPlayer());
			buyMenu.show(menu.getPlayer());
		}

		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuUpdate(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton)
		 */
		public void onMenuUpdate(Menu menu, MenuButton sender) 
		{	
		}
	}
	
	/**
	 * Listener class for the "Sell"-button.
	 * 
	 * @author Thomas Churchman
	 *
	 */
	private class SellMenuButtonListener implements MenuButtonListener
	{
		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuButtonClick(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton, org.bukkit.event.inventory.InventoryClickEvent)
		 */
		public void onMenuButtonClick(Menu menu, MenuButton sender,
				InventoryClickEvent event) 
		{
			CustomerMenu.this.continued = true;
			
			Menu sellMenu = new CustomerSellMenu(shop, menu.getPlayer());
			sellMenu.show(menu.getPlayer());
		}

		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuUpdate(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton)
		 */
		public void onMenuUpdate(Menu menu, MenuButton sender) 
		{
		}
	}
	
	private boolean continued;
	
	/**
	 * Constructor.
	 * @param shop The shop this menu is for.
	 * @param player The player this menu is for.
	 */
	public CustomerMenu(Shop shop, Player player)
	{
		super(shop, player);
		this.addListener(this);
		
		this.continued = false;
		
		this.setName(Utils.prepareMessage("inventoryHeaders.welcome", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
		try
		{
			this.setNumRows(1);
		}
		catch(Exception e)
		{
			PluginState.getPlugin().getLogger().severe("Could not set number of menu inventory rows.");
		}
		this.prepareInventory();
		
		MenuButton sellButton = new MenuButton(Material.GOLD_INGOT, "Sell", Utils.prepareDescription("buttonDescriptions.sellToShop", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
		sellButton.setListener(new SellMenuButtonListener());
		
		MenuButton buyButton = new MenuButton(Material.IRON_INGOT, "Buy", Utils.prepareDescription("buttonDescriptions.buyFromShop", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
		buyButton.setListener(new BuyMenuButtonListener());
		
		this.setButton(0, sellButton);
		this.setButton(1, buyButton); 
	}

	/*
	 * (non-Javadoc)
	 * @see org.kepow.economysim.MenuListener#onMenuInventoryClose(org.kepow.economysim.Menu, org.bukkit.event.inventory.InventoryCloseEvent)
	 */
	public void onMenuInventoryClose(Menu menu, InventoryCloseEvent event) 
	{
		if(!continued)
		{
			Player player = menu.getPlayer();
			
			player.sendMessage(Utils.prepareMessage("transactions.closeWelcome", "%player", player.getName()));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.kepow.economysim.MenuListener#onMenuInventoryOpen(org.kepow.economysim.Menu, org.bukkit.event.inventory.InventoryOpenEvent)
	 */
	public void onMenuInventoryOpen(Menu menu, InventoryOpenEvent event) 
	{
		Player player = menu.getPlayer();
		
		player.sendMessage(Utils.prepareMessage("transactions.welcome", 
				"%shop", shop.getName(),
				"%shopDisplayName", shop.getDisplayName(),
				"%player", player.getName()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.kepow.economysim.MenuListener#onMenuUpdate(org.kepow.economysim.MenuUpdateEvent)
	 */
	public void onMenuUpdate(MenuUpdateEvent event) 
	{
	}
}
