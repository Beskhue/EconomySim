package org.kepow.economysim;

import java.util.Arrays;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Class to represent a "buy"-button for items.
 * 
 * @author Thomas Churchman
 *
 */
public class MenuBuyItemButton extends MenuButton implements MenuButtonListener
{
	private ItemStack representedItem;
	
	/**
	 * Constructor.
	 * @param menu The menu the button is for.
	 * @param itemStack The item stack the button should represent.
	 */
	public MenuBuyItemButton(Menu menu, ItemStack itemStack)
	{
		super(itemStack, "");
		
		this.representedItem = itemStack;
		this.representedItem.setAmount(1);
		
		this.item = new ItemStack(this.representedItem);
		
		this.setListener(this);
		
		update(menu);
	}
	
	/**
	 * Recalculate prices.
	 */
	public void update(Menu menu)
	{
		String worldGroup = PluginState.getWorldConfig().getGroupFromWorld(menu.getPlayer().getWorld());
		double priceFor1 = PluginState.getSimulator().getTotalPrice(worldGroup, new ItemStack[]{this.representedItem}, Simulator.TransactionType.BUY);
		
		ItemMeta meta = this.representedItem.getItemMeta();
		this.setName(Utils.prepareMessage("buttonDescriptions.buy", "%item", item.getType().toString()));
		
		int stackSize = item.getMaxStackSize();
		if(stackSize > 1)
		{
			ItemStack stacked = new ItemStack(this.representedItem);
			stacked.setAmount(stackSize);
			double stackedPrice = PluginState.getSimulator().getTotalPrice(worldGroup, new ItemStack[]{stacked}, Simulator.TransactionType.BUY);
			
			meta.setLore(Arrays.asList(Utils.prepareDescription("buttonDescriptions.buyStack", 
					"%priceOne", priceFor1, 
					"%priceStack", stackedPrice,
					"%stackSize", stackSize,
					"%currencySingular", EconomySim.economy.currencyNameSingular(), 
					"%currencyPlural", EconomySim.economy.currencyNamePlural())));
		}
		else
		{
			meta.setLore(Arrays.asList(Utils.prepareDescription("buttonDescriptions.buyOne", 
					"%priceOne", priceFor1, 
					"%currencySingular", EconomySim.economy.currencyNameSingular(), 
					"%currencyPlural", EconomySim.economy.currencyNamePlural())));
		}
		
		
		this.item.setItemMeta(meta);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.kepow.economysim.MenuButtonListener#onMenuButtonClick(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton, org.bukkit.event.inventory.InventoryClickEvent)
	 */
	public void onMenuButtonClick(Menu menu, MenuButton sender,
			InventoryClickEvent event) {
		boolean buyStack = event.isShiftClick();
		Player player = (Player) event.getWhoClicked();
		
		String worldGroup = PluginState.getWorldConfig().getGroupFromWorld(player.getWorld());
		
		int buyAmount = 1;
		if(buyStack)
		{
			buyAmount = this.representedItem.getMaxStackSize();
		}
		
		ItemStack buy = new ItemStack(this.representedItem);
		buy.setAmount(buyAmount);
		
		double price = PluginState.getSimulator().getTotalPrice(worldGroup, new ItemStack[]{buy}, Simulator.TransactionType.BUY);
		
		if(EconomySim.economy.getBalance(player) >= price)
		{
			ItemMeta meta = this.representedItem.getItemMeta();
			String name = meta.getDisplayName();
			
			EconomyResponse r = EconomySim.economy.withdrawPlayer(player, price);
			if(r.transactionSuccess())
			{
				Utils.giveItems(player, new ItemStack[]{buy});
				
				PluginState.getSimulator().addBuyMovement(worldGroup, new ItemStack[]{buy});
				PluginState.getPlugin().updateAllMenus();
				
				player.sendMessage(Utils.prepareMessage("transactions.boughtItem", 
						"%amount", buyAmount, 
						"%value", price, 
						"%item", this.representedItem.getType().toString(),
						"%currencySingular", EconomySim.economy.currencyNameSingular(), 
						"%currencyPlural", EconomySim.economy.currencyNamePlural()));
			}
			else
			{
				player.sendMessage(Utils.prepareMessage("transactions.failed", 
						"%error", r.errorMessage));
			}
			menu.updateButtons();
		}
		else
		{
			player.sendMessage(Utils.prepareMessage("transactions.notEnoughMoney", 
					"%amount", buyAmount, 
					"%value", price, 
					"%item", this.representedItem.getType().toString(),
					"%currencySingular", EconomySim.economy.currencyNameSingular(), 
					"%currencyPlural", EconomySim.economy.currencyNamePlural()));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.kepow.economysim.MenuButtonListener#onMenuUpdate(org.kepow.economysim.Menu, org.kepow.economysim.MenuButton)
	 */
	public void onMenuUpdate(Menu menu, MenuButton sender) 
	{
		this.update(menu);
	}
}
