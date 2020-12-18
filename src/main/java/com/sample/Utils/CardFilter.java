package com.sample.Utils;

import java.util.ArrayList;

import com.sample.Cards.Card;
import com.sample.Cards.PlayedCard;

public class CardFilter {
		
	public static ArrayList<Card> filterByCard(ArrayList<Card> cards, Card card, CardTest test) {
		ArrayList<Card> cardsFiltered = new ArrayList<Card>();
		for(Card c : cards) {
			if(test.test(c))
				cardsFiltered.add(c);
		}
		return cardsFiltered;
	}
	
	public static ArrayList<Card> filterByLastCard(ArrayList<Card> cards, Card card, PlayedCard playedCard, CardsTest test) {
		ArrayList<Card> cardsFiltered = new ArrayList<Card>();
		for(Card c : cards) {
			if(test.test(c, playedCard))
				cardsFiltered.add(c);
		}
		return cardsFiltered;
	}
	
}
