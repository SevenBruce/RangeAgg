import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;
import messages.BackES;
import messages.IdKeys;
import messages.ParamsECC;
import messages.RegBack;
import messages.RegMessage;
import messages.RepMessage;

public class EdgeServer {

	private Pairing pairing;
	private Element f;
	private Element g;
	private Element h;

	private long idESi;
	private BigInteger yi;
	private Element yI;
	private BigInteger pai0;
	private BigInteger order;

	private ArrayList<RepMessage> alRep = new ArrayList<RepMessage>();

	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alYij = new ArrayList<Element>();

	public EdgeServer(ParamsECC params) {
		super();
		this.f = params.getF();
		this.g = params.getG();
		this.h = params.getH();
		this.pairing = params.getPairing();
		this.order = this.pairing.getG1().getOrder();
	}

	public RepMessage getRepMessage(RepMessage rep) {

		alRep.add(rep);
		if (alRep.size() < Params.METERS_NUM)
			return null;

		if (false == checkingIncomeMessage()) {
			System.out.println("check failed eserver eserver eserver eserver");
			return null;
		}

		Element ci = preparingReportingData();
		return generateReportingMessage(ci);
	}

	private RepMessage generateReportingMessage(Element ci) {
		// TODO Auto-generated method stub
		long ti = System.currentTimeMillis();

		String temStr = Long.toString(this.idESi) + ci.toString() + Long.toString(ti);
		Element deltaI = Utils.hash2ElementG1(temStr, this.pairing).duplicate().mul(yi);

		RepMessage rep = new RepMessage(this.idESi, ti, ci, deltaI);
		return rep;
	}

	private Element preparingReportingData() {
		Iterator<RepMessage> itRep = alRep.iterator();

		Element sumCij = pairing.getG1().newZeroElement();
		while (itRep.hasNext()) {
			sumCij.mul(itRep.next().getCij());
		}
		sumCij = sumCij.duplicate().add(    this.f.duplicate().mul(this.pai0.negate().add(this.order))   );
		alRep.clear();
		return sumCij;
	}

	private boolean checkingIncomeMessage() {
		Element left = PrepareLeft();
		Element right = PrepareRight();

		if (!left.equals(right)) {
//			System.out.println("left ::: " + left);
//			System.out.println("right::: " + right);
			System.out.println("right not equal to left failed!");
//			System.exit(1);
			return false;
		} else {
//			System.out.println("preparing data, please wait!!!");
			return true;
		}
	}

	private Element PrepareLeft() {
		Iterator<RepMessage> itRep = alRep.iterator();

		Element sumDelta = pairing.getG1().newZeroElement();
		while (itRep.hasNext()) {
			sumDelta.mul(itRep.next().getDeltaI());
		}
		Element result = pairing.pairing(sumDelta, this.h);
		return result;
	}

	private Element PrepareRight() {

		Iterator<RepMessage> itRep = alRep.iterator();

		Element sumPairing = pairing.getGT().newZeroElement();
		while (itRep.hasNext()) {
			RepMessage rep = itRep.next();
			String temStr = Long.toString(rep.getItTDij()) + rep.getCij().toString() + Long.toString(rep.getTij());
			Element deltaI = Utils.hash2ElementG1(temStr, this.pairing);

			Element yij = getPublicKeyById(rep.getItTDij());
			Element temPairing = pairing.pairing(deltaI, yij);

			sumPairing.mul(temPairing);
		}

		return sumPairing;
	}

	public void getIdKeyPair(BackES back) {
		this.idESi = back.getId();
		this.yI = back.getyI();
		this.yi = back.getYi();
	}

	private Element getPublicKeyById(long id) {
		int index = alId.indexOf(id);
		return alYij.get(index);
	}

	public void getIdKeys(IdKeys idKeys) {
		this.alId = idKeys.getAlId();
		this.alYij = idKeys.getAlYij();
	}

	public void setPai0(BigInteger pai0) {
		this.pai0 = pai0;
//		Element tem = this.f.duplicate().mul(this.pai0);
//		Element tem1 = this.f.duplicate().mul(this.pai0.negate().add(this.order));
//		System.out.println(this.pai0);
//		System.out.println(tem.add(tem1));
	}
	
	
	public void clear() {
		alId.clear();
		alYij.clear();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
