package messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class RepMessage {
	private long id;
	private Element ci;
	private Element si;
	private int ti;
	public RepMessage(Element ci, Element si, int ti, long id) {
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
	public Element getCi() {
		return  ci;
	}
	public void setCi(Element ci) {
		this.ci = ci;
	}
	public Element getSi() {
		return si;
	}
	public void setSi(Element si) {
		this.si = si;
	}
	public int getTi() {
		return ti;
	}
	public void setTi(int ti) {
		this.ti = ti;
	}

}
