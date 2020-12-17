package com.sample.Cards;

public class ActionCard extends Card {
	
	public static enum ACTION_TYPE {REVERSE, SKIP, DRAW_TWO, WILD, WILD_DRAW_FOUR};
	
	private final ACTION_TYPE actionType; 
	private final boolean quick; //Se true, l'effetto della carta è immediato
	
	public ActionCard(ACTION_TYPE actionType, COLOR color, boolean quick) {
		super(Card.CARD_TYPE.ACTION, color);
		this.actionType = actionType;
		this.quick = quick;
	}
	
	public ActionCard(ACTION_TYPE actionType, boolean quick) {
		super(Card.CARD_TYPE.ACTION, null);
		this.actionType = actionType;
		this.quick = quick;
	}

	public ACTION_TYPE getActionType() {
		return actionType;
	}

	public boolean isQuick() {
		return quick;
	}
}
