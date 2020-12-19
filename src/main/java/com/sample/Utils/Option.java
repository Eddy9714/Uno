package com.sample.Utils;

public class Option {
	
	public static enum TYPE {COLOR_CHOICE, CARD_CHOICE, PASS_TURN, DRAW, DECLARE_UNO, CHARGE_UNO_MISSED, CHOICE_YES, CHOICE_NO}; 
	private final TYPE type;
	private final String message;
	
	public Option(TYPE type, String message) {
		this.type = type;
		this.message = message;
	}

	public TYPE getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
}
