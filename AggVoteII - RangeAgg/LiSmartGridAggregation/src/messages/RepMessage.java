package messages;

import it.unisa.dia.gas.jpbc.Element;

public class RepMessage {

	long itTDij;
	long tij;
	Element cij;
	Element deltaI;
	public RepMessage(long itTDij, long tij, Element cij, Element deltaI) {
		super();
		this.itTDij = itTDij;
		this.tij = tij;
		this.cij = cij;
		this.deltaI = deltaI;
	}
	public long getItTDij() {
		return itTDij;
	}
	public void setItTDij(long itTDij) {
		this.itTDij = itTDij;
	}
	public long getTij() {
		return tij;
	}
	public void setTij(long tij) {
		this.tij = tij;
	}
	public Element getCij() {
		return cij;
	}
	public void setCij(Element cij) {
		this.cij = cij;
	}
	public Element getDeltaI() {
		return deltaI;
	}
	public void setDeltaI(Element deltaI) {
		this.deltaI = deltaI;
	}
}
