package messages;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class ParamsECC {
	
	private Element f;
	private Element g;
	private Element h;
	private Pairing pairing;
	public Element getF() {
		return f;
	}
	public void setF(Element f) {
		this.f = f;
	}
	public Element getG() {
		return g;
	}
	public void setG(Element g) {
		this.g = g;
	}
	public Element getH() {
		return h;
	}
	public void setH(Element h) {
		this.h = h;
	}
	public Pairing getPairing() {
		return pairing;
	}
	public void setPairing(Pairing pairing) {
		this.pairing = pairing;
	}
	public ParamsECC(Element f, Element g, Element h, Pairing pairing) {
		super();
		this.f = f;
		this.g = g;
		this.h = h;
		this.pairing = pairing;
	}

}
