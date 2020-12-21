package com.sample.Utils;

public class Option {
	
	public static enum TYPE {COLOR_CHOICE, CARD_CHOICE, PASS_TURN, DRAW, DECLARE_UNO, CHARGE_UNO_MISSED, CHOICE_YES, CHOICE_NO, PLAYER_CHOICE}; 
	private final TYPE type;
	private final String message;
	private final Object value;
	
	public Option(TYPE type, Object value, String message) {
		this.type = type;
		this.value = value;
		this.message = message;
	}

	public TYPE getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public Object getValue() {
		return value;
	}
}
