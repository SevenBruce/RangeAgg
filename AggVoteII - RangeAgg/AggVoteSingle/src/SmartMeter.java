import java.io.IOException;
import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage;

public class SmartMeter {

	private long id;
	private Pairing pairing;

	private BigInteger di;
	private BigInteger order;
	private Element ri;

	private Element g;
	private Element ge;
	private Element[] gg;
	private Element rsi;
	private int count;

	public SmartMeter(ParamsECC ps) throws IOException {
		super();

		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();

		this.g = ps.getGeneratorOfG1();
		this.ge = ps.getGeneratorOfGT();
		this.gg = ps.getGg();

		this.order = pairing.getG1().getOrder();
		this.di = Utils.randomBig(order);
		this.ri = this.g.duplicate().pow(this.di);
	}

	/**
	 * A meter sends its identity and public key to aggregator for registration
	 */
	public RegMessage genRegMesssage() {
		RegMessage reg = new RegMessage(this.id, this.ri);
		return reg;
	}

	public long getId() {
		return this.id;
	}

	/**
	 * A meter get the registration message from the aggregator, it has to update
	 * its key to encrypt meter's consumption data
	 */
	public void getRegBack(Element rci) {
		this.rsi = rci.duplicate().mul(this.di);
//		this.rsi = rci.duplicate();
	}

	/**
	 * A meter report multiple types of data to aggregator at a time
	 */
	public RepMessage genRepMessage(int typesOfData) throws IOException {
		if (count++ > 2401)
			count = 1;

		Element ci = this.pairing.getGT().newZeroElement();
		Element gi = pairing.pairing(Utils.hash2ElementG1(Integer.toString(count), this.pairing), this.rsi);

		for (int i = 0; i < typesOfData; i++) {
			BigInteger data = BigInteger.valueOf(Utils.randomInt(Params.UPBOUND_LIMIT_OF_METER_DATA));
//			BigInteger data = BigInteger.valueOf( Utils.randomInt(10) );
//			System.out.print(data  + "  ");
			Element tem = this.gg[i].duplicate().mul(data);
			ci = ci.add(tem);
		}
//		System.out.println(count);
		ci = ci.add(gi.duplicate());
//		System.out.println();

		BigInteger temBig = Utils.hash2Big(ci.toString() + id + count, this.order);
		temBig = temBig.add(this.di).modInverse(order);
		Element si = this.g.duplicate().mul(temBig);

		return new RepMessage(ci, si, count, id);
	}

}
