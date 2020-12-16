package com.sample.Utils;
import com.sample.Cards.Card;
import java.util.ArrayList;
import com.sample.Players.PlayerInGame;

public class PlayerCards {
	
	private ArrayList<Card> cards;
	private PlayerInGame player;
	
	public PlayerCards(PlayerInGame player, ArrayList<Card> cards) {
		this.setCards(cards);
		this.setPlayer(player);
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}

	public PlayerInGame getPlayer() {
		return player;
	}

	public void setPlayer(PlayerInGame player) {
		this.player = player;
	}
}
