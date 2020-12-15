package com.sample.Cards;

public abstract class Card {
	public static enum CARD_TYPE {NORMAL, ACTION};
	public CARD_TYPE type;
	
	public Card(CARD_TYPE type) {
		this.type = type;
	}
	
}
