package org.kepow.economysim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents the Item Configuration used in the plugin.
 * 
 * @author Thomas Churchman
 *
 */
public class ItemConfig 
{
	/**
	 * Class that represents an "item map". For example, the diamond block ItemStack
	 * might map to an ItemMap with as ItemStack diamond (non-block) and relativeValue 9.
	 * 
	 * Another example are damaged tools, that might map to an ItemMap with as item the
	 * non-damaged tool and a health of, e.g., 0.6, if the tool was damaged 60%.
	 * 
	 * @author Thomas Churchman
	 *
	 */
	public class ItemMap
	{
		private ItemStack item;
		private int relativeValue;
		private double health;
		
		/**
		 * Constructor.
		 * @param item The item stack the map should represent.
		 */
		public ItemMap(ItemStack item)
		{
			this(item, 1);
		}
		
		/**
		 * Constructor.
		 * @param item The item stack the map should represent.
		 * @param relativeValue The relative value of the original item to the item stack represented by the map.
		 */
		public ItemMap(ItemStack item, int relativeValue)
		{
			this(item, relativeValue, 1.0);
		}
		
		/**
		 * Constructor.
		 * @param item The item stack the map should represent.
		 * @param health The health of the original item (the item stack represented should always have a health of 100%).
		 */
		public ItemMap(ItemStack item, double health)
		{
			this(item, 1, health);
		}
		
		/**
		 * Constructor.
		 * @param item The item stack the map should represent.
		 * @param relativeValue The relative value of the original item to the item stack represented by the map.
		 * @param health The health of the original item (the item stack represented should always have a health of 100%).
		 */
		public ItemMap(ItemStack item, int relativeValue, double health)
		{
			this.item = item;
			this.relativeValue = relativeValue;
			this.health = health;
		}
		
		/**
		 * Constructor.
		 * @param map A tuple containing an item stack the map should represent and the relative value of the original item 
		 * to the item stack represented by the map. 
		 */
		public ItemMap(Tuple<ItemStack, Integer> map)
		{
			this(map, 1.0);
		}
		
		/**
		 * Constructor.
		 * @param map A tuple containing an item stack the map should represent and the relative value of the original item 
		 * to the item stack represented by the map. 
		 * @param health The health of the original item (the item stack represented should always have a health of 100%).
		 */
		public ItemMap(Tuple<ItemStack, Integer> map, double health)
		{
			this(map.t1, map.t2, health);
		}
		
		public ItemStack getItem()
		{
			return this.item;
		}
		
		public int getRelativeValue()
		{
			return this.relativeValue;
		}
		
		public double getHealth()
		{
			return this.health;
		}
		
		@Override
		public boolean equals(Object other)
		{
			if(!(other instanceof ItemMap))
			{
				return false;
			}
			
			ItemMap otherMap = (ItemMap) other;
			return otherMap.item.equals(this.item) && otherMap.relativeValue == this.relativeValue && otherMap.health == this.health;
		}
		
		@Override
		public int hashCode()
		{
			int hash = 1;
			hash = hash * 17 + item.toString().hashCode();
			hash = hash * 31 + relativeValue;
			hash = hash * 13 + (int) (health * 100);
			
			return hash;
		}
	}
	
	private Map<ItemStack, String> mapToGroup;
	private Map<String, Boolean> groupAllDataTypes;
	private Map<ItemStack, Tuple<ItemStack, Integer>> mapTo;
	
	/**
	 * Constructor.
	 * @param itemGroupsData A configuration map to construct the ItemConfig out of.
	 */
	public ItemConfig(Map<String, Object> itemGroupsData)
	{
		mapToGroup = new HashMap<ItemStack, String>();
		groupAllDataTypes = new HashMap<String, Boolean>();
		mapTo = new HashMap<ItemStack, Tuple<ItemStack, Integer>>();
		
		for(String group : itemGroupsData.keySet())
		{
			MemorySection groupData = ((MemorySection)itemGroupsData.get(group));
			
			List<Map<?,?>> items = groupData.getMapList("items");
			
			boolean groupAllDataTypes = groupData.getBoolean("allDataTypes");
			this.groupAllDataTypes.put(group, groupAllDataTypes);
							
			
			//List<Map<?, ?>> items = (List<Map<?, ?>>) itemGroupData.get("items");
			if(items.size() > 0)
			{
				ItemStack prototypeItem = (ItemStack) items.get(0).get("item"); 
				
				for(Map<?, ?> item : items)
				{
					ItemStack itemStack = (ItemStack) item.get("item");
					mapToGroup.put(itemStack, group);
					int relativeValue = 1;
					
					if(item.containsKey("relativeValue"))
					{
						relativeValue = (Integer) item.get("relativeValue");
					}
					
					mapTo.put(itemStack, new Tuple<ItemStack,Integer>(prototypeItem, relativeValue));
				}
			}
		}
	}
	
	/**
	 * Get the mapping of an item stack.
	 * @param item The item stack to get the mapping for.
	 * @return An ItemMap containing the item stack the input item stack maps to
	 * the quantitative value relative to the item stack it maps to and the 
	 * durability of the item, if relevant.
	 */
	public ItemMap getMapping(ItemStack item)
	{
		short max = item.getType().getMaxDurability();
		double health = 1.0;
		if(max != 0)
		{	// The item has durability
			health = (double) (max - item.getDurability()) / (double) max;						
		}
		
		if(mapTo.containsKey(item))
		{	// We found a mapping for this item.
			return new ItemMap(mapTo.get(item), health);
		}
		else
		{	// No "simple" mapping was found, check if we have a mapping
			// when we discard item data/damage information and whether that
			// group is defined for all item data/damage types.
			ItemStack checkItem = item;
			checkItem.setDurability((short) 0);
			
			if(mapToGroup.containsKey(checkItem))
			{	// A group exists for the "bare" item
				
				String group = mapToGroup.get(checkItem);
				
				if(groupAllDataTypes.get(group))
				{	// The group is defined for all item data/damage types.
					return new ItemMap(mapTo.get(checkItem), health);
				}
				else
				{	
					if(max != 0)
					{	// The item has durability
						return new ItemMap(mapTo.get(checkItem), health);						
					}
					else
					{	// The group is not defined for all item data/damage types,
						// return the original item.
						return new ItemMap(item, health);
					}
				}
			}
			else
			{	// No group exists for the "bare" item either,
				// return the original item.
				return new ItemMap(item, health);
			}
		}
	}
}
