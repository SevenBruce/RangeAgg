import java.io.IOException;
import java.math.BigInteger;

import messages.BackES;
import messages.IdKeys;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage;

public class myMain {

//	static int count = 10;
	private static long sl;
	private static long el;
	private static Out out;

	private static PublicCloudCenter pcc;
	private static EdgeServer eserver;
	private static SmartMeter[] meter;

	public static void main(String args[]) throws IOException {

		out = new Out("Li2019Li2019Li2020_8_8_4.time");
		dataAggregationPhase();
		out.close();
		Runtime.getRuntime().exec("shutdown -s");
	}

	/**
	 * simulate the multiple reporting phase a meter report multiple types of data
	 * to the server analysis.
	 * 
	 * @throws IOException
	 */
	private static void dataAggregationPhase() throws IOException {

		printAndWrite("Li2019 Li2019 Li2019 Li2019 Li2019 : ");

		for (int num : Params.ARRAY_OF_METERS_NUM) {
			Params.METERS_NUM = num;

			pcc = new PublicCloudCenter();

			double totalTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				entitiesIntialiaztion();
				totalTime += oneTimeMeterRegTime();
				clear();
			}

			printAndWrite("reg reg reg time with meter number : " + num);
			printAndWriteData(totalTime / Params.EXPERIMENT_REPEART_TIMES);

			entitiesIntialiaztion();
			oneTimeMeterRegTime();

			totalTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				totalTime += oneTimeMeterRepTime();
			}

			printAndWrite("rep rep rep time with meter number : " + num);
			printAndWriteData(totalTime / Params.EXPERIMENT_REPEART_TIMES);
		}
	}

	private static void printAndWriteData(double totalTime) {
		System.out.println(totalTime / 1000000);
		out.println(totalTime / 1000000);
		printAndWrite("");
	}

	private static void printAndWrite(String outStr) {
		System.out.println(outStr);
		out.println(outStr);
	}

	private static void entitiesIntialiaztion() throws IOException {
		ParamsECC params = pcc.getParamsECC();
		pcc.pareparePaiforMeters(Params.METERS_NUM);

		eserver = new EdgeServer(params);
		aggregatorRegistration();

		meterIntialiaztion();
	}

	private static void aggregatorRegistration() throws IOException {
		BackES back = pcc.genIdKeysForES();
		eserver.getIdKeyPair(back);
		BigInteger pai0 = pcc.getPai0();
		eserver.setPai0(pai0);
	}

	private static void meterIntialiaztion() throws IOException {
		meter = new SmartMeter[Params.METERS_NUM];
		ParamsECC ps = pcc.getParamsECC();

		for (int i = 0; i < meter.length; i++) {
			meter[i] = new SmartMeter(ps);
		}
	}

	private static long oneTimeMeterRegTime() throws IOException {

		long sl = System.nanoTime();
		for (int i = 0; i < meter.length; i++) {
			RegMessage reg = meter[i].genRegMesssage();
			BigInteger paii = pcc.getMeterRegMessage(reg);
			meter[i].setPai(paii);
		}
		long el = System.nanoTime();
		IdKeys idKeys = pcc.getIdKeys();
//		System.out.println("idKeys : " + idKeys.getAlId().size());
		eserver.getIdKeys(idKeys);
		return (el - sl);
	}

	private static void clear() {
		pcc.clear();
		eserver.clear();
	}

	private static long oneTimeMeterRepTime() throws IOException {

		long sl = System.nanoTime();
		for (int i = 0; i < Params.METERS_NUM; i++) {
			RepMessage repMessage = meter[i].genRepMessage();
			RepMessage repAgg = eserver.getRepMessage(repMessage);
			pcc.getRepMessage(repAgg);
		}
		long el = System.nanoTime();
		return (el - sl);
	}

}
