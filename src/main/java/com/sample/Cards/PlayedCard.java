package com.sample.Cards;
import java.util.ArrayList;

import com.sample.Players.PlayerInGame;

public class PlayedCard {
	
	private final int turn;
	private final ArrayList<Card> cards = new ArrayList<Card>(1);
	private final PlayerInGame player;
	
	public PlayedCard(PlayerInGame player, int cardIndex, int turn) {
		assert(cardIndex >= 0 && cardIndex < player.getCards().size());
		cards.add(player.getCards().get(cardIndex));
		this.player = player;
		this.turn = turn;
	}
	
	public Card getCard() {
		return cards.get(0);
	}

	public int getTurn() {
		return turn;
	}

	public PlayerInGame getPlayer() {
		return player;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayedCard other = (PlayedCard) obj;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		return true;
	}
}
