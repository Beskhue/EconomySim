package org.kepow.economysim;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the plugin. Provides methods to load, 
 * unload and save the plugin state.
 * 
 * @author Thomas Churchman
 *
 */
public final class EconomySim extends JavaPlugin 
{
	public static Economy economy = null;
	public static Permission permissions = null;
	public static Chat chat = null;
	
	/**
	 * Called when the plugin has been loaded and is enabled.
	 */
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		ConfigurationSerialization.registerClass(ShopList.class);
		ConfigurationSerialization.registerClass(Shop.class);
		ConfigurationSerialization.registerClass(Simulator.class);
		ConfigurationSerialization.registerClass(WorldSimulator.class);
		
		
		PluginState.setPlugin(this);
		PluginState.prepareCustomConfigs();
		
		// Setup vault
        if(!setupEconomy()) 
        {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        
		// Set commands
        EconomySimCommandExecutor executor = new EconomySimCommandExecutor();
        
		this.getCommand("eslist").setExecutor(executor);
		this.getCommand("esadd").setExecutor(executor);
		this.getCommand("esremove").setExecutor(executor);
		this.getCommand("esrename").setExecutor(executor);
		this.getCommand("essetshop").setExecutor(executor);
		
		
		
		// Hook into citizens API 
		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) 
		{
			getLogger().severe(String.format("[%s] - Citizens 2 not found or not enabled. Not registering trait with Citizens 2.", getDescription().getName()));				
		}
		else
		{
			net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(EconomySimMerchantTrait.class).withName("economysim"));
		}
		
		// Load shops
		ShopList shops = (ShopList) PluginState.getShopsCustomConfig().getCustomConfig().get("shopList", ShopList.class);
		PluginState.setShopList(shops);
		
		// Load item config
		ItemConfig itemConfig = new ItemConfig(PluginState.getItemsCustomConfig().getCustomConfig().getConfigurationSection("itemGroups").getValues(false));
		PluginState.setItemConfig(itemConfig);
		
		// Load world config
		WorldConfig worldConfig = new WorldConfig(PluginState.getWorldsCustomConfig().getCustomConfig().getConfigurationSection("worldConfig").getValues(false));
		PluginState.setWorldConfig(worldConfig);
		
		// Load simulator
		Simulator simulator = null;
		if(PluginState.getSimulatorCustomConfig().getCustomConfig().contains("simulator"))
		{
			simulator = (Simulator) PluginState.getSimulatorCustomConfig().getCustomConfig().get("simulator");
		}
		else
		{
			simulator = new Simulator();
		}
		PluginState.setSimulator(simulator);
		
		// Set up scheduled saving
		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(PluginState.getPlugin(), new Runnable()
		{ 
			public void run() 
			{
				save();
			} 
		}, 20*60*5, 20*60*5);
	}
	
	/**
	 * Save the plugin state to files.
	 */
	private void save()
	{
		PluginState.getShopsCustomConfig().getCustomConfig().set("shopList", PluginState.getShopList());
		PluginState.getShopsCustomConfig().saveCustomConfig();
		
		PluginState.getSimulatorCustomConfig().getCustomConfig().set("simulator", PluginState.getSimulator());
		PluginState.getSimulatorCustomConfig().saveCustomConfig();
	}
	
	/**
	 * Called when the plugin is being disabled.
	 */
	@Override
	public void onDisable()
	{
		save();
	}
	
    private boolean setupEconomy() 
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) 
        {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) 
        {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
    
    private boolean setupPermissions() 
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        return permissions != null;
    }
    
    private boolean setupChat() 
    {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    
}
