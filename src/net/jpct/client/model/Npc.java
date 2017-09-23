package net.jpct.client.model;

public class Npc {
	
	private final int uid;
	
	public Npc(int uid) {
		this.uid = uid;
	}

	public void handleClick() {
		System.out.println("");
	}
	
	public int getUid() {
		return uid;
	}
	
}
