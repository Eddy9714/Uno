package com.sample.Utils;
import com.sample.Cards.Card;
import java.util.ArrayList;
import com.sample.Players.PlayerInGame;

public class PlayerCards {
	
	private final ArrayList<Card> cards;
	private final PlayerInGame player;
	
	public PlayerCards(PlayerInGame player, ArrayList<Card> cards) {
		this.cards = cards;
		this.player = player;
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public PlayerInGame getPlayer() {
		return player;
	}
}
