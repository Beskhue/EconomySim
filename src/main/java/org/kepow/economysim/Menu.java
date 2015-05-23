package org.kepow.economysim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Class that wraps around Bukkit's Inventory to provide
 * a menu API.
 * 
 * @author Thomas Churchman
 */
public class Menu implements Listener
{	
	private String name;
	private int size;
	private boolean allowPlaceItems;
	
	private List<MenuListener> listeners;
	private HashMap<Integer, MenuButton> buttons;
	protected Inventory inventory = null;
	
	private Player player;
	
	/**
	 * Constructor.
	 * @param player The player the menu is for.
	 */
	public Menu(Player player)
	{
		PluginState.getPlugin().getServer().getPluginManager().registerEvents(this, PluginState.getPlugin());
		
		listeners = new ArrayList<MenuListener>();
		
		buttons = new HashMap<Integer, MenuButton>();
		name = "Inventory";
		size = 9;
		allowPlaceItems = false;
		
		this.player = player;
	}
	
	/**
	 * Creates the inventory if it was not created yet. 
	 */
	public void prepareInventory()
	{
		String nameSubstr = name.substring(0, Math.min(name.length(), 32));
		if(inventory == null)
		{
			inventory = Bukkit.createInventory(null, size, nameSubstr);
		}
	}
	
	/**
	 * Set the name for the inventory.
	 * @param name The name to give to the inventory.
	 */
	public void setName(String name)
	{
		if(inventory == null)
		{
			this.name = name;
		}
	}
	
	/**
	 * Set the number of rows the inventory should consist of.
	 * @param rows The number of rows the inventory should consist of.
	 * @throws Exception if rows is less than 1
	 */
	public void setNumRows(int rows) throws Exception
	{
		if(inventory == null)
		{
			if(rows >= 1)
			{
				this.size = rows*9;
			}
			else
			{
				throw(new Exception("Number of rows must be 1 or more."));
			}
		}
	}
	
	/**
	 * Set whether the player is allowed to place items in and
	 * take items from the inventory (except for the buttons).
	 * @param allowPlaceItems Boolean indicating whether the player
	 * is allowed to place items in and take items from the inventory. 
	 */
	public void setAllowPlaceItems(boolean allowPlaceItems)
	{
		this.allowPlaceItems = allowPlaceItems;
	}
	
	/**
	 * Set a button at the given slot 
	 * @param slot
	 * @param button
	 */
	public void setButton(int slot, MenuButton button)
	{
		if(slotIsInBounds(slot))
		{
			buttons.put(slot, button);
			inventory.setItem(slot, buttons.get(slot).getItem());
		}
	}	
	
	/**
	 * Add a listener for this menu.
	 * @param listener The listener to add. 
	 */
	public void addListener(MenuListener listener)
	{
		this.listeners.add(listener);
	}
	
	/**
	 * Remove a listener from this menu.
	 * @param listener The listener to remove. 
	 */
	public void removeListener(MenuListener listener)
	{
		this.listeners.remove(listener);
	}
	
	/**
	 * 
	 * @param slot
	 * @return
	 */
	protected boolean slotIsInBounds(int slot)
	{
		return slot >= 0 && slot < size;
	}
	
	/**
	 * Show the inventory to the given player.
	 * @param player The player to show the inventory to.
	 */
	public void show(Player player)
	{
		player.openInventory(inventory);
	}
	
	/**
	 * Call to let the menu buttons update.
	 */
	public void updateButtons()
	{
		final Menu thisMenu = this;
		
		MenuUpdateEvent event = new MenuUpdateEvent(this);
		for(MenuListener listener : listeners)
    	{
    		listener.onMenuUpdate(event);
    	}
		
		if(event.getCancelled())
		{
			// Event was cancelled, do not update the menu.
			return;
		}
	
		// Update buttons next tick.
		Bukkit.getScheduler().scheduleSyncDelayedTask(PluginState.getPlugin(), new Runnable()
		{ 
			public void run() 
			{
				for(MenuButton button : buttons.values())
				{
					button.onMenuUpdate(thisMenu);
				}
				
				updateInventory();
			} 
		}, 1);
	}
	
	/**
	 * Update the inventory for all viewers.
	 * Resets all the buttons.
	 */
	public void updateInventory()
	{
		for(int slot : buttons.keySet())
		{
			inventory.setItem(slot, buttons.get(slot).getItem());
		}
		// for(HumanEntity human : inventory.getViewers())	
		// {
		//     Player player = (Player)human;
		//     player.updateInventory();
		// }
	}
	
	/**
	 * Close the inventory for all viewers (but does not destroy the inventory!)
	 */
	public void close()
	{
		List<HumanEntity> l = new ArrayList<HumanEntity>(inventory.getViewers());
		for(HumanEntity human : l)	
		{
			human.closeInventory();
		}
	}
	
	/**
	 * Destroy this menu. 
	 */
	public void destroy()
	{
		HandlerList.unregisterAll(this);
	}
	
	/**
	 * Get an array of items placed in the inventory in this menu.
	 * Does not return menu buttons placed in the inventory.
	 * @return An array of items placed in the inventory, without 
	 * the menu buttons present in the inventory.
	 */
	public ItemStack[] getPlacedItems()
	{
		return getPlacedItemsAndSlots().values().toArray(new ItemStack[0]);
	}
	
	/**
	 * Get the player this menu is for.
	 * @return The player this menu  is for.
	 */
	public Player getPlayer()
	{
		return this.player;
	}
	
	/**
	 * Get a HashMap of slots mapping to items placed in the inventory 
	 * in this menu. Does not return menu buttons placed in the inventory.
	 * @return A HashMap of slot indices mapping to items placed 
	 * in the inventory, without the menu buttons present in the inventory.
	 */
	public HashMap<Integer, ItemStack> getPlacedItemsAndSlots()
	{
		HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		for(int slot = 0; slot < size; ++slot)
		{
			if(!buttons.containsKey(slot))
			{
				ItemStack item = inventory.getItem(slot);
				if(item != null)
				{
					items.put(slot, item);
				}
			}
		}
		
		return items;
	}
	
	/**
	 * Called when a player closes the inventory. 
	 * @param event
	 */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) 
    {
    	if (event.getInventory().equals(this.inventory)) 
		{
	    	for(MenuListener listener : listeners)
	    	{
	    		listener.onMenuInventoryClose(this, event);
	    	}
	    	
	    	this.destroy();
		}
    	
    	//if (viewing.contains(event.getPlayer().getName()))
        //    viewing.remove(event.getPlayer().getName());
    }
	
	/**
	 * Called when a player opens the inventory. 
	 * @param event
	 */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) 
    {
    	if (event.getInventory().equals(this.inventory)) 
		{
	    	for(MenuListener listener : listeners)
	    	{
	    		listener.onMenuInventoryOpen(this, event);
	    	}
		}
    }
    
	/**
	 * Called when items are placed by being dragged in the inventory.
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryDrag(InventoryDragEvent event)
	{
		if (event.getInventory().equals(this.inventory)) 
		{
			if(!allowPlaceItems)
			{
				Set<Integer> slots = event.getRawSlots();
				for(int slot : slots)
				{
					if(slot >= 0 && slot < size)
					{	// Dragged inside shop inventory
						event.setCancelled(true);
						return;
					}
				}
			}
			else
			{
				Set<Integer> slots = event.getRawSlots();
				for(int slot : slots)
				{
					if(slot >= 0 && slot < size)
					{	// Dragged inside shop inventory
						updateButtons();
						return;
					}
				}
			}
		}
	}
	
	/**
	 * Called when the user clicks in the inventory.
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) 
	{
		if (event.getInventory().equals(this.inventory)) 
		{
			int slot = event.getRawSlot();
			InventoryAction action = event.getAction();
			
			if (slot >= 0 && slot < size) 
			{	// Shop inventory was clicked
				if(!allowPlaceItems)
				{
					event.setCancelled(true);
				}
				else
				{
					if(buttons.containsKey(slot))
					{	// A button was clicked
						event.setCancelled(true);
					}
					else
					{	// The click was not on a button
						updateButtons();
					}
				}
				
				if(buttons.containsKey(slot))
				{
					buttons.get(slot).onShopButtonClick(this, event);
				}
			}
			else if(slot >= size)
			{	// Player inventory was clicked
				if(!allowPlaceItems)
				{
					if(!(
						// (Enumerate all allowable actions as opposed to
						// illegal actions, to more easily avoid exploits
						// in favour of restrictions if/when more inventory 
						// actions are added in future versions.)   
						action == InventoryAction.PICKUP_ALL 
						|| 
						action == InventoryAction.PICKUP_SOME 
						|| 
						action == InventoryAction.PICKUP_HALF
						||
						action == InventoryAction.PICKUP_ONE
						||
						action == InventoryAction.PLACE_ALL
						||
						action == InventoryAction.PLACE_SOME
						||
						action == InventoryAction.PLACE_ONE
						||
						action == InventoryAction.SWAP_WITH_CURSOR
						||
						action == InventoryAction.DROP_ALL_CURSOR
						||
						action == InventoryAction.DROP_ONE_CURSOR
						||
						action == InventoryAction.DROP_ONE_SLOT
						||
						action == InventoryAction.HOTBAR_MOVE_AND_READD
						||
						action == InventoryAction.HOTBAR_SWAP
						||
						action == InventoryAction.CLONE_STACK
						||
						action == InventoryAction.COLLECT_TO_CURSOR
						))
					{
						event.setCancelled(true);
					}
				}
				else
				{
					updateButtons();
				}
			}
		}
	}
}
