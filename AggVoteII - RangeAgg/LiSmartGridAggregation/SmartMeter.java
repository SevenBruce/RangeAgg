import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import messages.*;

public class SmartMeter {

	private long idTDij;
	private BigInteger yij;
	private Element yIJ;

	private Element f;
	private Element g;
	private Element h;

	private BigInteger order;
	private Pairing pairing;
	private BigInteger pai;

	public SmartMeter(ParamsECC params) {
		super();
		this.pairing = params.getPairing();
		this.f = params.getF();
		this.g = params.getG();
		this.h = params.getH();
		this.order = pairing.getG1().getOrder();

		this.idTDij = Utils.randomlong();
		this.yij = Utils.randomBig(this.order);
		this.yIJ = this.h.duplicate().mul(this.yij);
	}

	public void setPai(BigInteger pai) {
		this.pai = pai;
	}

	public RegMessage genRegMesssage() {

		long tReg = System.currentTimeMillis();
		String temStr = Long.toString(this.idTDij) + Long.toString(tReg);
		Element sigij = Utils.hash2ElementG1(temStr, this.pairing).duplicate().mul(this.yij);

		// RegMessage(long id, long tReg, Element sigij, Element yij)
		return new RegMessage(idTDij, tReg, sigij, yIJ);
	}

	public RepMessage genRepMessage() {
		
		long tij = System.currentTimeMillis();

		Element temF = this.f.duplicate().pow(this.pai);

		BigInteger mi = BigInteger.valueOf(Utils.randomInt());
//		System.out.println(mi);
		Element temG = this.g.duplicate().mul(mi);

		BigInteger rij = Utils.randomBig(this.order);
		Element temH = this.h.duplicate().mul(rij);
		Element cij = temF.duplicate().add(temG).duplicate().add(temH);
		
		String temStr = Long.toString(this.idTDij) + cij.toString() + Long.toString(tij);
		Element deltaIj = Utils.hash2ElementG1(temStr, this.pairing).duplicate().mul(this.yij);


		return new RepMessage(this.idTDij, tij, cij, deltaIj);
	}

	
	
	
	
	
	
	
	
	
	
}
