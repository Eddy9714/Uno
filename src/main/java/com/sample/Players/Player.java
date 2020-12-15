package com.sample.Players;
import java.util.UUID;

public class Player {
	
	private String id = "";
	private String name = "";
	
	public Player(String name) {
		UUID uuid = UUID.randomUUID();
		id = uuid.toString();
		this.setName(name);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
