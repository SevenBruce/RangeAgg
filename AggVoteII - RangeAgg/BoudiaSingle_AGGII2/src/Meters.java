import java.io.IOException;
import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Point;
import messages.Cij;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage2;
import messages.Sij;

public class Meters {

	private long id;
	private Pairing pairing;

	private BigInteger aij;
	private Element pij;

	private Element aggj_uij_shardKeyWithAgg;

	private Element[] yi;
	private BigInteger[] fi;

	private Element g;
	private Element rsi;
	private BigInteger order;

	public Meters(ParamsECC ps) throws IOException {
		super();

		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();

		this.g = ps.getGeneratorOfG1();

		this.order = pairing.getG1().getOrder();
		this.aij = Utils.randomBig(order);
		this.pij = this.g.duplicate().pow(this.aij);

		this.yi = ps.getYi();
		this.fi = ps.getFi();
	}

	/**
	 * A meter sends its identity and public key to aggregator for registration
	 */
	public RegMessage genRegMesssage() {
		RegMessage reg = new RegMessage(this.id, this.pij);
		return reg;
	}

	public long getId() {
		return this.id;
	}

	/**
	 * A meter get the registration messsage from the aggregator, it has to update
	 * its key to encrypt meter's consumption data
	 */
	public void getRegBackFromAgg(Element pj) {
		this.aggj_uij_shardKeyWithAgg = pj.duplicate().pow(this.aij);
		// System.out.println(aggj_uij_shardKeyWithAgg);
	}

	/**
	 * A meter report multiple types of data to aggregator at a time
	 */
	public RepMessage2 genRepMessage() throws IOException {

		long ts = System.currentTimeMillis();
		BigInteger rij1 = Utils.randomBig(this.order);
		Element rijG = this.g.duplicate().pow(rij1);
		
		Element[] ci = new Element[Params.NUMBER_OF_REPORTING_DATA_TYPE];

		for (int i = 0; i < Params.NUMBER_OF_REPORTING_DATA_TYPE; i++) {
			
//			BigInteger temData = BigInteger.valueOf( 10 );
//			System.out.println("mi : " + data);
			BigInteger temData = BigInteger.valueOf(Utils.randomInt());
			temData = temData.subtract(this.fi[i]).mod(this.order);
//			
			Element data = this.g.duplicate().mul(temData);
			ci[i] = data.add(this.yi[i].duplicate().pow(rij1));
		}
//		System.out.println();
		
		
		Cij cij = new Cij(rijG, ci);

		// signature
		BigInteger rij2 = Utils.randomBig(this.order);
		Element rij2G = this.g.duplicate().pow(rij2);

		BigInteger rij2Reverse = rij2.modInverse(this.order);
		BigInteger D = Utils.hash2Big(cij.toString() + this.id + ts, this.order);
		BigInteger r = ((Point) rij2G).getX().toBigInteger();
		BigInteger aijr = this.aij.multiply(r);
		BigInteger zij = (aijr.add(D)).multiply(rij2Reverse);

		Sij sij = new Sij(rij2G, zij);
		return new RepMessage2(cij, this.id, ts, sij);

	}

}
