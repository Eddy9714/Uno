package com.sample;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Collections;

import com.sample.Cards.ActionCard;
import com.sample.Cards.Card;
import com.sample.Cards.NormalCard;
import com.sample.Players.Player;
import com.sample.Players.PlayerInGame;
import com.sample.Utils.PlayerCard;
import com.sample.Utils.PlayerCards;

public class Game {
	
	public static final int MAX_PLAYERS = 10;
	public static final int CARDS_TO_DEAL = 7;
	
	public static enum GAME_STATUS {INIT, READY, BEGIN, PLAY, END};
	public static enum PHASE_STATUS {DEAL_CARDS, FIRST_CARD, DRAW, ANSWER, SOLVE_EFFECTS, PLAY_CARDS, TURN_START, TURN_END};
	
	private String id;
	private GAME_STATUS status = GAME_STATUS.INIT;
	private PHASE_STATUS phaseStatus = null;
	private boolean directionLeft = true;
	
	private final ArrayList<PlayerInGame> playersInGame = new ArrayList<PlayerInGame>();
	private final ArrayList<Card> pile = new ArrayList<Card>();//mazzo
	private final ArrayList<Card> discardPile = new ArrayList<Card>();//scarti
	private final ArrayList<PlayerCard> normalPendingCards = new ArrayList<PlayerCard>(); //pila di eventi lenti che devono accadere
	private PlayerCard cardToEvaluate = null;
	
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
				Card topCard = discardPile.remove(discardPile.size() - 1);
				for(Card card : discardPile){
					pile.add(card);
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
	
	public void playCard(PlayerInGame p, Card card) {
		
		assert(isCardPlayable(card, false));
		
		p.removeCard(card);
		discardPile.add(card);
		
		if(card.getClass() == ActionCard.class) {
			ActionCard actionCard = (ActionCard)card;
			
			if(actionCard.isQuick()) {
				setCardToEvaluate(new PlayerCard(p, card)); 
			}
			else normalPendingCards.add(new PlayerCard(p, card));		
		}
		
	}
	
	public void putAndShuffle(Card card) {
		discardPile.remove(card);
		pile.add(card);
		shufflePile();
	}
	
	public void printPlayerHand(ArrayList<Card> cards) {
		for(Card card : cards){
			System.out.print(card + " ");
		}
		System.out.println();
	}
	
	public ArrayList<Card> getPlayableCards(ArrayList<Card> cards, boolean asAnswer) {
		ArrayList<Card> playableCards = new ArrayList<Card>();
		
		for(Card card : cards) {
			if(isCardPlayable(card, asAnswer)){
				playableCards.add(card);
			}
		}
		
		return playableCards;
	}
	
	private boolean isCardPlayable(Card card, boolean asAnswer) {
		
		Card lastCard = null;
		
		if(discardPile.size() > 0) {
			lastCard = discardPile.get(discardPile.size() - 1);
		}
		
		if(lastCard == null)
			return true;
		
		if(lastCard.getClass() == ActionCard.class) {
			
			boolean actionAsAnswerPlayableConditions = card.getClass() == ActionCard.class
					&& ((ActionCard)card).getActionType() == ((ActionCard)lastCard).getActionType()
					&& ((ActionCard)card).getActionType() != ActionCard.ACTION_TYPE.WILD_DRAW_FOUR;
			
			if(asAnswer) {
				return actionAsAnswerPlayableConditions;
			}
			else {
				return actionAsAnswerPlayableConditions || lastCard.getColor() == card.getColor();
			}
		}
		else {
			return lastCard.getColor() == card.getColor() || (card.getClass() == NormalCard.class && 
					((NormalCard)card).getNumber() == ((NormalCard)lastCard).getNumber());
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
	
	public ArrayList<PlayerCard> getNormalPendingCards() {
		return normalPendingCards;
	}
	
	public boolean isDirectionLeft() {
		return directionLeft;
	}

	public void setDirectionLeft(boolean directionLeft) {
		this.directionLeft = directionLeft;
	}
	
	public PlayerCard getCardToEvaluate() {
		return cardToEvaluate;
	}

	public void setCardToEvaluate(PlayerCard cardToEvaluate) {
		this.cardToEvaluate = cardToEvaluate;
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
