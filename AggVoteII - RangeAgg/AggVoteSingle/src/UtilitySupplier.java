import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage;

public class UtilitySupplier {

	private long id;
	private Pairing pairing;
	private Element g;
	private Element ge;
	private Element gg[];
	private BigInteger order;

	private BigInteger ds;
	private Element rs;
	private Element rsx;

	private BigInteger[] keys;
	private BigInteger[] deKeys;
	private int dataTypes;
	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alKeys = new ArrayList<Element>();

//	private BigInteger LIMIT = BigInteger.valueOf(Params.REPORT_UPBOUND_LIMIT);
//	private BigInteger aLEAP = BigInteger.valueOf(Params.LEAPES).divide(BigInteger.valueOf(4));
//
//	Element trapSetByTamedKangaroo[];
//	Element[][] table;
//
//	int[] mForKangaroo;
//	BigInteger[] dnForKangaroo;
//	BigInteger[][] distance;

	public UtilitySupplier(int dataTypes) throws IOException {
		this.dataTypes = dataTypes;
		String path = "AggVote" + this.dataTypes + ".properties";
		this.pairing = PairingFactory.getPairing(path);
//		PairingFactory.getPairing("aggVote.properties");
		
		this.id = Utils.randomlong();
		this.order = pairing.getG1().getOrder();
		this.ds = Utils.randomBig(this.order);

		this.g = this.pairing.getG1().newRandomElement().getImmutable();
		this.ge = this.pairing.pairing(this.g, this.g);
		this.rs = this.g.duplicate().pow(this.ds);

		In in = new In(path);
		in.readLine();
		in.readLine();
		in.readLine();

		keys = new BigInteger[this.dataTypes];
		gg = new Element[this.dataTypes];
		deKeys = new BigInteger[this.dataTypes];

		if (this.dataTypes == 1) {
				keys[0] = BigInteger.ONE;
				deKeys[0] = BigInteger.ONE;
				gg[0] = ge;
		}else {
			for (int i = 0; i < this.dataTypes; i++) {
				keys[i] = new BigInteger(in.readLine().substring(3));
//				System.out.println("keys[" + i + "] : " + keys[i]);
				deKeys[i] = this.order.divide(keys[i]);
				gg[i] = ge.duplicate().mul(deKeys[i]);
			}
		}

//		prepareElementsForLongKangaroo();
	}

	public ParamsECC getParamsECC() {
		ParamsECC ps = new ParamsECC(this.pairing, this.g, this.ge, this.gg);
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

		if (false == checkTheSignatureOfIncomingMessage(rep)) {
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
		double[] con = new double[this.dataTypes];

		Element gx = this.pairing.pairing(Utils.hash2ElementG1(Long.toString(rep.getTi()), pairing), this.rsx);
		Element cu = rep.getCi().duplicate().add(gx.duplicate());

		for (int i = 0; i < con.length; i++) {

			Element generator = gg[i].duplicate().mul(deKeys[i]);
//			int t1 = linearSearch(generator, cu.duplicate().mul(deKeys[i]), order.divide(keys[i]));
//			System.out.println(" linearSearch " + t1);
//			con[i] = t1;

//			int t0 = longKangaroo(generator, cu.duplicate().mul(deKeys[i]), order.divide(deKeys[i]), i);
//			con[i] = t0;
//			System.out.println(" longKangaroo " +t0);
//			System.out.println();
		}
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

		String temStr = rep.getCi().toString() + rep.getId() + rep.getTi();
		BigInteger temBig = Utils.hash2Big(temStr, order);
		Element temElement = this.g.duplicate().mul(temBig);

		Element ri = getPublicKeyById(rep.getId());
		temElement = temElement.duplicate().add(ri);
		Element temGe = pairing.pairing(temElement, rep.getSi());

		if (!temGe.equals(this.ge))
			return false;

		return true;
	}

	private Element getPublicKeyById(long id) {
		int index = alId.indexOf(id);
		return alKeys.get(index);
	}

//	private void prepareElementsForLongKangaroo() {
//		
//		trapSetByTamedKangaroo = new Element[dataTypes];
//		mForKangaroo = new int[dataTypes];
//		dnForKangaroo = new BigInteger[dataTypes];
//		
//		table = new Element[dataTypes][32];
//		distance = new BigInteger[dataTypes][32];
//		/*
//		 * Pollard's lambda algorithm for finding discrete logs * which are known to be
//		 * less than a certain limit LIMIT ref:
//		 * https://github.com/miracl/MIRACL/blob/master/source/kangaroo.cpp
//		 */
//		for (int type = 0; type < this.dataTypes; type++) {
//			mForKangaroo[type] = 1;
//			
//			for (BigInteger s = BigInteger.ONE;; mForKangaroo[type]++) {
//				distance[type][mForKangaroo[type] - 1] = s;
//				s = s.add(s);
//				if ((s.add(s)).divide(BigInteger.valueOf(mForKangaroo[type])).compareTo(aLEAP) > 0)
//					break;
//			}
//			
//			
////			for (int m = 0; m < this.distance.length; m++) {
////				for (int n = 0; n < this.distance[m].length; n++) {
////					System.out.print( distance[m][n] + "  ");
////				}
////				System.out.println( );
////				System.out.println( );
////				System.out.println( );
////			}
//
//			/* create table */
//			Element temGenerator = gg[type].duplicate().pow(deKeys[type]);
//			for (int j = 0; j < mForKangaroo[type]; j++) {
//				/* create table */
//				temGenerator.duplicate().pow(distance[type][j]);
//				table[type][j] = temGenerator.duplicate().pow(distance[type][j]);
//				// System.out.println("trap failed... : " + table[i]);
//			}
//			trapSetByTamedKangaroo[type] = temGenerator.duplicate().pow(LIMIT);
//
////			for (int m = 0; m < this.table.length; m++) {
////				for (int n = 0; n < this.table[m].length; n++) {
////					System.out.print( table[m][n] + "  ");
////				}
////				System.out.println( );
////			}
////			System.out.println( );
//
//			dnForKangaroo[type] = BigInteger.ZERO;
//			// System.out.println("setting trap..." + m);
//
//			for (int j = 0; j < Params.LEAPES; j++) {
//				/* set traps beyond LIMIT using tame kangaroo */
//				int k = Math.abs(trapSetByTamedKangaroo[type].toBigInteger().intValue()) % mForKangaroo[type];
//				trapSetByTamedKangaroo[type].mul(table[type][k]);
//				dnForKangaroo[type] = (dnForKangaroo[type].add(distance[type][k])).mod(order.divide(deKeys[type]));
//			}
//
//		}
//	}
//
//	private int longKangaroo(Element generator, Element num, BigInteger order, int type) {
//		BigInteger dm;
//		Element Num = num;
//		int tem = 0;
//		for (dm = BigInteger.ZERO;;) {
//			tem = Math.abs(Num.toBigInteger().intValue()) % mForKangaroo[type];
//			Num.mul(table[type][tem]);
//			dm = (dm.add(distance[type][tem])).mod(order);
//
//			if (Num.equals(trapSetByTamedKangaroo[type]))
//				break;
//			if (dm.compareTo(LIMIT.add(dnForKangaroo[type])) > 0) {
//				System.out.println("trap failed... : " + dm);
//				return -1;
//			}
//		}
//
//		return (LIMIT.add(dnForKangaroo[type]).subtract(dm)).intValue();
//	}

}
