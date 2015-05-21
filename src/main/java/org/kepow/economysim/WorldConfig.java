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
		MemorySection groupsConfigDataSection = (MemorySection)worldGroupsData.get("groupConfig");
		
		Map<String, Object> groupsData = groupsDataSection.getValues(false);
		for(String key : groupsData.keySet())
		{
			worldGroups.put(key, (List<String>) groupsData.get(key));
		}
		
		Map<String, Object> groupsConfigData = groupsConfigDataSection.getValues(false);
		for(String key : groupsConfigData.keySet())
		{
			MemorySection groupConfigDataSection = (MemorySection) groupsConfigData.get(key);
			Map<String, Object> groupConfigData = groupConfigDataSection.getValues(false);
			groupsConfig.put(key, groupConfigData);
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
			// Get default group base price
			return (Double) groupsConfig.get(DEFAULT_GROUP).get("basePrice");
		}
		
	}
}
