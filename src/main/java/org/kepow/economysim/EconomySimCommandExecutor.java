package org.kepow.economysim;

import java.util.HashMap;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class to represent the command executor.
 * 
 * @author Thomas Churchman
 *
 */
public class EconomySimCommandExecutor implements CommandExecutor 
{
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel,
			String[] args) 
	{
		Player player = null;
	    if(sender instanceof Player) 
	    {
	    	player = (Player) sender;
		}
		
	    try
    	{
		    if(command.getLabel().equals("esadd")) 
		    {
    			String shopName = args[0];
    			Shop shop = new Shop(shopName);
    			if(player != null)
				{
    				shop.addOwner(player);
				}
    			
    			if(PluginState.getShopList().add(shop))
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.shopAdded", "%shop", shopName));
    			}
    			else
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.shopAlreadyExists", "%shop", shopName));
    			}
		    }
    		else if(command.getLabel().equals("eslist")) 
    		{
    			HashMap<String, Shop> shops = PluginState.getShopList().getShops();
    			
    			sender.sendMessage(Utils.prepareMessage("commands.shopListHeader"));
    			for(Shop shop : shops.values())
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.shopListShop", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName()));
    			}
    			sender.sendMessage(Utils.prepareMessage("commands.shopListFooter"));
    		}
    		else if(command.getLabel().equals("esrename")) 
    		{
    			String shopName = args[0];
    			
    			String displayName = "";
    			for(int i = 1; i < args.length; ++i)
    			{
    				if(i > 1)
    				{
    					displayName += " ";
    				}
    				displayName += args[i];
    			}
    			
    			Shop shop = PluginState.getShopList().get(shopName);
    			if(shop != null)
    			{
    				if(shop.canManage(player))
    				{
    					shop.setDisplayName(displayName);
    					sender.sendMessage(Utils.prepareMessage("commands.shopDisplayNameSet", "%shop", shopName, "%shopDisplayName", displayName));
    				}
    				else
    				{
    					sender.sendMessage(Utils.prepareMessage("commands.doNotOwnShop", "%shop", shopName));
    				}
    			}
    			else
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.shopDoesNotExist", "%shop", shopName));
    			}
    		}
    		else if(command.getLabel().equals("esremove")) 
    		{
    			String shopName = args[0];
    			Shop shop = PluginState.getShopList().get(shopName);
    			if(shop != null)
    			{
    				if(shop.canManage(player))
    				{
    					PluginState.getShopList().remove(shopName);
    					sender.sendMessage(Utils.prepareMessage("commands.shopRemoved", "%shop", shopName));
    				}
    				else
    				{
    					sender.sendMessage(Utils.prepareMessage("commands.doNotOwnShop", "%shop", shopName));;
    				}
    			}
    			else
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.shopDoesNotExist", "%shop", shopName));
    			}
    		}
    		else if(command.getLabel().equals("esaddowner"))
    		{
    			String shopName = args[0];
    			Shop shop = PluginState.getShopList().get(shopName);
    			if(shop != null)
    			{
    				if(shop.canManage(player))
    				{
    					String target = args[1];
    					
    					OfflinePlayer targetPlayer = Utils.getPlayer(target);
    					if(targetPlayer != null)
    					{
    						if(shop.addOwner(targetPlayer))
    						{
    							sender.sendMessage(Utils.prepareMessage("commands.shopOwnerAdded", "%shop", shopName, "%player", targetPlayer.getName()));
    						}
    						else
    						{
    							sender.sendMessage(Utils.prepareMessage("commands.shopOwnerNotAdded", "%shop", shopName, "%player", targetPlayer.getName()));
    						}
    					}
    					else
    					{
    						sender.sendMessage(Utils.prepareMessage("commands.playerNotFound", "%player", target));
    					}
    				}
    				else
    				{
    					sender.sendMessage(Utils.prepareMessage("commands.doNotOwnShop", "%shop", shopName));
    				}
    			}
    			else
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.shopDoesNotExist", "%shop", shopName));
    			}
    		}
    		else if(command.getLabel().equals("esremoveowner"))
    		{
    			String shopName = args[0];
    			Shop shop = PluginState.getShopList().get(shopName);
    			if(shop != null)
    			{
    				if(shop.canManage(player))
    				{
    					String target = args[1];
    					OfflinePlayer targetPlayer = Utils.getPlayer(target);
    					if(targetPlayer != null)
    					{
    						if(shop.removeOwner(player, targetPlayer))
    						{
    							sender.sendMessage(Utils.prepareMessage("commands.shopOwnerRemoved", "%shop", shopName, "%player", targetPlayer.getName()));
    						}
    						else
    						{
    							sender.sendMessage(Utils.prepareMessage("commands.shopOwnerNotRemoved", "%shop", shopName, "%player", targetPlayer.getName()));
    						}
    					}
    					else
    					{
    						sender.sendMessage(Utils.prepareMessage("commands.playerNotFound", "%player", targetPlayer.getName()));
    					}
    				}
    				else
    				{
    					sender.sendMessage(Utils.prepareMessage("commands.doNotOwnShop", "%shop", shopName));
    				}
    			}
    			else
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.shopDoesNotExist", "%shop", shopName));
    			}
    		}
    		else if(command.getLabel().equals("essetshop"))
    		{
    			int npcid = -1;
    			int i = 0;
    			
    			// Attempt to parse an NPC id from the command
    			try
    			{
    				npcid = Integer.parseInt(args[0]);
    				i = 1;
    			}
    			catch(Exception e)
    			{	
    			}	
    			
    			// Process the arguments to remove the NPC indicator. 
    			String[] newArgs = new String[args.length-i];
    			for (int j = i; j < args.length; j++) 
    			{
    				newArgs[j-i] = args[j];
    			}
    			
    			NPC npc;
    			if (npcid == -1)
    			{
    				// Sender didn't specify an id, use their selected NPC.
    				npc = ((Citizens) PluginState.getPlugin().getServer().getPluginManager().getPlugin("Citizens")).getNPCSelector().getSelected(sender);
    				if(npc != null )
    				{
    					// Gets NPC Selected for this sender
    					npcid = npc.getId();
    				}
    				else
    				{
    					//no NPC selected.
    					sender.sendMessage(Utils.prepareMessage("commands.noNPCSelected"));
    					return true;
    				}			
    			}
    			
    			npc = CitizensAPI.getNPCRegistry().getById(npcid); 
    			if (npc == null) 
    			{
    				// Specified NPC doesn't exist.
    				sender.sendMessage(Utils.prepareMessage("commands.npcNotFound", "%npcID", npcid+""));
    				return true;
    			}
    			
    			EconomySimMerchantTrait trait = null;
    			if (!npc.hasTrait(EconomySimMerchantTrait.class)) 
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.mustBePerformedOnTrait", "%trait", "economysim"));
    				return true;
    			}
    			else
				{
    				trait = npc.getTrait(EconomySimMerchantTrait.class);
				}
    			
    			String shopName = newArgs[0];
    			Shop shop = PluginState.getShopList().get(shopName);
    			if(shop != null)
    			{
    				if(shop.canManage(player))
    				{
    					trait.setShop(shopName);
    					sender.sendMessage(Utils.prepareMessage("commands.npcShopSet", "%shop", shopName, "%npc", npc.getName()));
    				}
    				else
    				{
    					sender.sendMessage(Utils.prepareMessage("commands.doNotOwnShop", "%shop", shopName));
    				}
    			}
    			else
    			{
    				sender.sendMessage(Utils.prepareMessage("commands.shopDoesNotExist", "%shop", shopName));
    			}
    		}
    		else
    		{
    			sender.sendMessage(Utils.prepareMessage("commands.invalidCommand"));
    			return false;
    		}
	    			
	    	
		    return true;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	    
		return false;
	}

}
