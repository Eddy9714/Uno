package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.sample.Players.Player;

public class Main {
	public static void main(String[] args) {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSession kSession = kContainer.newKieSession("ksession-rules");
		
		Player p1 = new Player("Eddy9714");
		Player p2 = new Player("Burzy");
		
		Game g = new Game(p1, p2);
		kSession.insert(g);
		
		g.setStatus(Game.GAME_STATUS.READY);
		System.out.println(g.getStatus());
		int fired = kSession.fireAllRules();
		System.out.println(g.getPlayersInGame().get(0).getCards().size());
	}
}
