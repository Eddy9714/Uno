package com.sample.Cards;
import com.sample.Players.PlayerInGame;

public class PlayedCard {
	
	private final int turn;
	private final Card card;
	private final PlayerInGame player;
	
	public PlayedCard(PlayerInGame player, int cardIndex, int turn) {
		assert(cardIndex >= 0 && cardIndex < player.getCards().size());
		this.card = player.getCards().get(cardIndex);
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
