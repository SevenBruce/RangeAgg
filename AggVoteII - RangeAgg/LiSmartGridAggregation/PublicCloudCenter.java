import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Point;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import messages.BackES;
import messages.IdKeys;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage;

public class PublicCloudCenter {

	ArrayList<BigInteger> alRandNumber = new ArrayList<BigInteger>();
	Random rnd = new Random();
	BigInteger pai0;
	Iterator<BigInteger> itRandNumber;

	private long id;
	private BigInteger p;
	private BigInteger q;
	private Pairing pairing;

	private Element f;
	private Element g;
	private Element x;
	private Element h;

	private BigInteger order;

	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alYij = new ArrayList<Element>();
	
	ArrayList<Long> alIdES = new ArrayList<Long>();
	ArrayList<Element> alYiES = new ArrayList<Element>();

	public PublicCloudCenter() {

		super();
		this.id = Utils.randomlong();

		String path = "AggVote2.properties";
		this.pairing = PairingFactory.getPairing(path);
		this.f = this.pairing.getG1().newRandomElement().getImmutable();
		this.g = this.pairing.getG1().newRandomElement().getImmutable();
		this.x = this.pairing.getG1().newRandomElement().getImmutable();

		this.order = pairing.getG1().getOrder();

		In in = new In(path);
		in.readLine();
		in.readLine();
		in.readLine();

		p = new BigInteger(in.readLine().substring(3));
		q = new BigInteger(in.readLine().substring(3));
		this.h = this.x.duplicate().mul(this.q);
		
//		prepareElementsForLongKangaroo();
	}
	
	
	public void pareparePaiforMeters(int meterNumber) {
		long seed = System.nanoTime();
		rnd.setSeed(seed);
		BigInteger random;
		BigInteger sum = BigInteger.ZERO;
		
		for (int i = 0; i < meterNumber; i++) {
			random = Utils.randomBig(this.order);
//			System.out.println(random);/
			sum = sum.add(random);
			sum = sum.mod(this.order);
			alRandNumber.add(random);
		}
		pai0 = sum;
//		System.out.println(pai0);
//		System.out.println();
		itRandNumber = alRandNumber.iterator();
	}

	public BackES genIdKeysForES() {
		long id = Utils.randomlong();
		BigInteger yi = Utils.randomBig(this.order);
		Element yI = this.h.duplicate().mul(yi);
		BackES back = new BackES(id, yi, yI);
		
		alIdES.add(id);
		alYiES.add(yI);
		return back;
	}

	
	public ParamsECC getParamsECC() {
		ParamsECC params = new ParamsECC(this.f, this.g, this.h, this.pairing);
		return params;
	}

	
	public void getRepMessage(RepMessage rep) {
		
		if (null == rep) {
			return;
		}
		
		if (false == checkingIncomeMessage(rep)) {
			System.out.println("check failed pcc pcc pcc side");
		}else {

//			System.out.println();
//			int t2 = longKangaroo(g.duplicate().mul(this.p), (g.duplicate().mul(this.p)).mul(BigInteger.valueOf(3035)), this.q);
//			int t2 = longKangaroo(g.duplicate().mul(this.p), ((rep.getCij().duplicate().mul(this.p))), this.q);
//			System.out.println(" t2 : " + t2);
//			int t0 = linearSearch(g.duplicate().mul(this.p), ((rep.getCij().duplicate().mul(this.p))), this.q);
//			System.out.println(" t0 : " + t0);
		}
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

	
	private Element PrepareLeft(RepMessage rep) {
		Element result = pairing.pairing(rep.getDeltaI(), this.h);
		return result;
	}

	
	private Element PrepareRight(RepMessage rep) {

		String temStr = Long.toString(rep.getItTDij()) + rep.getCij().toString() + Long.toString(rep.getTij());
		Element deltaI = Utils.hash2ElementG1(temStr, this.pairing);

		Element yij = getPublicKeyById(rep.getItTDij());
		Element temPairing = pairing.pairing(deltaI, yij);

		return temPairing;
	}

	
	private Element getPublicKeyById(long id) {
		int index = alIdES.indexOf(id);
		return alYiES.get(index);
	}

	
	private boolean checkingIncomeMessage(RepMessage rep) {
		// TODO Auto-generated method stub
		Element left = PrepareLeft(rep);
		Element right = PrepareRight(rep);

		if (left.equals(right)) {
//			System.out.println("preparing data, please wait!!!");
			return true;
		}
		return false;
	}

	
	public BigInteger getMeterRegMessage(RegMessage reg) {

		Element left = this.pairing.pairing(reg.getSigij(), this.h);
		
		String temStr = Long.toString(reg.getId()) + Long.toString(reg.gettReg());
		Element sigij = Utils.hash2ElementG1(temStr, this.pairing);
		Element right = this.pairing.pairing(sigij, reg.getYij());

		if (left.equals(right)) {
			alId.add(reg.getId());
			alYij.add(reg.getYij());
//			 System.out.println("reg well");
		} else {
			System.out.println("reg reg reg reg failed");
			// System.out.println(reg.getBetai());
		}
		return getRequestMessage();
	}

	
	public BigInteger getRequestMessage() {
		if (itRandNumber.hasNext()) {
			return itRandNumber.next();
		}
		return BigInteger.valueOf(-1);
	}

	
	public BigInteger getPai0() {
		return pai0;
	}
	
	public IdKeys getIdKeys() {
		IdKeys idKeys = new IdKeys(this.alId, this.alYij);
		return idKeys;
	}
	
	public void clear() {
		alId.clear();
		alYij.clear();
		alIdES.clear();
		alYiES.clear();
		alRandNumber.clear();
		pai0 = BigInteger.ZERO;
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
//			table[i] = (this.g.duplicate().mul(this.p)).duplicate().pow(distance[i]);
//			// System.out.println("trap failed... : " + table[i]);
//		}
//
//		trapSetByTamedKangaroo = (this.g.duplicate().mul(this.p)).pow(LIMIT);
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
