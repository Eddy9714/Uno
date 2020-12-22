package com.sample;

import java.util.ArrayList;
import java.util.Scanner;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.sample.Players.Player;

public class Main {
	public static void main(String[] args) {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSession kSession = kContainer.newKieSession("ksession-rules");
		
		Scanner in = new Scanner(System.in);
		int num = -1;
		
		do 
		{
			System.out.println("Inserisci il numero di giocatori:");
			num = in.nextInt();
			if(num >= 2 && num <= 10) {
				
				ArrayList<Player> players = new ArrayList<Player>(num);
				
				in.nextLine();
				
				for(int k=0;k<num;k++) {
					
					String playerName = "";
					
					do {
						System.out.println("Inserisci il nome del giocatore " + (k + 1) + ":");
						playerName = in.nextLine();
						if(playerName.length() > 0) {
							players.add(new Player(playerName));
						}
					}
					while(playerName.length() <= 0);
					
				}
				
				Game g = new Game(num, players);
				kSession.insert(g);
				kSession.fireAllRules();
			}
			else {
				System.out.println("Inserisci un numero di giocatori compreso tra 2 e 10");
			}
		}
		while(num < 2 || num > 10);
		
		in.close();
	}
}
