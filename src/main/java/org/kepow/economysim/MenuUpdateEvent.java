package org.kepow.economysim;

/**
 * Class representing a menu update event.
 * 
 * @author Thomas Churchman
 *
 */
public class MenuUpdateEvent 
{
	private final Menu menu;
	
	private boolean cancelled = false;
	
	/**
	 * Constructor.
	 * @param menu The menu the event is for.
	 */
	public MenuUpdateEvent(Menu menu)
	{
		this.menu = menu;
	}
	
	/**
	 * Set the cancellation state of the event. A cancelled event
	 * will not update buttons and will not refresh the inventory.
	 * @param cancelled Boolean indicating cancellation. True to cancel, false to not cancel. 
	 */
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
	
	/**
	 * Get the cancellation state of the event.
	 * @return The cancellation state.
	 */
	public boolean getCancelled()
	{
		return this.cancelled;
	}
	
	/**
	 * Get the menu this event belongs to.
	 * @return The menu this event belongs to.
	 */
	public Menu getMenu()
	{
		return this.menu;
	}
}
