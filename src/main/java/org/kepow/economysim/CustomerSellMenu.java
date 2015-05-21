package org.kepow.economysim;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Class to represent the menu a player sees when they
 * are selling items to the shop.
 * 
 * @author Thomas Churchman
 *
 */
public class CustomerSellMenu extends ShopMenu implements MenuListener
{
	/**
	 * Listener class for the "Cancel"-button.
	 * 
	 * @author Thomas Churchman
	 *
	 */
	private class CancelMenuButtonListener implements MenuButtonListener
	{
		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuButtonClick(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton, org.bukkit.event.inventory.InventoryClickEvent)
		 */
		public void onMenuButtonClick(Menu menu, MenuButton sender,
				InventoryClickEvent event) 
		{
			menu.close();
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
	 * Listener class for the "Confirm"-button.
	 * 
	 * @author Thomas Churchman
	 *
	 */
	private class ConfirmMenuButtonListener implements MenuButtonListener
	{
		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuButtonClick(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton, org.bukkit.event.inventory.InventoryClickEvent)
		 */
		public void onMenuButtonClick(Menu menu, MenuButton sender,
				InventoryClickEvent event) 
		{
			ItemStack[] items = menu.getPlacedItems();
			
			Player player = menu.getPlayer();
			String worldGroup = PluginState.getWorldConfig().getGroupFromWorld(player.getWorld());
			
			if(items.length > 0)
			{
				double price = PluginState.getSimulator().getTotalPrice(worldGroup, items, Simulator.TransactionType.SELL);
				EconomyResponse r = EconomySim.economy.depositPlayer(player, price);
				
				if(r.transactionSuccess())
				{
					CustomerSellMenu.this.confirmedSale = true;
					
					PluginState.getSimulator().addSaleMovement(worldGroup, items);
					
					player.sendMessage(Utils.prepareMessage("transactions.soldItem", 
							"%amount", items.length, 
							"%value", price, 
							"%currencySingular", EconomySim.economy.currencyNameSingular(), 
							"%currencyPlural", EconomySim.economy.currencyNamePlural()));
				}
				else
				{
					player.sendMessage(Utils.prepareMessage("transactions.failed", 
							"%error", r.errorMessage));
				}
			}
			
			menu.close();
		}

		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuUpdate(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton)
		 */
		public void onMenuUpdate(Menu menu, MenuButton sender) 
		{
			ItemStack[] items = menu.getPlacedItems();
			
			Player player = menu.getPlayer();
			String worldGroup = PluginState.getWorldConfig().getGroupFromWorld(player.getWorld());
			
			double price = PluginState.getSimulator().getTotalPrice(worldGroup, items, Simulator.TransactionType.SELL);
			if(items.length > 0)
			{
				sender.setDescription(Utils.prepareDescription("buttonDescriptions.confirmSaleWithPrice", 
						"%amount", items.length, 
						"%value", price, 
						"%currencySingular", EconomySim.economy.currencyNameSingular(), 
						"%currencyPlural", EconomySim.economy.currencyNamePlural()));
			}
			else
			{
				sender.setDescription(Utils.prepareDescription("buttonDescriptions.confirmSale"));
			}
			
			menu.updateInventory();
		}
		
	}
	
	public final int NUM_ROWS = 6;
	
	private boolean confirmedSale;
	
	/**
	 * Constructor.
	 * @param shop The shop the menu is for.
	 * @param player Thep layer the menu is for.
	 */
	public CustomerSellMenu(Shop shop, Player player)
	{
		super(shop, player);
		this.addListener(this);
		
		this.confirmedSale = false;
		
		this.setName(Utils.prepareMessage("inventoryHeaders.sell", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
		try
		{
			this.setNumRows(NUM_ROWS);
		}
		catch(Exception e)
		{
			PluginState.getPlugin().getLogger().severe("Could not set number of menu inventory rows.");
		}
		this.setAllowPlaceItems(true);
		this.prepareInventory();
		
		MenuButton cancelButton = new MenuButton(Material.REDSTONE_BLOCK, "Cancel", Utils.prepareDescription("buttonDescriptions.cancelSale"));
		cancelButton.setListener(new CancelMenuButtonListener());
		this.setButton(NUM_ROWS*9-2, cancelButton);
		
		MenuButton confirmButton = new MenuButton(Material.EMERALD_BLOCK, "Confirm", Utils.prepareDescription("buttonDescriptions.confirmSale"));
		confirmButton.setListener(new ConfirmMenuButtonListener());
		this.setButton(NUM_ROWS*9-1, confirmButton);
	}

	/*
	 * (non-Javadoc)
	 * @see org.kepow.economysim.MenuListener#onMenuInventoryClose(org.kepow.economysim.Menu, org.bukkit.event.inventory.InventoryCloseEvent)
	 */
	public void onMenuInventoryClose(Menu menu, InventoryCloseEvent event) 
	{
		Player player = menu.getPlayer();
		if(!confirmedSale)
		{
			ItemStack[] items = menu.getPlacedItems();
			Utils.giveItems(player, items);
		}
		
		player.sendMessage(Utils.prepareMessage("transactions.closeSale", "%player", player.getName()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.kepow.economysim.MenuListener#onMenuInventoryOpen(org.kepow.economysim.Menu, org.bukkit.event.inventory.InventoryOpenEvent)
	 */
	public void onMenuInventoryOpen(Menu menu, InventoryOpenEvent event) 
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.kepow.economysim.MenuListener#onMenuUpdate(org.kepow.economysim.MenuUpdateEvent)
	 */
	public void onMenuUpdate(MenuUpdateEvent event) 
	{
	}
}
