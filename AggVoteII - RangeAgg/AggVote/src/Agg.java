import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage;

public class Agg {

	private ArrayList<RepMessage> alRep = new ArrayList<RepMessage>();

	private long id;
	private Pairing pairing;
	private Element g;
	private BigInteger dj;
	private Element rj;

	private Element rs;

	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alKeys = new ArrayList<Element>();

	Element lastGroupBasedKey;

	public Agg(ParamsECC ps) throws IOException {
		super();
		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();

		BigInteger order = pairing.getG1().getOrder();
		this.dj = Utils.randomBig(order);

		this.g = ps.getGeneratorOfG1();
		this.rj = this.g.duplicate().pow(this.dj);
	}

	public void setRs(Element rs) {
		this.rs = rs;
	}

	public void getMeterRegMessage(RegMessage reg) {
		alId.add(reg.getId());
		alKeys.add(reg.getKey());
	}

	public RegMessage genRegMesssage() {
		RegMessage reg = new RegMessage(this.id, this.rj);
		return reg;
	}

	public Element generateReplayForMeter(long id) {
		int index = alId.indexOf(id);
		// System.out.println(" index : " + index);
		if (index == 0) {
			lastGroupBasedKey = initializeRci();
			return lastGroupBasedKey;
		} else {
			lastGroupBasedKey.add(getPublicKeyByIndex(index - 1));
			lastGroupBasedKey.add(getPublicKeyByIndex(index));
			return lastGroupBasedKey;
		}
	}

	public Element initializeRci() {
		Iterator<Element> itKey = alKeys.iterator();
		itKey.next();
		Element result = this.pairing.getG1().newZeroElement();

		while (itKey.hasNext()) {
			result.sub(itKey.next());
		}
		result.sub(this.rs.duplicate());
		return result;
	}

	public Element genServerRegBack() {
		lastGroupBasedKey.add(this.rs.duplicate());
		Element result = lastGroupBasedKey.add(getPublicKeyByIndex(alKeys.size() - 1));
		return result;
	}

	public RepMessage getRepMessage(RepMessage rep) throws IOException {

		alRep.add(rep);
		if (alRep.size() < Params.METERS_NUM)
			return null;

		if (false == checkingIncomeMessage()) {
			System.out.println("check failed at the agg side");
			return null;
		}
		
		return generateReportingMessage(sumUpReportingData(), rep.getTi());
	}

	private Element[] sumUpReportingData() throws IOException {

		Iterator<RepMessage> itRep = alRep.iterator();
		Element[] tem;

		Element[] ci = new Element[Params.NUMBER_OF_REPORTING_DATA_TYPE];
		for (int i = 0; i < ci.length; i++) {
			ci[i] = this.pairing.getGT().newZeroElement();
		}

		while (itRep.hasNext()) {
			tem = itRep.next().getCi();
			for (int i = 0; i < ci.length; i++) {
				ci[i].add(tem[i]);
			}
		}

		clearReportMessage();
		return ci;
	}

	private boolean checkingIncomeMessage() throws IOException {

		ArrayList<BigInteger> alFai = prepareFai();

		Element left = PrepareLeft(alFai);
		Element right = PrepareRight(alFai);

		if (!left.equals(right)) {
			System.out.println("left ::: " + left);
			System.out.println("right::: " + right);
			System.out.println("Agg Agg Agg Agg Agg right not equal to left failed!");
//			System.exit(1);
		} else {
			// System.out.println("preparing data, please wait!!!");
		}
		return true;
	}

	private ArrayList<BigInteger> prepareFai() {
		ArrayList<BigInteger> alFai = new ArrayList<BigInteger>();
		for (int i = 0; i < Params.METERS_NUM; i++) {
//			alFai.add(BigInteger.valueOf(Utils.randomInt(100)));
			alFai.add(Utils.randomFai());
		}
		return alFai;
	}

	private Element PrepareLeft(ArrayList<BigInteger> alFai) {

		Iterator<RepMessage> itRep = alRep.iterator();
		Iterator<BigInteger> itFai = alFai.iterator();

		if (!itRep.hasNext()) {
			return null;
		}

		Element temResult = pairing.getG1().newZeroElement();
		while (itRep.hasNext()) {
			temResult.add(itRep.next().getSi().duplicate().pow(itFai.next()));
		}
		
		Element result = pairing.pairing(temResult, this.g);
		return result;
	}

	private Element PrepareRight(ArrayList<BigInteger> alFai) {

		Iterator<RepMessage> itRep = alRep.iterator();
		Iterator<BigInteger> itFai = alFai.iterator();

		if (!itRep.hasNext()) {
			return null;
		}

		Element result = pairing.getGT().newOneElement();

		Element temHash;
		Element temRi;
		Element temPairing;

		while (itFai.hasNext()) {

			RepMessage rep = itRep.next();
			temHash = Utils.hash2ElementG1(getCii(rep.getCi()) + rep.getId() + rep.getTi(), this.pairing);
			
			temRi = getPublicKeyById(rep.getId());
			temPairing = pairing.pairing(temHash, temRi.duplicate().pow(itFai.next()));
			result.mul(temPairing);
		}
		return result;
	}

	private Element getPublicKeyById(long id) {
		int index = alId.indexOf(id);
		return alKeys.get(index);
	}

	private Element getPublicKeyByIndex(int index) {
		return alKeys.get(index);
	}

	private RepMessage generateReportingMessage(Element[] ci, long ti) throws IOException {

		String cii = getCii(ci);
		Element si = Utils.hash2ElementG1(cii + id + ti, this.pairing).pow(this.dj);

		return new RepMessage(ci, si, ti, this.id);
	}

	private String getCii(Element[] ci) {
		String cii = "";
		for (int i = 0; i < ci.length; i++) {
			cii += ci[i].toString();
		}
		return cii;
	}

	
	private void clearReportMessage() throws IOException {
		alRep.clear();
	}
	
	public void clearReg() throws IOException {
		alId.clear();
		alKeys.clear();
	}

}
