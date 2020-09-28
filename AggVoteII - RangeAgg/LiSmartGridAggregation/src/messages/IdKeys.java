package messages;

import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;

public class IdKeys {
	ArrayList<Long> alId;
	ArrayList<Element> alYij;
	public ArrayList<Long> getAlId() {
		return alId;
	}
	public void setAlId(ArrayList<Long> alId) {
		this.alId = alId;
	}
	public ArrayList<Element> getAlYij() {
		return alYij;
	}
	public void setAlYij(ArrayList<Element> alYij) {
		this.alYij = alYij;
	}
	public IdKeys(ArrayList<Long> alId, ArrayList<Element> alYij) {
		super();
		this.alId = alId;
		this.alYij = alYij;
	}
	
}
