package com.sample.Utils;
import com.sample.Cards.Card;
import java.util.ArrayList;
import com.sample.Players.PlayerInGame;

public class PlayerCards {
	
	private ArrayList<Card> cards;
	private PlayerInGame player;
	
	public PlayerCards(PlayerInGame player, ArrayList<Card> cards) {
		this.cards = cards;
		this.player = player;
	}
}
