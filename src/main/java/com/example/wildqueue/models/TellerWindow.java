package com.example.wildqueue.models;

public class TellerWindow {
	private Teller teller;
	private int windowNumber;

	public TellerWindow(Teller teller, int windowNumber) {
		this.teller = teller;
		this.windowNumber = windowNumber;
	}

	public int getWindowNumber() { return windowNumber; }
	public void setWindowNumber(int windowNumber) { this.windowNumber = windowNumber; }

	public Teller getTeller() { return teller; }
	public void setTeller(Teller teller) {
		this.teller = teller;
	}
}

