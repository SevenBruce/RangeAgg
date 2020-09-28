import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import messages.BroadcastMessage;
import messages.ParamsECC;
import messages.RegBack;
import messages.RegMessage;
import messages.RepMessage;
import messages.RepReply;

public class SmartMeter {

	private long id;
	private BigInteger xi;
	private Element yi;

	private Pairing pairing;
	private BigInteger order;
	private Element g1;
	private Element g2;
	private Element g3;
	private Element yoc;
	private Element certi;

	private Element gk;
	ArrayList<BroadcastMessage> alBdc = new ArrayList<BroadcastMessage>();

	public SmartMeter(ParamsECC ps) throws IOException {
		super();

		this.id = Utils.randomlong();

		this.pairing = ps.getPairing();
		this.g1 = ps.getG1();
		this.g2 = ps.getG2();
		this.g3 = ps.getG3();
		this.yoc = ps.getYoc();

		this.order = pairing.getG1().getOrder();
		this.xi = Utils.randomBig(order);
		this.yi = this.g1.duplicate().pow(this.xi);
	}

	/**
	 * A meter sends its identity and public key to aggregator for registration
	 */
	public RegMessage genRegMesssage() {
		RegMessage reg = new RegMessage(this.id, this.yi);
		return reg;
	}

	/**
	 * A meter get the registration message from the aggregator, it has to update
	 * its key to encrypt meter's consumption data
	 */
	public void getRegBack(RegBack reg) {
		this.certi = reg.getCert();
	}

	public BroadcastMessage genBroadcastMessage() {
		BroadcastMessage bdc = new BroadcastMessage(this.id, this.yi, this.certi);
		return bdc;
	}

	public void getBroadcrastMessage(BroadcastMessage bdc) {

		alBdc.add(bdc);
		if (alBdc.size() < Params.METERS_NUM - 1)
			return;

		if (checkingBroadcastMessage()) {
			this.gk = getGK();
//		System.out.println("this.gk : " + this.gk);
		}
	}

	private Element getGK() {
		Iterator<BroadcastMessage> itBdc = alBdc.iterator();
		Element gk = pairing.getG1().newZeroElement();

		while (itBdc.hasNext()) {
			BroadcastMessage bdc = itBdc.next();
			gk.add(bdc.getPublicKey());
		}
		gk.add(this.yi);
		this.alBdc.clear();
		return gk;
	}

	public void clear() {
		alBdc.clear();
	}

	private boolean checkingBroadcastMessage() {
		Element left = PrepareLeft();
		Element right = PrepareRight();

		if (!left.equals(right)) {
			System.out.println(" checkingBroadcastMessage failed failed failed" );
			return false;
		}
		return true;
	}

	private Element PrepareLeft() {
		Iterator<BroadcastMessage> itBdc = alBdc.iterator();
		Element sum = pairing.getG1().newZeroElement();

		while (itBdc.hasNext()) {
			BroadcastMessage bdc = itBdc.next();

			String temStr = bdc.getId() + bdc.getPublicKey().toString();
			Element tem = Utils.hash2ElementG1(temStr, pairing);
			sum.add(tem);
		}
		Element temGe = pairing.pairing(sum, this.yoc);
		return temGe;
	}

	private Element PrepareRight() {
		Iterator<BroadcastMessage> itRep = alBdc.iterator();
		Element sumCert = pairing.getG1().newZeroElement();

		while (itRep.hasNext()) {
			sumCert.add(itRep.next().getCerti());
		}
		return pairing.pairing(sumCert, this.g1);
	}

	/**
	 * A meter report multiple types of data to aggregator at a time
	 */
	public RepMessage genRepMessage() throws IOException {

		BigInteger ri = Utils.randomBig(this.order);
		Element cia = this.g1.duplicate().mul(ri);

		BigInteger mi = BigInteger.valueOf(Utils.randomInt());
//		System.out.println(" mi : " +  mi );
		Element tem = this.g1.duplicate().mul(mi);
		Element cib = this.gk.duplicate().mul(ri);
		cib.add(tem);

		Long tsi = System.currentTimeMillis();
		String temStr = this.id + cia.toString() + cib.toString() + tsi;
		BigInteger hi = Utils.hash2Big(temStr, this.order);
		BigInteger xihi = hi.multiply(this.xi);
		Element delta = this.g3.duplicate().mul(xihi);
		delta.add(this.g2.duplicate().mul(this.xi));

//		int t0 = Utils.linearSearch(this.gk, this.gk.duplicate().mul(ri),this.order);
//		System.out.println("t0 :  " + t0);

//		{
//			Element gEle = this.pairing.pairing(g1, g1);
//			Element cibEle = this.pairing.pairing(cib, this.g1);
//			Element cibKey = this.pairing.pairing(cia, this.gk);
//			int t0 = linearSearch(gEle, cibEle.sub(cibKey),this.order);
//			System.out.println("t0 :  " + t0);
//		}

		// RepMessage(long id, Element cia, Element cib, Element sigma, int tsi)
		return new RepMessage(this.id, cia, cib, tsi, delta);
	}
	
//	private int linearSearch(Element generator, Element num, BigInteger order) {
//		Element g = generator.duplicate();
//
//		for (int i = 1; i < 100000; i++) {
//			if (g.equals(num)) {
//				return i;
//			}
//			g.mul(generator.duplicate());
//		}
//		return -1;
//	}

	/**
	 * A meter report multiple types of data to aggregator at a time
	 */
	public RepReply genRepReply(Element sumCa) throws IOException {

		Element di = sumCa.duplicate().mul(xi);

		Long tsi = System.currentTimeMillis();
		String temStr = this.id + di.toString() + tsi;
		BigInteger temBig = Utils.hash2Big(temStr, this.order);
		temBig = temBig.multiply(this.xi);

		Element delta = this.g3.duplicate().mul(temBig);
		delta.add(this.g2.duplicate().mul(this.xi));

		// RepReply(long id, Element di, Element sigma, long tsi)
		return new RepReply(this.id, di, tsi, delta);
	}

}
