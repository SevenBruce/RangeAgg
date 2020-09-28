package messages;

import it.unisa.dia.gas.jpbc.Element;

public class RegBack {
	
	private Element cert;
	public RegBack(Element cert) {
		super();
		this.cert = cert;
	}
	public Element getCert() {
		return cert;
	}
	public void setCert(Element cert) {
		this.cert = cert;
	}

}
