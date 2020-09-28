package messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class RepMessage {
	private long id;
	private Element []ci;
	private Element si;
	private long ti;
	public RepMessage(Element []ci, Element si, long ti, long id) {
		super();
		this.id = id;
		this.ci = ci;
		this.si = si;
		this.ti = ti;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Element[] getCi() {
		return (Element[]) ci;
	}
	public void setCi(Element []ci) {
		this.ci = ci;
	}
	public Element getSi() {
		return si;
	}
	public void setSi(Element si) {
		this.si = si;
	}
	public long getTi() {
		return ti;
	}
	public void setTi(long ti) {
		this.ti = ti;
	}

}
