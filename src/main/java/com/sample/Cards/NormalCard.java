package com.sample.Cards;

import com.sample.Cards.Card.CARD_TYPE;

public class NormalCard extends Card {
	
	private final int number;
	public static enum COLOR {YELLOW, GREEN, RED, BLUE};
	private COLOR color;
	
	public NormalCard(int number, COLOR color) {
		super(Card.CARD_TYPE.NORMAL);
		this.number = number;
		this.color = color;
	}

	public int getNumber() {
		return number;
	}

	public COLOR getColor() {
		return color;
	}
	
}
