import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage;

public class Server {

	private long id;
	private Pairing pairing;
	private Element g;
	private Element ge;
	private BigInteger order;

	private BigInteger ds;
	private Element rs;
	private Element rsx;

	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alKeys = new ArrayList<Element>();

	public Server() throws IOException {
		this.pairing = PairingFactory.getPairing("AggVote.properties");

		this.id = Utils.randomlong();
		this.order = pairing.getG1().getOrder();
		this.ds = Utils.randomBig(this.order);

		this.g = this.pairing.getG1().newRandomElement().getImmutable();
		this.ge = this.pairing.pairing(this.g, this.g);
		this.rs = this.g.duplicate().pow(this.ds);
		
//		prepareElementsForLongKangaroo();
	}

	public ParamsECC getParamsECC() {
		ParamsECC ps = new ParamsECC(this.pairing, this.g, this.ge);
		return ps;
	}

	public Element getRs() {
		return this.rs.duplicate();
	}

	public void getAggRegMessage(RegMessage reg) {
		alId.add(reg.getId());
		alKeys.add(reg.getKey());
	}

	public void getAggRegBack(Element ra) {
		this.rsx = ra.duplicate().mul(this.ds);
	}

	public void getRepMessage(RepMessage rep) throws IOException {
		
		if (null == rep) {
			return;
		}
		
		if ( false==checkTheSignatureOfIncomingMessage(rep) ) {
			System.out.println("server check failed");
		} else {
//			System.out.println("server checkeeeedddd");
		}
		
		double[] con = getConsumptionData(rep);
//		for (int i = 0; i < con.length; i++) {
//			System.out.println(" sum of  " + (i + 1) + " types of data " + con[i]);
//		}
//		System.out.println();
	}

	public double[] getConsumptionData(RepMessage rep) {
		double[] con = new double[rep.getCi().length];
		
		Element gx = this.pairing.pairing(Utils.hash2ElementG1(Long.toString(rep.getTi()), pairing) , this.rsx);
//		
//		for (int i = 0; i < con.length; i++) {
////			con[i] = longKangaroo(this.ge, rep.getCi()[i].duplicate().add(gx.duplicate()),this.order);
//			con[i] = linearSearch(this.ge, rep.getCi()[i].duplicate().add(gx.duplicate()),this.order);
//		}
		return con;
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

		Element left = this.pairing.pairing(rep.getSi(), this.g.duplicate());
		
		Element temHash = Utils.hash2ElementG1(getCii(rep.getCi()), pairing);
		Element right = this.pairing.pairing(temHash, getPublicKeyById(rep.getId()));

		if (!left.equals(right)) {
			System.out.println("left ::: " + left);
			System.out.println("right::: " + right);
			System.out.println("right not equal to left failed server server!");
//			System.exit(1);
		} else {
//			System.out.println("preparing data, please wait!!!");
		}
		return true;
	}
	
	private String getCii(Element[] ci){
		String cii = "";
		for (int i = 0; i < ci.length; i++) {
			cii += ci[i].toString();
		}
		return cii;
	}
	
	private Element getPublicKeyById(long id) {
		int index = alId.indexOf(id);
		return alKeys.get(index);
	}

	
//	private BigInteger LIMIT = BigInteger.valueOf(PublicParams.REPORT_UPBOUND_LIMIT);
//	private BigInteger aLEAP = BigInteger.valueOf(PublicParams.LEAPES).divide(BigInteger.valueOf(4));
//	
//	Element trapSetByTamedKangaroo;
//	Element[] table = new Element[32];
//	int mForKangaroo;
//	BigInteger dnForKangaroo;
//	BigInteger[] distance = new BigInteger[32];
//	
//	private void prepareElementsForLongKangaroo(){
//		/*
//		 * Pollard's lambda algorithm for finding discrete logs * which are
//		 * known to be less than a certain limit LIMIT ref:
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
//			table[i] = this.ge.duplicate().pow(distance[i]);
//			// System.out.println("trap failed... : " + table[i]);
//		}
//
//		trapSetByTamedKangaroo = this.ge.duplicate().pow(LIMIT);
//		
//		dnForKangaroo = BigInteger.ZERO;
//		// System.out.println("setting trap..." + m);
//		for (int j = 0; j < PublicParams.LEAPES; j++) {
//			/* set traps beyond LIMIT using tame kangaroo */
//			int i = Math.abs(trapSetByTamedKangaroo.toBigInteger().intValue()) % mForKangaroo;
//			trapSetByTamedKangaroo.mul(table[i]);
//			dnForKangaroo = (dnForKangaroo.add(distance[i])).mod(order);
//		}
//	}
//	
//	private int longKangaroo(Element generator, Element num, BigInteger order){
//		BigInteger dm;
//		Element Num = num;
//		int i = 0;
//		for (dm = BigInteger.ZERO;;) {
//			i = Math.abs(Num.toBigInteger().intValue()) % mForKangaroo;
////			i = Math.abs(((Point)Num).getX().toBigInteger().intValue()) % mForKangaroo;
//			Num.mul(table[i]);
//			dm = (dm.add(distance[i])).mod(order);
//
//			if (Num.equals(trapSetByTamedKangaroo))
//				break;
//			if (dm.compareTo(LIMIT.add(dnForKangaroo)) > 0){
//				System.out.println("trap failed... : " + dm);
//				return -1;
//			}
//		}
//
//		return (LIMIT.add(dnForKangaroo).subtract(dm)).intValue();
//	}

	

}
