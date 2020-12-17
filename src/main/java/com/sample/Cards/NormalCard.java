package com.sample.Cards;

public class NormalCard extends Card {
	
	private final int number;
	
	public NormalCard(int number, COLOR color) {
		super(Card.CARD_TYPE.NORMAL, color);
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public String toString() {
		return "[" + number + " " + color + "]";
	}
	
}


