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
	private Element ge;
	private BigInteger dj;
	private Element rj;
	private BigInteger order;

	private Element rs;

	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alKeys = new ArrayList<Element>();

//	Element lastGroupBasedKey;

	public Agg(ParamsECC ps) throws IOException {
		super();
		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();

		this.order = pairing.getG1().getOrder();
		this.dj = Utils.randomBig(order);

		this.g = ps.getGeneratorOfG1();
		this.rj = this.g.duplicate().pow(this.dj);
		this.ge = ps.getGeneratorOfGT();
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

//	System.out.println(index + " f : " + arrayIndexPre);
//	System.out.println(index + " r : " + arrayIndexRaer);
//	System.out.println();
	
	public Element assignMeterKeys(Long id, int arraySize) {

		Element front = this.pairing.getG1().newZeroElement();
		Element rear = this.pairing.getG1().newZeroElement();
		
		Long idPre = id;
		Long idRear = id;
		
		for(int i = 0;i<Params.NUMBER_S;i++) {
			int preIndex = getPre( idPre, arraySize);
			int rearIndex = getRear( idRear, arraySize);
			front = alKeys.get(preIndex).duplicate();
			rear = alKeys.get(rearIndex).duplicate();
			
			idPre = alId.get(preIndex);
			idRear = alId.get(rearIndex);
		}
		
		front = front.duplicate().sub(rear).duplicate();
		front = front.duplicate().sub(this.rs);
		return front;
	}
	
//	
//	private int[] getPreRear(Long id, int arraySize) {
//		int index = alId.indexOf(id);
//		int arrays = alId.size() / arraySize - 1;
//		int arrayNum = (index / arraySize);
//		int arrayIndexPre = (index - 1 + arraySize) % arraySize;
//		int arrayIndexRaer = (index + 1 + arraySize) % arraySize;
//
//		if (arrayNum >= arrays) {
//			arrayNum = arrays;
//			int lastSize = alId.size() - (alId.size() / arraySize - 1) * arraySize;
//
//			arrayIndexPre = (index - arrayNum * arraySize - 1 + lastSize)% lastSize;
//			arrayIndexRaer = (index - arrayNum * arraySize + 1)% lastSize;
//		}
//		arrayIndexPre = arrayNum * arraySize + arrayIndexPre;
//		arrayIndexRaer = arrayNum * arraySize + arrayIndexRaer;
//		return new int[]{arrayIndexPre,arrayIndexRaer};
//	}
	
	private int getRear(Long id, int arraySize) {
		
		int index = alId.indexOf(id);
		int arrays = alId.size() / arraySize - 1;
		int arrayNum = (index / arraySize);
		int arrayIndexPre = (index - 1 + arraySize) % arraySize;
		int arrayIndexRaer = (index + 1 + arraySize) % arraySize;

		if (arrayNum >= arrays) {
			arrayNum = arrays;
			int lastSize = alId.size() - (alId.size() / arraySize - 1) * arraySize;

			arrayIndexPre = (index - arrayNum * arraySize - 1 + lastSize)% lastSize;
			arrayIndexRaer = (index - arrayNum * arraySize + 1)% lastSize;
		}
		arrayIndexPre = arrayNum * arraySize + arrayIndexPre;
		arrayIndexRaer = arrayNum * arraySize + arrayIndexRaer;
		return arrayIndexRaer;
	}
	
private int getPre(Long id, int arraySize) {
		
		int index = alId.indexOf(id);
		int arrays = alId.size() / arraySize - 1;
		int arrayNum = (index / arraySize);
		int arrayIndexPre = (index - 1 + arraySize) % arraySize;
		int arrayIndexRaer = (index + 1 + arraySize) % arraySize;

		if (arrayNum >= arrays) {
			arrayNum = arrays;
			int lastSize = alId.size() - (alId.size() / arraySize - 1) * arraySize;

			arrayIndexPre = (index - arrayNum * arraySize - 1 + lastSize)% lastSize;
			arrayIndexRaer = (index - arrayNum * arraySize + 1)% lastSize;
		}
		arrayIndexPre = arrayNum * arraySize + arrayIndexPre;
		arrayIndexRaer = arrayNum * arraySize + arrayIndexRaer;
		return arrayIndexRaer;
	}

	
	public Element genServerSum() {
		Iterator<Element> itKey = alKeys.iterator();
		Element result = this.pairing.getG1().newZeroElement();

		while (itKey.hasNext()) {
			result.add(itKey.next().duplicate());
		}
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

	private Element sumUpReportingData() throws IOException {

		Iterator<RepMessage> itRep = alRep.iterator();
		Element tem;

		Element ci = pairing.getGT().newOneElement();

		while (itRep.hasNext()) {
			tem = itRep.next().getCi();
			ci.add(tem);
		}

		clearReportMessage();
		return ci;
	}

	private boolean checkingIncomeMessage() throws IOException {

		Iterator<RepMessage> itRep = alRep.iterator();
		while (itRep.hasNext()) {
			RepMessage rep = itRep.next();
			
			Element left = PrepareLeft(rep);
			if (!left.equals(this.ge))
				return false;
		}
		return true;
	}
	
	private Element PrepareLeft(RepMessage rep) {
		String temStr = rep.getCi().toString() + rep.getId() + rep.getTi() ;
		BigInteger temBig = Utils.hash2Big(temStr, order);
		Element temElement = this.g.duplicate().mul(temBig);
		
		Element ri = getPublicKeyById(rep.getId());
		temElement = temElement.duplicate().add(ri);
		Element temGe = pairing.pairing(temElement, rep.getSi());
		return temGe;
	}

	private Element getPublicKeyById(long id) {
		int index = alId.indexOf(id);
		return alKeys.get(index);
	}


	private RepMessage generateReportingMessage(Element ci, int count) throws IOException {

		BigInteger temBig = Utils.hash2Big(ci.toString() + id + count, this.order);
		temBig = temBig.add(this.dj).modInverse(order);
		Element si = this.g.duplicate().mul(temBig);

		return new RepMessage(ci, si, count, this.id);
	}

	private void clearReportMessage() throws IOException {
		alRep.clear();
	}
	
	public void clear() throws IOException {
		alId.clear();
		alKeys.clear();
	}
	
	
	

}
