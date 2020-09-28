package messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class ParamsECC {
	
	private Pairing pairing;
	private Element generatorOfG1;
	private Element ycc;
	private Element[] yi;
	private BigInteger[] fi;
	public Pairing getPairing() {
		return pairing;
	}
	public void setPairing(Pairing pairing) {
		this.pairing = pairing;
	}
	public Element getGeneratorOfG1() {
		return generatorOfG1;
	}
	public void setGeneratorOfG1(Element generatorOfG1) {
		this.generatorOfG1 = generatorOfG1;
	}
	public Element getYcc() {
		return ycc;
	}
	public void setYcc(Element ycc) {
		this.ycc = ycc;
	}
	public Element[] getYi() {
		return yi;
	}
	public void setYi(Element[] yi) {
		this.yi = yi;
	}
	public BigInteger[] getFi() {
		return fi;
	}
	public void setFi(BigInteger[] fi) {
		this.fi = fi;
	}
	public ParamsECC(Pairing pairing, Element generatorOfG1, Element ycc, Element[] yi, BigInteger[] fi) {
		super();
		this.pairing = pairing;
		this.generatorOfG1 = generatorOfG1;
		this.ycc = ycc;
		this.yi = yi;
		this.fi = fi;
	}
	
}
