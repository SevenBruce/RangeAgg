package messages;

import it.unisa.dia.gas.jpbc.Element;

public class RepMessage {
	private long id;
	private Element cia;
	private Element cib;
	private Element sigma;
	private long tsi;
	
	public RepMessage() {
		super();
	}
	public RepMessage(long id, Element cia, Element cib, long tsi, Element sigma) {
		super();
		this.id = id;
		this.cia = cia;
		this.cib = cib;
		this.sigma = sigma;
		this.tsi = tsi;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Element getCia() {
		return cia;
	}
	public void setCia(Element cia) {
		this.cia = cia;
	}
	public Element getCib() {
		return cib;
	}
	public void setCib(Element cib) {
		this.cib = cib;
	}
	public Element getSigma() {
		return sigma;
	}
	public void setSigma(Element sigma) {
		this.sigma = sigma;
	}
	public long getTsi() {
		return tsi;
	}
	public void setTsi(long tsi) {
		this.tsi = tsi;
	}
}
