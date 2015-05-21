package org.kepow.economysim;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

/**
 * Class to represent a conversation prompt asking a player 
 * to enter the number of rows the buy menu of the shop
 * should have. 
 * 
 * @author Thomas Churchman
 *
 */
public class ManageNumBuyRows extends NumericPrompt
{
	private final Shop shop;
	private final boolean incorrectInput;
	
	/**
	 * Constructor.
	 * @param shop The shop this prompt is for.
	 */
	public ManageNumBuyRows(Shop shop)
	{
		this(shop, false);
	}
	
	/**
	 * Constructor.
	 * @param shop The shop this prompt is for.
	 * @param incorrectInput Whether the prompt has been filled in already erroneously.
	 */
	public ManageNumBuyRows(Shop shop, boolean incorrectInput)
	{
		this.shop = shop;
		this.incorrectInput = incorrectInput;
	}

	/**
	 * Get the prompt text to send to the player.
	 */
	public String getPromptText(ConversationContext arg0) 
	{
		if(incorrectInput)
		{
			return Utils.prepareMessage("conversations.numBuyRowsError", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName());
		}
		else
		{
			return Utils.prepareMessage("conversations.numBuyRows", "%shop", shop.getName(), "%shopDisplayName", shop.getDisplayName());
		}
	}

	/**
	 * Process validated input.
	 */
	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, Number arg1) 
	{
		int number = arg1.intValue();
		if(number >= 1 && number <= 6)
		{
			shop.setNumBuyRows(number);
			arg0.getForWhom().sendRawMessage(Utils.prepareMessage("conversations.numBuyRowsSet", 
					"%shop", shop.getName(), 
					"%shopDisplayName", shop.getDisplayName(),
					"%num", number+""));
			return END_OF_CONVERSATION;
		}
		else
		{
			return new ManageNumBuyRows(shop, true);
		}
	}

}
