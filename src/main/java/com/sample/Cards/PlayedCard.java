package com.sample.Cards;
import com.sample.Players.PlayerInGame;

public class PlayedCard {
	
	private final int turn;
	private final Card card;
	private final PlayerInGame player;
	
	public PlayedCard(Card card, PlayerInGame player, int turn) {
		this.card = card;
		this.player = player;
		this.turn = turn;
	}
	
	public Card getCard() {
		return card;
	}

	public int getTurn() {
		return turn;
	}

	public PlayerInGame getPlayer() {
		return player;
	}
}
