package org.kepow.economysim;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Class to represent a "manage menu". This is the first menu
 * a player will see when they start to manage a shop. 
 * 
 * @author Thomas Churchman
 *
 */
public class ManageMenu extends ShopMenu
{
	/**
	 * Listener class for the "Manage Number of Buy Rows"-button.
	 * 
	 * @author Thomas Churchman
	 *
	 */
	private class ManageNumBuyRowsMenuButtonListener implements MenuButtonListener
	{
		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuButtonClick(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton, org.bukkit.event.inventory.InventoryClickEvent)
		 */
		public void onMenuButtonClick(Menu menu, MenuButton sender,
				InventoryClickEvent event) 
		{
			menu.close();
			
			ConversationFactory factory = new ConversationFactory(PluginState.getPlugin());
			
			ManageNumBuyRows prompt = new ManageNumBuyRows(((ShopMenu)menu).getShop());
			
			factory.withFirstPrompt(prompt);
			Conversation conversation = factory.buildConversation(menu.getPlayer());
			conversation.begin();
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
	 * Listener class for the "Manage Buy Items"-button.
	 * 
	 * @author Thomas Churchman
	 *
	 */
	private class ManageBuyItemsMenuButtonListener implements MenuButtonListener
	{
		/*
		 * (non-Javadoc)
		 * @see org.kepow.economysim.MenuButtonListener#onMenuButtonClick(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton, org.bukkit.event.inventory.InventoryClickEvent)
		 */
		public void onMenuButtonClick(Menu menu, MenuButton sender,
				InventoryClickEvent event) 
		{
			Menu manageItemsMenu = new ManageItemsMenu(shop, menu.getPlayer());
			manageItemsMenu.show(menu.getPlayer());
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
	 * Constructor.
	 * @param shop The shop to create the menu for.
	 * @param player The player to create the menu for. 
	 */
	public ManageMenu(Shop shop, Player player)
	{
		super(shop, player);
		
		this.setName(Utils.prepareMessage("inventoryHeaders.manage", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
		try
		{
			this.setNumRows(1);
		}
		catch(Exception e)
		{
			PluginState.getPlugin().getLogger().severe("Could not set number of menu inventory rows.");
		}
		this.prepareInventory();
		
		
		MenuButton manageNumBuyRowsMenuButton = new MenuButton(Material.RECORD_3, "Set Number of Rows", Utils.prepareDescription("buttonDescriptions.setNumRows", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
		manageNumBuyRowsMenuButton.setListener(new ManageNumBuyRowsMenuButtonListener());
		
		MenuButton manageBuyItemsMenuButton = new MenuButton(Material.RECORD_3, "Set Inventory", Utils.prepareDescription("buttonDescriptions.setInventory", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
		manageBuyItemsMenuButton.setListener(new ManageBuyItemsMenuButtonListener());
		
		this.setButton(0, manageNumBuyRowsMenuButton);
		this.setButton(1, manageBuyItemsMenuButton); 
	}
}
