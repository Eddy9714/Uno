package com.sample.Utils;

import com.sample.Cards.Card;
import com.sample.Players.PlayerInGame;

public class PlayerCard {

	private final Card card;
	private final PlayerInGame player;
	
	public PlayerCard(PlayerInGame player, Card card) {
		this.card = card;
		this.player = player;
	}

	public PlayerInGame getPlayer() {
		return player;
	}

	public Card getCard() {
		return card;
	}
}
