package com.sample.Cards;

public abstract class Card {
	
	public static enum CARD_TYPE {NORMAL, ACTION};
	public static enum COLOR {YELLOW, GREEN, RED, BLUE};
	
	private COLOR color;
	public CARD_TYPE type;
	
	public Card(CARD_TYPE type, COLOR color) {
		this.type = type;
		this.color = color;
	}
	
	protected COLOR getColor() {
		return color;
	}
	
}
