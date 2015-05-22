package org.kepow.economysim;

/**
 * Class representing the plugin's state.
 * 
 * @author Thomas Churchman
 *
 */
public final class PluginState 
{
	private static EconomySim plugin = null;
	private static WorldConfig worldConfig = null;
	private static Simulator simulator = null;
	private static ShopList shopList = null;
	private static ItemConfig itemConfig = null;
	
	private static CustomConfig shopsCustomConfig;
	private static CustomConfig simulatorCustomConfig;
	private static CustomConfig itemsCustomConfig;
	private static CustomConfig messagesCustomConfig;
	
	/**
	 * Prepare the custom config objects.
	 */
	public static void prepareCustomConfigs()
	{
		PluginState.shopsCustomConfig = new CustomConfig("shops.yml");
		PluginState.simulatorCustomConfig = new CustomConfig("simulator.yml");
		PluginState.itemsCustomConfig = new CustomConfig("items.yml");
		PluginState.messagesCustomConfig = new CustomConfig("messages.yml");
		
		shopsCustomConfig.saveDefaultConfig();
		simulatorCustomConfig.saveDefaultConfig();
		itemsCustomConfig.saveDefaultConfig();
		messagesCustomConfig.saveDefaultConfig();
	}
	
	public static CustomConfig getShopsCustomConfig()
	{
		return PluginState.shopsCustomConfig;
	}
	
	public static CustomConfig getSimulatorCustomConfig()
	{
		return PluginState.simulatorCustomConfig;
	}
	
	public static CustomConfig getItemsCustomConfig()
	{
		return PluginState.itemsCustomConfig;
	}
	
	public static CustomConfig getMessagesCustomConfig()
	{
		return PluginState.messagesCustomConfig;
	}
	
	public static void setPlugin(EconomySim plugin)
	{
		PluginState.plugin = plugin;
	}
	
	public static EconomySim getPlugin()
	{
		return PluginState.plugin;
	}
	
	public static void setSimulator(Simulator simulator)
	{
		PluginState.simulator = simulator;
	}
	
	public static Simulator getSimulator()
	{
		return PluginState.simulator;
	}
	
	public static void setShopList(ShopList shopList)
	{
		PluginState.shopList = shopList;
	}
	
	public static ShopList getShopList()
	{
		return PluginState.shopList;
	}
	
	public static void setWorldConfig(WorldConfig worldConfig)
	{
		PluginState.worldConfig = worldConfig; 
	}
	
	public static WorldConfig getWorldConfig()
	{
		return PluginState.worldConfig;
	}
	
	public static void setItemConfig(ItemConfig itemConfig)
	{
		PluginState.itemConfig = itemConfig; 
	}
	
	public static ItemConfig getItemConfig()
	{
		return PluginState.itemConfig;
	}
}
