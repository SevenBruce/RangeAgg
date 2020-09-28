package messages;

import it.unisa.dia.gas.jpbc.Element;

public class RepReply {
	private long id;
	private Element di;
	private long tsi;
	private Element sigma;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Element getDi() {
		return di;
	}
	public void setDi(Element di) {
		this.di = di;
	}
	public long getTsi() {
		return tsi;
	}
	public void setTsi(long tsi) {
		this.tsi = tsi;
	}
	public Element getSigma() {
		return sigma;
	}
	public void setSigma(Element sigma) {
		this.sigma = sigma;
	}
	public RepReply(long id, Element di, long tsi, Element sigma) {
		super();
		this.id = id;
		this.di = di;
		this.sigma = sigma;
		this.tsi = tsi;
	}
}
