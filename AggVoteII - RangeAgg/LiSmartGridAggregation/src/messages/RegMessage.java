package messages;

import it.unisa.dia.gas.jpbc.Element;

public class RegMessage {
	long id;
	long tReg;
	Element sigij;
	Element yij;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long gettReg() {
		return tReg;
	}
	public void settReg(long tReg) {
		this.tReg = tReg;
	}
	public Element getSigij() {
		return sigij;
	}
	public void setSigij(Element sigij) {
		this.sigij = sigij;
	}
	public Element getYij() {
		return yij;
	}
	public void setYij(Element yij) {
		this.yij = yij;
	}
	public RegMessage(long id, long tReg, Element sigij, Element yij) {
		super();
		this.id = id;
		this.tReg = tReg;
		this.sigij = sigij;
		this.yij = yij;
	}
}
