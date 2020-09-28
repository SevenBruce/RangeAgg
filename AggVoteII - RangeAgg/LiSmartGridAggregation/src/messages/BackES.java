package messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class BackES {
	
	long id ;
	BigInteger yi;
	Element yI;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public BigInteger getYi() {
		return yi;
	}
	public void setYi(BigInteger yi) {
		this.yi = yi;
	}
	public Element getyI() {
		return yI;
	}
	public void setyI(Element yI) {
		this.yI = yI;
	}
	public BackES(long id, BigInteger yi, Element yI2) {
		super();
		this.id = id;
		this.yi = yi;
		yI = yI2;
	}

}
