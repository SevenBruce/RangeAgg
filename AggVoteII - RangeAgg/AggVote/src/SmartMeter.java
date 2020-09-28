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
	private Element ri;
	
	private Element g;
	private Element ge;
	private Element rsi;

	public SmartMeter(ParamsECC ps) throws IOException {
		super();
		
		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();

		this.g = ps.getGeneratorOfG1();
		this.ge= ps.getGeneratorOfGT();
		
		BigInteger order = pairing.getG1().getOrder();
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
	 * A meter get the registration message from the aggregator, it has to update its key to encrypt 
	 * meter's consumption data
	 */
	public void getRegBack(Element rci) {
		this.rsi = rci.duplicate().mul(this.di);
	}
	
	
	/**
	 * A meter report multiple types of data to aggregator at a time
	 */
	public RepMessage genRepMessage(int typesOfData) throws IOException {
		long ti = System.currentTimeMillis();
		ti = ti / 1000000;
		ti = ti * 1000000;
		
		Element[] ci = new Element[typesOfData];

		Element gi = pairing.pairing(Utils.hash2ElementG1(Long.toString(ti), this.pairing), this.rsi);
		
		for (int i = 0; i < typesOfData; i++) {
			BigInteger data = BigInteger.valueOf( Utils.randomInt() );
//			BigInteger data = BigInteger.valueOf( 10 );
//			System.out.println("mi : " + data);
			ci[i] = this.ge.duplicate().mul(data).add(gi.duplicate());
//			ci[i] = this.ge.duplicate().add(gi.duplicate());
//			ci[i] = (gi.duplicate());
		}
//		System.out.println();
		
		Element si = Utils.hash2ElementG1(getCii(ci) + id + ti, this.pairing).pow(this.di);

		return new RepMessage(ci, si, ti, id);
	}
	
	private String getCii(Element[] ci){
		String cii = "";
		for (int i = 0; i < ci.length; i++) {
			cii += ci[i].toString();
		}
		return cii;
	}

}
