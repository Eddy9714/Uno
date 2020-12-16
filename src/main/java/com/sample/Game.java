package com.sample;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Collections;
import java.util.Random;

import com.sample.Cards.ActionCard;
import com.sample.Cards.Card;
import com.sample.Cards.NormalCard;
import com.sample.Players.Player;
import com.sample.Players.PlayerInGame;
import com.sample.Utils.PlayerCards;

public class Game {
	
	public static final int MAX_PLAYERS = 10;
	public static final int CARDS_TO_DEAL = 7;
	
	public static enum GAME_STATUS {INIT, READY, BEGIN, PLAY, END};
	public static enum PHASE_STATUS {DEAL_CARDS, FIRST_CARD, EVAL_FIRST_CARD, DRAW, RESPOND, SOLVE_EFFECTS, PLAY_CARDS, PASS_TURN};
	
	public static enum GAME_DIRECTION {LEFT, RIGHT}
	
	private String id;
	private GAME_STATUS status = GAME_STATUS.INIT;
	private PHASE_STATUS phaseStatus = null;
	private GAME_DIRECTION direction = GAME_DIRECTION.LEFT;
	
	private ArrayList<PlayerInGame> playersInGame = new ArrayList<PlayerInGame>();
	private ArrayList<Card> pile = new ArrayList<Card>();
	private ArrayList<Card> discardPile = new ArrayList<Card>();
	private PlayerCards playingCards;
	private Card playingCard;
	
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
	
	public void setPlayerRoles() {
		Random rand = new Random();
		int randomPlayerIndex = rand.nextInt(playersInGame.size());
		for(int k=0;k<playersInGame.size(); k++) {
			if(randomPlayerIndex == k) {
				PlayerInGame dealer = playersInGame.get(k);
				dealer.setRole(PlayerInGame.ROLE.DEALER);
				dealer.setPlayerTurn(true);
			}
			else 
				playersInGame.get(k).setRole(PlayerInGame.ROLE.NORMAL);
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
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.SKIP));
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.REVERSE));
				pile.add(new ActionCard(ActionCard.ACTION_TYPE.DRAW_TWO));
			}
		}
		
		shufflePile();
	}
	
	public void shufflePile() {
		Collections.shuffle(pile);
	}
	
	public void exchangePiles() {
		ArrayList<Card> tmp = pile;
		pile = discardPile;
		discardPile = tmp;
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
				exchangePiles();
				shufflePile();
				k--;
			}
		}
		
		p.addCards(cards);
		
		return cards;
	}
	
	public void playCards(PlayerInGame p, ArrayList<Card> cards) {
		
		for(Card card : cards) {
			p.getCards().remove(card);
			discardPile.add(card);
		}
		setPlayingCards(new PlayerCards(p, cards));
	}
	
	public void putAndShuffle(Card card) {
		discardPile.remove(card);
		pile.add(card);
		shufflePile();
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
	
	public GAME_DIRECTION getDirection() {
		return direction;
	}

	public void setDirection(GAME_DIRECTION direction) {
		this.direction = direction;
	}
	
	public PlayerCards getPlayingCards() {
		return playingCards;
	}

	public void setPlayingCards(PlayerCards playingCards) {
		this.playingCards = playingCards;
	}
	
	public Card getPlayingCard() {
		return playingCard;
	}

	public void setPlayingCard(Card playingCard) {
		this.playingCard = playingCard;
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
