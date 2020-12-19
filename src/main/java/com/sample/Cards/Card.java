package com.sample.Cards;

public abstract class Card {
	
	public static enum CARD_TYPE {NORMAL, ACTION};
	public static enum COLOR {YELLOW, GREEN, RED, BLUE};
	
	protected COLOR color;
	protected CARD_TYPE type;
	
	public Card(CARD_TYPE type, COLOR color) {
		this.type = type;
		this.color = color;
	}
	
	public COLOR getColor() {
		return color;
	}
	
	public void setColor(COLOR color) {
		this.color = color;
	}
	
	public CARD_TYPE getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Card [color=" + color + ", type=" + type + "]";
	}
}
