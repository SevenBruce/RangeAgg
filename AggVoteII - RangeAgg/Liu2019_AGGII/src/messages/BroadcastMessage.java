package messages;

import it.unisa.dia.gas.jpbc.Element;

public class BroadcastMessage {
	private long id;
	private Element publicKey;
	private Element certi;
	public BroadcastMessage(long id, Element publicKey, Element certi) {
		super();
		this.id = id;
		this.publicKey = publicKey;
		this.certi = certi;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Element getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(Element publicKey) {
		this.publicKey = publicKey;
	}
	public Element getCerti() {
		return certi;
	}
	public void setCerti(Element certi) {
		this.certi = certi;
	}
}
