package com.example.wildqueue.models;

public class TellerWindow {
	private Teller teller;

	public TellerWindow(Teller teller) {
		this.teller = teller;
	}

	public Teller getTeller() {
		return teller;
	}

	public void setTeller(Teller teller) {
		this.teller = teller;
	}
}

