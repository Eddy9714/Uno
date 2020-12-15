package com.sample.Cards;

public class ActionCard extends Card {
	
	public static enum ACTION_TYPE {REVERSE, SKIP, DRAW_TWO, WILD, WILD_DRAW_FOUR};
	
	private final ACTION_TYPE type; 
	
	public ActionCard(ACTION_TYPE type) {
		super(Card.CARD_TYPE.ACTION);
		this.type = type;
	}
}
