package com.sample;
import java.util.ArrayList;
import java.util.UUID;

import java.util.Collections;

import com.sample.Cards.ActionCard;
import com.sample.Cards.Card;
import com.sample.Cards.PlayedCard;
import com.sample.Cards.NormalCard;
import com.sample.Players.Player;
import com.sample.Players.PlayerInGame;
import com.sample.Utils.CardTest;
import com.sample.Utils.CardsTest;

public class Game {
	
	public final int MAX_PLAYERS;
	public static final int CARDS_TO_DEAL = 7;
	
	public static enum GAME_STATUS {INIT, BEGIN, PLAY, END};
	public static enum PHASE_STATUS {DEAL_CARDS, FIRST_CARD, EVAL_FIRST_CARD, EVAL_CARD, MAIN, TURN_START, TURN_END};
	
	private String id;
	private int turn = 0;
	private GAME_STATUS status = GAME_STATUS.INIT;
	private PHASE_STATUS phaseStatus = null;
	
	private boolean directionLeft = true;
	
	private final ArrayList<PlayerInGame> playersInGame = new ArrayList<PlayerInGame>();
	private boolean nextToSkip = false;
	
	private final ArrayList<Card> pile = new ArrayList<Card>();//mazzo

	private final ArrayList<PlayedCard> discardPile = new ArrayList<PlayedCard>();//scarti
	
	private PlayedCard cardToEvaluate = null;
	
	/*COSTRUTTORI*/
	
	public Game(int maxPlayers) {
		this.MAX_PLAYERS = maxPlayers;
		id = UUID.randomUUID().toString();
	}
	
	public Game(int maxPlayers, ArrayList<Player> players) {
		this.MAX_PLAYERS = maxPlayers;
		
		id = UUID.randomUUID().toString();
		for(Player p : players) {
			if(!addPlayer(p))
				break;
		}
	}
	
	public Game(int maxPlayers, Player... players) {
		this.MAX_PLAYERS = maxPlayers;
		
		id = UUID.randomUUID().toString();
		for(Player p : players)
			if(!addPlayer(p))
				break;
	}
	
	/*FUNZIONI*/
	
	public void createDeterministicPile() {
		NormalCard.COLOR[] colors = NormalCard.COLOR.values();
		pile.clear();
		
		for (int k=0;k<30;k++) {			
			//Carte speciali
			for(int i=0;i<colors.length; i++) {
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.DRAW_TWO, colors[i]));;
			}
		}
	}
	
	public void createPile() {
		NormalCard.COLOR[] colors = NormalCard.COLOR.values();
		
		pile.clear();
		
		// Quattro 0
		
		for(int k=0;k<colors.length; k++) {
			pile.add(new NormalCard(0, colors[k]));
		}
		
		// quattro +4, quattro cambia colore
		for(int k=0;k<4;k++) {
			pile.add(new ActionCard(ActionCard.ACTION_TYPE.WILD));
			pile.add(new ActionCard(ActionCard.ACTION_TYPE.WILD_DRAW_FOUR));
		}
		
		for (int k=0;k<2;k++) {
			//Numeri da 1 a 9
			for(int j=1;j<10;j++) {
				for(int i=0;i<colors.length; i++) {
					pile.add(new NormalCard(j, colors[i]));
				}
			}
			
			//Carte speciali
			for(int i=0;i<colors.length; i++) {
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.SKIP, colors[i]));
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.REVERSE, colors[i]));
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.DRAW_TWO, colors[i]));
			}
		}
		
		for(int k=0;k<100;k++) {
			shufflePile();
		}
	}
	
	public void shufflePile() {
		Collections.shuffle(pile);
	}
	
	public Card dealCardToPlayer(PlayerInGame p) {
		return dealCardsToPlayer(p, 1).get(0);
	}
	
	public ArrayList<Card> dealCardsToPlayer(PlayerInGame p, int number) {
		
		ArrayList<Card> cards = new ArrayList<Card>(number);
		for(int k=0;k<number;k++) {
			if(pile.size() > 0) {
				Card card = pile.remove(pile.size() - 1);
				cards.add(card);
			}
			else {
				System.out.println(pile.size());
				System.out.println(discardPile.size());
				
				PlayedCard topCard = discardPile.remove(discardPile.size() - 1);
				for(PlayedCard cardPlayed : discardPile){
					pile.add(cardPlayed.getCard());
				}
				discardPile.clear();
				discardPile.add(topCard);
				shufflePile();
				k--;
			}
		}
		
		p.addCards(cards);
		
		return cards;
	}
	
	public void putAndShuffle(Card card) {
		pile.add(card);
		shufflePile();
	}
	
	public void printCardsList(ArrayList<Card> cards) {
		for(Card card : cards){
			System.out.print(card + " ");
		}
		System.out.println();
	}
	
	public ArrayList<Card> getPlayableCards(PlayerInGame p) {
		PlayedCard lastCard = null;
		
		if(discardPile.size() > 0)
			lastCard = discardPile.get(discardPile.size() - 1);
		
		return getPlayableCards(p, lastCard);
	}
	
	public ArrayList<Card> getPlayableCards(PlayerInGame p, PlayedCard playedCard) {
		ArrayList<Card> playableCards = new ArrayList<Card>();
		
		for(Card card : p.getCards()) {
			if(isCardPlayable(p, card, playedCard)){
				playableCards.add(card);
			}
		}
		
		return playableCards;
	}
	
	public boolean isCardPlayable(PlayerInGame p, Card card) {
		PlayedCard lastCard = null;
		
		if(discardPile.size() > 0)
			lastCard = discardPile.get(discardPile.size() - 1);
		
		return isCardPlayable(p, card, lastCard);
	}

	
	public boolean isCardPlayable(PlayerInGame p, Card card, PlayedCard playedCard) {		
		if(playedCard == null)
			return true;
		
		CardTest isNormalCard = cardToTest -> {
			return cardToTest.getType() == Card.CARD_TYPE.NORMAL;
		};
		
		CardTest isActionCard = cardToTest -> {
			return cardToTest.getType() == Card.CARD_TYPE.ACTION;
		};
		
		CardsTest sameActionCard = (cardToTest, lastPlayedCard) -> {
			return ((ActionCard)cardToTest).getActionType() == ((ActionCard)(lastPlayedCard.getCard())).getActionType();
		};
		
		CardsTest sameNumber = (cardToTest, lastPlayedCard) -> {			
			return ((NormalCard)cardToTest).getNumber() == ((NormalCard)(lastPlayedCard.getCard())).getNumber();
		};
		
		CardsTest sameColor = (cardToTest, lastPlayedCard) -> {
			return cardToTest.getColor() == lastPlayedCard.getCard().getColor();
		};
		
		CardTest noColor = (cardToTest) -> {
			return cardToTest.getColor() == null;
		};
		
		return (
			sameColor.test(card, playedCard) || noColor.test(card) ||
			(isActionCard.test(card) && isActionCard.test(playedCard.getCard()) && sameActionCard.test(card, playedCard)) || 
			(isNormalCard.test(card) && isNormalCard.test(playedCard.getCard()) && sameNumber.test(card, playedCard))
		);
	}
	
	/* SETTER/GETTER */

	public String getId() {
		return id;
	}
	
	public GAME_STATUS getStatus() {
		return status;
	}

	public void setStatus(GAME_STATUS status) {
		this.status = status;
	}
	
	public PHASE_STATUS getPhaseStatus() {
		return phaseStatus;
	}

	public void setPhaseStatus(PHASE_STATUS phaseStatus) {
		this.phaseStatus = phaseStatus;
	}	

	public ArrayList<PlayerInGame> getPlayersInGame() {
		return playersInGame;
	}
	
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>(playersInGame.size());
		for(PlayerInGame pInGame : playersInGame) {
			players.add(pInGame.getPlayer());
		}
		return players;
	}
	
	public boolean addPlayer(Player p) {
		if(playersInGame.size() >= MAX_PLAYERS)
			return false;
		else {
			playersInGame.add(new PlayerInGame(p));
			return true;
		}
	}
	
	public boolean isDirectionLeft() {
		return directionLeft;
	}

	public void setDirectionLeft(boolean directionLeft) {
		this.directionLeft = directionLeft;
	}
	
	public PlayedCard getCardToEvaluate() {
		return cardToEvaluate;
	}

	public void setCardToEvaluate(PlayedCard cardToEvaluate) {
		this.cardToEvaluate = cardToEvaluate;
	}
	
	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}
	
	public ArrayList<Card> getPile() {
		return pile;
	}
	
	public ArrayList<PlayedCard> getDiscardPile() {
		return discardPile;
	}
	
	public boolean isNextToSkip() {
		return nextToSkip;
	}

	public void setNextToSkip(boolean nextToSkip) {
		this.nextToSkip = nextToSkip;
	}
	
	/*ELEMENTI PER DROOLS*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((playersInGame == null) ? 0 : playersInGame.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		Game other = (Game) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (playersInGame == null) {
			if (other.playersInGame != null)
				return false;
		} else if (!playersInGame.equals(other.playersInGame))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
}
