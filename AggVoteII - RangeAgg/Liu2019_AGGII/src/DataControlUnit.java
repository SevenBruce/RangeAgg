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

public class DataControlUnit {

	private ArrayList<RepMessage> alRep = new ArrayList<RepMessage>();

	private long id;
	private Pairing pairing;
	private Element g1;
	private Element g2;
	private Element g3;
	private BigInteger xdcu;
	private Element ydcu;
	private BigInteger order;

	private Element rs;
	private Element certi;

	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alKeys = new ArrayList<Element>();

	public DataControlUnit(ParamsECC ps) throws IOException {
		super();
		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();
		this.order = pairing.getG1().getOrder();

		this.g1 = ps.getG1();
		this.g2 = ps.getG2();
		this.g3 = ps.getG3();

		this.xdcu = Utils.randomBig(order);
		this.ydcu = this.g1.duplicate().pow(this.xdcu);
	}

	public void setRs(Element rs) {
		this.rs = rs;
	}

	public void getMeterRegMessage(RegMessage reg) {
		alId.add(reg.getId());
		alKeys.add(reg.getKey());
	}

	public RegMessage genRegMesssage() {
		RegMessage reg = new RegMessage(this.id, this.ydcu);
		return reg;
	}

	/**
	 * A meter get the registration message from the aggregator, it has to update
	 * its key to encrypt meter's consumption data
	 */
	public void getRegBack(RegBack reg) {
		this.certi = reg.getCert();
	}

	public RepMessage getRepMessage(RepMessage rep) throws IOException {

		alRep.add(rep);
		if (alRep.size() < Params.METERS_NUM)
			return null;

		if (false == checkingIncomeMessage()) {
			System.out.println("check failed at the agg side");
			return null;
		}

		return generateReportingMessage();
	}

	private boolean checkingIncomeMessage() throws IOException {
		Element left = PrepareLeft();
		Element right = PrepareRight();

		if (!left.equals(right))
			return false;
		return true;
	}

	private Element PrepareLeft() {
		Iterator<RepMessage> itRep = alRep.iterator();
		Element sum = pairing.getG1().newZeroElement();

		while (itRep.hasNext()) {
			sum.add(itRep.next().getSigma());
		}
		Element temGe = pairing.pairing(sum, this.g1);
		return temGe;
	}

	private Element PrepareRight() {

		Iterator<RepMessage> itRep = alRep.iterator();
		Element sum1 = pairing.getG1().newZeroElement();
		Element sum2 = pairing.getG1().newZeroElement();

		while (itRep.hasNext()) {
			RepMessage rep = itRep.next();

			Element yi = getPublicKeyById(rep.getId());
			sum1.add(yi);

			String temStr = rep.getId() + rep.getCia().toString() + rep.getCib().toString() + rep.getTsi();
			BigInteger hi = Utils.hash2Big(temStr, order);
			Element temEle = yi.duplicate().mul(hi);
			sum2.add(temEle);
		}
		Element temGe1 = pairing.pairing(this.g2, sum1);
		Element temGe2 = pairing.pairing(this.g3, sum2);
		return temGe1.mul(temGe2);
	}

	private Element getPublicKeyById(long id) {
		int index = alId.indexOf(id);
		return alKeys.get(index);
	}
//
//	private Element getPublicKeyByIndex(int index) {
//		return alKeys.get(index);
//	}

	private RepMessage generateReportingMessage() throws IOException {

		Iterator<RepMessage> itRep = alRep.iterator();
		Element sumCia = pairing.getG1().newZeroElement();
		Element sumCib = pairing.getG1().newZeroElement();

		while (itRep.hasNext()) {
			RepMessage rep = itRep.next();
			sumCia.add(rep.getCia());
			sumCib.add(rep.getCib());
		}
		clearReportMessage();

		Long tsi = System.currentTimeMillis();
		String temStr = this.id + sumCia.toString() + sumCib.toString() + tsi;
		BigInteger hdcu = Utils.hash2Big(temStr, this.order);
		BigInteger xdcuHdcu = xdcu.multiply(hdcu);
		Element sigma = this.g3.duplicate().mul(xdcuHdcu);
		sigma.add(this.g2.duplicate().mul(this.xdcu));

		return new RepMessage(this.id, sumCia, sumCib, tsi, sigma);
	}

	private void clearReportMessage() throws IOException {
		alRep.clear();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
