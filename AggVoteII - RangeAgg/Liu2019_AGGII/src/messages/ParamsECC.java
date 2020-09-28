package messages;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class ParamsECC {
	
	private Pairing pairing;
	private Element g1;
	private Element g2;
	private Element g3;
	private Element yoc;
	public ParamsECC(Pairing pairing, Element g1, Element g2, Element g3, Element yoc) {
		super();
		this.pairing = pairing;
		this.g1 = g1;
		this.g2 = g2;
		this.g3 = g3;
		this.yoc = yoc;
	}
	public Pairing getPairing() {
		return pairing;
	}
	public void setPairing(Pairing pairing) {
		this.pairing = pairing;
	}
	public Element getG1() {
		return g1;
	}
	public void setG1(Element g1) {
		this.g1 = g1;
	}
	public Element getG2() {
		return g2;
	}
	public void setG2(Element g2) {
		this.g2 = g2;
	}
	public Element getG3() {
		return g3;
	}
	public void setG3(Element g3) {
		this.g3 = g3;
	}
	public Element getYoc() {
		return yoc;
	}
	public void setYoc(Element yoc) {
		this.yoc = yoc;
	}
}
