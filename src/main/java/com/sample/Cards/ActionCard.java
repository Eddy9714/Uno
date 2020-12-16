package com.sample.Cards;

public class ActionCard extends Card {
	
	public static enum ACTION_TYPE {REVERSE, SKIP, DRAW_TWO, WILD, WILD_DRAW_FOUR};
	
	private final ACTION_TYPE actionType; 
	
	public ActionCard(ACTION_TYPE actionType) {
		super(Card.CARD_TYPE.ACTION);
		this.actionType = actionType;
	}

	public ACTION_TYPE getActionType() {
		return actionType;
	}
}
