package com.sample;
import java.util.ArrayList;
import java.util.UUID;

import javax.persistence.CascadeType;

import java.util.Collections;

import com.sample.Cards.ActionCard;
import com.sample.Cards.Card;
import com.sample.Cards.PlayedCard;
import com.sample.Cards.NormalCard;
import com.sample.Players.Player;
import com.sample.Players.PlayerInGame;
import com.sample.Utils.CardTest;
import com.sample.Utils.CardsTest;
import com.sample.Utils.PlayerCards;

public class Game {
	
	public static final int MAX_PLAYERS = 10;
	public static final int CARDS_TO_DEAL = 7;
	
	public static enum GAME_STATUS {INIT, READY, BEGIN, PLAY, END};
	public static enum PHASE_STATUS {DEAL_CARDS, FIRST_CARD, EVAL_FIRST_CARD, ANSWER, EVAL_CARD, PLAY_CARDS, TURN_START, TURN_END};
	
	private String id;
	private int turn = 1;
	private int lastSkippedPlayerIndex = -1;
	private GAME_STATUS status = GAME_STATUS.INIT;
	private PHASE_STATUS phaseStatus = null;
	
	private boolean directionLeft = true;
	
	private final ArrayList<PlayerInGame> playersInGame = new ArrayList<PlayerInGame>();
	private PlayerInGame lastPlayer = null;
	private final ArrayList<Card> pile = new ArrayList<Card>();//mazzo

	private final ArrayList<PlayedCard> discardPile = new ArrayList<PlayedCard>();//scarti

	private final ArrayList<PlayedCard> normalPendingCards = new ArrayList<PlayedCard>(); //pila di eventi lenti che devono accadere
	private PlayedCard cardToEvaluate = null;
	private boolean stackSolved = false;
	
	/*COSTRUTTORI*/
	
	public Game() {
		id = UUID.randomUUID().toString();
	}
	
	public Game(ArrayList<Player> players) {
		id = UUID.randomUUID().toString();
		for(Player p : players) {
			if(!addPlayer(p))
				break;
		}
			
	}
	
	public Game(Player... players) {
		id = UUID.randomUUID().toString();
		for(Player p : players)
			if(!addPlayer(p))
				break;
	}
	
	/*FUNZIONI*/
	
	public void createDeterministicPile() {
		for(int k=0;k<108;k++) {
			pile.add(new ActionCard(ActionCard.ACTION_TYPE.DRAW_TWO, Card.COLOR.RED, true));
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
			pile.add(new ActionCard(ActionCard.ACTION_TYPE.WILD, true));
			pile.add(new ActionCard(ActionCard.ACTION_TYPE.WILD_DRAW_FOUR, true));
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
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.SKIP, colors[i], true));
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.REVERSE, colors[i], true));
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.DRAW_TWO, colors[i], false));
			}
		}
		
		shufflePile();
	}
	
	public void shufflePile() {
		Collections.shuffle(pile);
	}
	
	public ArrayList<PlayerCards> dealCardsToPlayers(PlayerInGame dealer, int number) {
		int index = playersInGame.indexOf(dealer);
		
		assert(index != -1);
		
		ArrayList<PlayerCards> list = new ArrayList<PlayerCards>(playersInGame.size());
		
		for(int k = index - 1; k >= 0; k--) {
			ArrayList<Card> cards = dealCardsToPlayer(playersInGame.get(k), number);
			list.add(new PlayerCards(playersInGame.get(k), cards));
		}
		
		for(int k = playersInGame.size() - 1; k >= index; k--) {
			ArrayList<Card> cards = dealCardsToPlayer(playersInGame.get(k), number);
			list.add(new PlayerCards(playersInGame.get(k), cards));
		}
		
		return list;
	}
	
	public Card dealCardToPlayer(PlayerInGame p) {
		return dealCardsToPlayer(p, 1).get(0);
	}
	
	public ArrayList<Card> dealCardsToPlayer(PlayerInGame p, int number) {
		assert(number >= 0);
		
		ArrayList<Card> cards = new ArrayList<Card>(number);
		for(int k=0;k<number;k++) {
			if(pile.size() > 0) {
				Card card = pile.remove(pile.size() - 1);
				cards.add(card);
			}
			else {
				assert(discardPile.size() >= number - k);
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
	
	//setCardToEvaluate(new PlayedCard($player, indice_carta, turno))
	
	public void playCard(PlayerInGame p, int index) {
		
		assert(index < p.getCards().size() && index >= 0);
		
		PlayedCard lastCard = null;
		
		if(discardPile.size() > 0)
			lastCard = discardPile.get(discardPile.size() - 1);
		
		assert(isCardPlayable(p, p.getCards().get(index), lastCard, false));
		
		PlayedCard playedCard = new PlayedCard(p, index, turn);
		
		discardPile.add(playedCard);
		
		if(p.getCards().get(index).getClass() == ActionCard.class) {
			ActionCard actionCard = (ActionCard)p.getCards().get(index);
			
			if(actionCard.isQuick()) {
				setCardToEvaluate(playedCard); 
				
				if(actionCard.getActionType() == ActionCard.ACTION_TYPE.WILD_DRAW_FOUR)
					normalPendingCards.add(playedCard);
			}
			else normalPendingCards.add(playedCard);		
		}
		
		p.removeCard(p.getCards().get(index));
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
	
	public void printActionStack() {
		for(PlayedCard playedCard : normalPendingCards){
			System.out.print(playedCard.getCard() + " ");
		}
		System.out.println();
	}
	
	public ArrayList<Card> getPlayableCards(PlayerInGame p, boolean asAnswer) {
		PlayedCard lastCard = null;
		
		if(discardPile.size() > 0)
			lastCard = discardPile.get(discardPile.size() - 1);
		
		return getPlayableCards(p, lastCard, asAnswer);
	}
	
	public ArrayList<Card> getPlayableCards(PlayerInGame p, PlayedCard playedCard, boolean asAnswer) {
		ArrayList<Card> playableCards = new ArrayList<Card>();
		
		for(Card card : p.getCards()) {
			if(isCardPlayable(p, card, playedCard, asAnswer)){
				playableCards.add(card);
			}
		}
		
		return playableCards;
	}

	
	public boolean isCardPlayable(PlayerInGame p, Card card, PlayedCard playedCard, boolean asAnswer) {		
		if(playedCard == null)
			return true;
		
		CardTest isNormalCard = cardToTest -> {
			return cardToTest.getType() == Card.CARD_TYPE.NORMAL;
		};
		
		CardTest isActionCard = cardToTest -> {
			return cardToTest.getType() == Card.CARD_TYPE.ACTION;
		};
		
		CardsTest sameActionCard = (cardToTest, lastPlayedCard) -> {
			assert(isActionCard.test(cardToTest));
			assert(isActionCard.test(lastPlayedCard.getCard()));
			
			return ((ActionCard)cardToTest).getActionType() == ((ActionCard)(lastPlayedCard.getCard())).getActionType();
		};
		
		CardsTest sameNumber = (cardToTest, lastPlayedCard) -> {
			assert(isNormalCard.test(cardToTest));
			assert(isNormalCard.test(lastPlayedCard.getCard()));
			
			return ((NormalCard)cardToTest).getNumber() == ((NormalCard)(lastPlayedCard.getCard())).getNumber();
		};
		
		CardsTest sameColor = (cardToTest, lastPlayedCard) -> {
			return cardToTest.getColor() == lastPlayedCard.getCard().getColor();
		};
		
		CardTest noColor = (cardToTest) -> {
			return cardToTest.getColor() == null;
		};
		
		if(asAnswer) {
			return isActionCard.test(card) && isActionCard.test(playedCard.getCard()) && sameActionCard.test(card, playedCard);
		}
		else {
			if(playedCard.getTurn() != this.getTurn() || playedCard.getPlayer() != p) {
				return (
					sameColor.test(card, playedCard) || noColor.test(card) ||
					(isActionCard.test(card) && isActionCard.test(playedCard.getCard()) && sameActionCard.test(card, playedCard)) || 
					(isNormalCard.test(card) && isNormalCard.test(playedCard.getCard()) && sameNumber.test(card, playedCard))
				);
			}
			else return (
					(isActionCard.test(card) && isActionCard.test(playedCard.getCard()) && sameActionCard.test(card, playedCard)) || 
					(isNormalCard.test(card) && isNormalCard.test(playedCard.getCard()) && sameNumber.test(card, playedCard)) 
				);
		}
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
	
	public boolean removePlayer(Player p) {
		return playersInGame.remove(p);
	}
	
	public ArrayList<PlayedCard> getNormalPendingCards() {
		return normalPendingCards;
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
	
	public int getLastSkippedPlayerIndex() {
		return lastSkippedPlayerIndex;
	}

	public void setLastSkippedPlayerIndex(int lastSkippedPlayerIndex) {
		this.lastSkippedPlayerIndex = lastSkippedPlayerIndex;
	}
	
	public boolean isStackSolved() {
		return stackSolved;
	}

	public void setStackSolved(boolean stackSolved) {
		this.stackSolved = stackSolved;
	}
	
	public PlayerInGame getLastPlayer() {
		return lastPlayer;
	}

	public void setLastPlayer(PlayerInGame lastPlayer) {
		this.lastPlayer = lastPlayer;
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
