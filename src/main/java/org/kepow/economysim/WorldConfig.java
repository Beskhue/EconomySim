package org.kepow.economysim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.configuration.MemorySection;

/**
 * Class that represents the World Configuration used in the plugin.
 * 
 * @author Thomas Churchman
 *
 */
public class WorldConfig 
{
	private final String DEFAULT_GROUP = "default";
	
	private Map<String, List<String>> worldGroups;
	private Map<String, Map<String, Object>> groupsConfig;
	
	/**
	 * Constructor.
	 * @param worldGroupsData A configuration map to construct the WorldConfig out of.
	 */
	public WorldConfig(Map<String, Object> worldGroupsData)
	{
		worldGroups = new HashMap<String, List<String>>();
		groupsConfig = new HashMap<String, Map<String, Object>>();
		
		MemorySection groupsDataSection = (MemorySection)worldGroupsData.get("groups");

		Map<String, Object> groupsData = groupsDataSection.getValues(false);
		for(String key : groupsData.keySet())
		{
			//worldGroups.put(key, (List<String>) groupsData.get(key));
			MemorySection groupDataSection = (MemorySection)groupsData.get(key);
			Map<String, Object> groupData = groupDataSection.getValues(false);
			
			if(groupData.containsKey("config"))
			{
				MemorySection groupConfigDataSection = (MemorySection)groupData.get("config");
				groupsConfig.put(key, groupConfigDataSection.getValues(false));
			}
			else
			{
				groupsConfig.put(key, new HashMap<String, Object>());
			}
			
			if(groupData.containsKey("worlds"))
			{
				worldGroups.put(key, groupDataSection.getStringList("worlds"));
			}
		}
	}
	
	/**
	 * Get the group a world belongs to.
	 * @param world The world to get the group for.
	 * @return The group the world belongs to.
	 */
	public String getGroupFromWorld(World world)
	{
		return getGroupFromWorld(world.getName());
	}
	
	/**
	 * Get the group a world belongs to.
	 * @param world  the world name of the world to get the group for.
	 * @return The group the world belongs to.
	 */
	public String getGroupFromWorld(String world)
	{
		String group = DEFAULT_GROUP;
		
		for(String grp : worldGroups.keySet())
		{
			if(worldGroups.get(grp).contains(world))
			{
				group = grp;
				break;
			}
		}
		
		return group;
	}
	
	/**
	 * Get all world groups.
	 * @return The set of world groups.
	 */
	public Set<String> getWorldGroups()
	{
		return worldGroups.keySet();
	}
	
	/**
	 * Get the default base price.
	 * @return The default base price.
	 */
	public double getBasePrice()
	{
		return getBasePrice(DEFAULT_GROUP);
	}
	
	/**
	 * Get the base price of a group.
	 * @param group The group to get the base price for.
	 * @return The base price of the group, or the default base price
	 * if the group has no base price set.
	 */
	public double getBasePrice(String group)
	{
		if(groupsConfig.get(group).containsKey("basePrice"))
		{
			return (Double) groupsConfig.get(group).get("basePrice");	
		}
		else
		{
			// Get default group setting
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("basePrice");
		}
	}
	
	/**
	 * Get the sell price steepness of a group.
	 * @param group The group to get the sell price steepness for.
	 * @return The sell price steepness of the group, or the default sell price steepness
	 * if the group has no sell price steepness set.
	 */
	public double getSellPriceSteepness(String group)
	{
		if(groupsConfig.get(group).containsKey("sellPriceSteepness"))
		{
			return (Double) groupsConfig.get(group).get("sellPriceSteepness");	
		}
		else
		{
			// Get default group setting
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("sellPriceSteepness");
		}
	}
	
	/**
	 * Get the buy price steepness of a group.
	 * @param group The group to get the buy price steepness for.
	 * @return The buy price steepness of the group, or the default buy price steepness
	 * if the group has no buy price steepness set.
	 */
	public double getBuyPriceSteepness(String group)
	{
		if(groupsConfig.get(group).containsKey("buyPriceSteepness"))
		{
			return (Double) groupsConfig.get(group).get("buyPriceSteepness");	
		}
		else
		{
			// Get default group setting
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("buyPriceSteepness");
		}
	}
	
	/**
	 * Get the buy price asymptote slope of a group.
	 * @param group The group to get the buy price asymptote slope for.
	 * @return The buy price asymptote slope of the group, or the default buy price asymptote slope
	 * if the group has no buy price asymptote slope set.
	 */
	public double getBuyPriceAsymptoteSlope(String group)
	{
		if(groupsConfig.get(group).containsKey("buyPriceAsymptoteSlope"))
		{
			return (Double) groupsConfig.get(group).get("buyPriceAsymptoteSlope");	
		}
		else
		{
			// Get default group setting
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("buyPriceAsymptoteSlope");
		}
	}
	
	/**
	 * Get the sell price factor of a group.
	 * @param group The group to get the sell price factor for.
	 * @return The sell price factor of the group, or the default sell price factor 
	 * if the group has no sell price factor set.
	 */
	public double getSellPriceFactor(String group)
	{
		if(groupsConfig.get(group).containsKey("sellPriceFactor"))
		{
			return (Double) groupsConfig.get(group).get("sellPriceFactor");	
		}
		else
		{
			// Get default group setting
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("sellPriceFactor");
		}
	}
	
	/**
	 * Get the buy price factor of a group.
	 * @param group The group to get the buy price factor for.
	 * @return The buy price factor of the group, or the default buy price factor 
	 * if the group has no buy price factor set.
	 */
	public double getBuyPriceFactor(String group)
	{
		if(groupsConfig.get(group).containsKey("buyPriceFactor"))
		{
			return (Double) groupsConfig.get(group).get("buyPriceFactor");	
		}
		else
		{
			// Get default group setting
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("buyPriceFactor");
		}
	}
	
	/**
	 * Get the sale decay per day of a group.
	 * @param group The group to get the sale decay per day for.
	 * @return The sale decay per day of the group, or the default sale decay per day 
	 * if the group has no sale decay per day set.
	 */
	public double getSaleDecayPerDay(String group)
	{
		if(groupsConfig.get(group).containsKey("saleDecayPerDay"))
		{
			return (Double) groupsConfig.get(group).get("saleDecayPerDay");	
		}
		else
		{
			// Get default group setting
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("saleDecayPerDay");
		}
	}
	
	/**
	 * Get the buy decay per day of a group.
	 * @param group The group to get the buy decay per day for.
	 * @return The buy decay per day of the group, or the default buy decay per day 
	 * if the group has no buy decay per day set.
	 */
	public double getBuyDecayPerDay(String group)
	{
		if(groupsConfig.get(group).containsKey("buyDecayPerDay"))
		{
			return (Double) groupsConfig.get(group).get("buyDecayPerDay");	
		}
		else
		{
			// Get default group setting
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("buyDecayPerDay");
		}
	}
}
