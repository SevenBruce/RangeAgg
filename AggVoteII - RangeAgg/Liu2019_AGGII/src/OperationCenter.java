import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Point;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import messages.ParamsECC;
import messages.RegBack;
import messages.RegMessage;
import messages.RepMessage;
import messages.RepReply;

public class OperationCenter {

	private Pairing pairing;
	private Element g1;
	private Element g2;
	private Element g3;
	private BigInteger order;

	private long id;
	private BigInteger xoc;
	private Element yoc;

	private RepMessage repStatic;
//	private Element sumDi;

	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alKeys = new ArrayList<Element>();
	ArrayList<RepReply> alReply = new ArrayList<RepReply>();

	public OperationCenter() throws IOException {
		String path = "AggVote.properties";
		this.pairing = PairingFactory.getPairing(path);

		this.id = Utils.randomlong();
		this.order = pairing.getG1().getOrder();

		this.g1 = this.pairing.getG1().newRandomElement().getImmutable();
		this.g2 = this.pairing.getG1().newRandomElement().getImmutable();
		this.g3 = this.pairing.getG1().newRandomElement().getImmutable();
		
		this.xoc = Utils.randomBig(this.order);
		this.yoc = this.g1.duplicate().pow(this.xoc);

//		prepareElementsForLongKangaroo();
	}

	public ParamsECC getParamsECC() {
		ParamsECC ps = new ParamsECC(this.pairing, this.g1, this.g2, this.g3,this.yoc);
		return ps;
	}
	
	public void clear() {
		alId.clear();
		alKeys.clear();
	}

	public RegBack getRegMessage(RegMessage reg) {
		alId.add(reg.getId());
		alKeys.add(reg.getKey());
		
		String originalString = reg.getId() + reg.getKey().toString();
		Element cert = Utils.hash2ElementG1(originalString, pairing).duplicate().mul(this.xoc);
		RegBack back = new RegBack(cert);
		return back;
	}

	public void getRepMessage(RepMessage rep) throws IOException {

		if (null == rep) {
			return;
		}

		if (false == checkTheSignatureOfIncomingMessage(rep)) {
			System.out.println("server check failed");
		} else {
//			System.out.println("server checkeeeedddd");
			repStatic = rep;
		}
	}
	
	public Element getCa() {
		return repStatic.getCia();
	}

	public void getReply(RepReply reply) throws IOException {

		alReply.add(reply);
		if (alReply.size() < Params.METERS_NUM)
			return;

		if (false == checkingIncomeMessage()) {
			System.out.println("check failed at the agg side");
			return;
		}
		
		Element sumCia = getSumCia();
		getConsumptionData(sumCia);
		alReply.clear();
	}
	
	private Element getSumCia() {
		Iterator<RepReply> itReply = alReply.iterator();
		Element sumCia = pairing.getG1().newZeroElement();

		while (itReply.hasNext()) {
			RepReply rep = itReply.next();
			sumCia.add(rep.getDi());
		}
		return sumCia;
	}
	
	
	private boolean checkingIncomeMessage() throws IOException {
		Element left = PrepareLeft();
		Element right = PrepareRight();

		if (!left.equals(right)) {
			System.out.println("check failed at reply reply reply");
			return false;
		}
			
		return true;
	}

	private Element PrepareLeft() {
		Iterator<RepReply> itReply = alReply.iterator();
		Element sum = pairing.getG1().newZeroElement();

		while (itReply.hasNext()) {
			sum.add(itReply.next().getSigma());
		}
		Element temGe = pairing.pairing(sum, this.g1);
		return temGe;
	}

	private Element PrepareRight() {

		Iterator<RepReply> itReply = alReply.iterator();
		Element sum1 = pairing.getG1().newZeroElement();
		Element sum2 = pairing.getG1().newZeroElement();

		while (itReply.hasNext()) {
			RepReply rep = itReply.next();

			Element yi = getPublicKeyById(rep.getId());
			sum1.add(yi);

			String temStr = rep.getId() + rep.getDi().toString() + rep.getTsi();
			BigInteger tem = Utils.hash2Big(temStr, order);
			Element temEle = yi.duplicate().mul(tem);
			sum2.add(temEle);
		}
		Element temGe1 = pairing.pairing(this.g2, sum1);
		Element temGe2 = pairing.pairing(this.g3, sum2);
		return temGe1.mul(temGe2);
	}

	public double getConsumptionData(Element sumD) {
		Element temData = repStatic.getCib().duplicate().sub(sumD);
		int t0 = 0;
//		int t0 = linearSearch(g1.duplicate(), temData.duplicate(), order);
//		t0 = linearSearch(g1.duplicate(), temData.duplicate(), order);
//		System.out.println("t0 £º " + t0);
		repStatic = new RepMessage();
		return t0;
	}

	private int linearSearch(Element generator, Element num, BigInteger order) {
		Element g = generator.duplicate();

		for (int i = 1; i < 100000; i++) {
			if (g.equals(num)) {
				return i;
			}
			g.mul(generator.duplicate());
		}
		return -1;
	}

	private boolean checkTheSignatureOfIncomingMessage(RepMessage rep) throws IOException {

		Element left = PrepareLeft(rep);
		Element right = PrepareRight(rep);

		if (!left.equals(right))
			return false;
		return true;
	}

	private Element PrepareLeft(RepMessage rep) {
		Element temGe = pairing.pairing(rep.getSigma(), this.g1);
		return temGe;
	}

	private Element PrepareRight(RepMessage rep) {
		Element yi = getPublicKeyById(rep.getId());

		String temStr = rep.getId() + rep.getCia().toString() + rep.getCib() + rep.getTsi();
		BigInteger hdcu = Utils.hash2Big(temStr, order);
		Element temEle = yi.duplicate().mul(hdcu);

		Element temGe1 = pairing.pairing(this.g2, yi);
		Element temGe2 = pairing.pairing(this.g3, temEle);
		return temGe1.mul(temGe2);
	}

	private Element getPublicKeyById(long id) {
		int index = alId.indexOf(id);
		return alKeys.get(index);
	}

	
	
//	private BigInteger LIMIT = BigInteger.valueOf(Params.REPORT_UPBOUND_LIMIT);
//	private BigInteger aLEAP = BigInteger.valueOf(Params.LEAPES).divide(BigInteger.valueOf(4));
//
//	Element trapSetByTamedKangaroo;
//	Element[] table = new Element[32];
//	int mForKangaroo;
//	BigInteger dnForKangaroo;
//	BigInteger[] distance = new BigInteger[32];
//
//	private void prepareElementsForLongKangaroo() {
//		/*
//		 * Pollard's lambda algorithm for finding discrete logs * which are known to be
//		 * less than a certain limit LIMIT ref:
//		 * https://github.com/miracl/MIRACL/blob/master/source/kangaroo.cpp
//		 */
//
//		mForKangaroo = 1;
//		for (BigInteger s = BigInteger.ONE;; mForKangaroo++) {
//			distance[mForKangaroo - 1] = s;
//			s = s.add(s);
//			if ((s.add(s)).divide(BigInteger.valueOf(mForKangaroo)).compareTo(aLEAP) > 0)
//				break;
//		}
//
//		for (int i = 0; i < mForKangaroo; i++) {
//			/* create table */
//			table[i] = this.g1.duplicate().pow(distance[i]);
//			// System.out.println("trap failed... : " + table[i]);
//		}
//
//		trapSetByTamedKangaroo = this.g1.duplicate().pow(LIMIT);
//
//		dnForKangaroo = BigInteger.ZERO;
//		// System.out.println("setting trap..." + m);
//		for (int j = 0; j < Params.LEAPES; j++) {
//			/* set traps beyond LIMIT using tame kangaroo */
////			int i = Math.abs(trapSetByTamedKangaroo.toBigInteger().intValue()) % mForKangaroo;
//			int i = Math.abs(((Point) trapSetByTamedKangaroo).getX().toBigInteger().intValue()) % mForKangaroo;
//			trapSetByTamedKangaroo.mul(table[i]);
//			dnForKangaroo = (dnForKangaroo.add(distance[i])).mod(order);
//		}
//	}
//
//	private int longKangaroo(Element generator, Element num, BigInteger order) {
//		BigInteger dm;
//		Element Num = num;
//		int i = 0;
//		for (dm = BigInteger.ZERO;;) {
////			i = Math.abs(Num.toBigInteger().intValue()) % mForKangaroo;
//			i = Math.abs(((Point)Num).getX().toBigInteger().intValue()) % mForKangaroo;
//			Num.mul(table[i]);
//			dm = (dm.add(distance[i])).mod(order);
//
//			if (Num.equals(trapSetByTamedKangaroo))
//				break;
//			if (dm.compareTo(LIMIT.add(dnForKangaroo)) > 0) {
//				System.out.println("trap failed... : " + dm);
//				return -1;
//			}
//		}
//
//		return (LIMIT.add(dnForKangaroo).subtract(dm)).intValue();
//	}

}
