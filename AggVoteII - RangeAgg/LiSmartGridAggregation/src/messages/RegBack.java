package messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class RegBack {
	
	private BigInteger kesai;
	private BigInteger bigMod160;
	private BigInteger bigMod;
	private Element yita;
	private Element generator;
	private Pairing pairing;
	
	public RegBack(BigInteger kesai, BigInteger bigMod160, BigInteger bigMod, Element yita, Pairing pairing, Element generator) {
		super();
		this.kesai = kesai;
		this.bigMod160 = bigMod160;
		this.bigMod = bigMod;
		this.yita = yita;
		this.pairing = pairing;
		this.generator = generator;
	}

	
	
	public Pairing getPairing() {
		return pairing;
	}



	public void setPairing(Pairing pairing) {
		this.pairing = pairing;
	}



	public BigInteger getKesai() {
		return kesai;
	}

	public void setKesai(BigInteger kesai) {
		this.kesai = kesai;
	}

	public BigInteger getBigMod160() {
		return bigMod160;
	}

	public void setBigMod160(BigInteger bigMod160) {
		this.bigMod160 = bigMod160;
	}

	public BigInteger getBigMod() {
		return bigMod;
	}

	public void setBigMod(BigInteger bigMod) {
		this.bigMod = bigMod;
	}

	public Element getYita() {
		return yita;
	}

	public void setYita(Element yita) {
		this.yita = yita;
	}

	public Element getGenerator() {
		return generator;
	}

	public void setGenerator(Element generator) {
		this.generator = generator;
	}
		
	

}
