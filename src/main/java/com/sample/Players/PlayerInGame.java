package com.sample.Players;

import java.util.ArrayList;

import com.sample.Cards.Card;

public class PlayerInGame{
	
	public static enum ROLE {NORMAL, DEALER};
	
	private Player player;
	private ROLE role;
	private ArrayList<Card> cards = new ArrayList<Card>();
	private boolean playerTurn = false;//decide il turno semaforo
	private boolean blocked = false; //stabilisce se il giocatore deve passare il suo turno
	
	private boolean hasDrawn = false;
	private boolean hasPlayedCards = false;
	private boolean unoDeclared = false;
	private boolean hasCharged = false;
	private boolean isFirst = false;
	
	public PlayerInGame(Player p) {
		this.player = p;
	}
	
	public ArrayList<Card> getCards() {
		return cards;
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}
	

	public Player getPlayer() {
		return player;
	}

	public ROLE getRole() {
		return role;
	}

	public void setRole(ROLE role) {
		this.role = role;
	}

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
	}
	
	public void addCard(Card card) {
		this.cards.add(card);
	}
	
	public void addCards(ArrayList<Card> cards) {
		for(Card card : cards)
			addCard(card);
	}
	
	public void removeCard(Card card) {
		this.cards.remove(card);
	}
	
	public void removeCards(ArrayList<Card> cards) {
		for(Card card : cards)
			removeCard(card);
	}

	public boolean isUnoDeclared() {
		return unoDeclared;
	}

	public void setUnoDeclared(boolean unoDeclared) {
		this.unoDeclared = unoDeclared;
	}

	public boolean hasDrawn() {
		return hasDrawn;
	}

	public void setHasDrawn(boolean hasDraw) {
		this.hasDrawn = hasDraw;
	}

	public boolean hasPlayedCards() {
		return hasPlayedCards;
	}

	public void setHasPlayedCards(boolean hasPlayedCards) {
		this.hasPlayedCards = hasPlayedCards;
	}

	public boolean hasCharged() {
		return hasCharged;
	}

	public void setHasCharged(boolean hasCharged) {
		this.hasCharged = hasCharged;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
}
