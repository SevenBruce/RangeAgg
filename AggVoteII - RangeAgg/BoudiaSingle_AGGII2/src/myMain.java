import java.io.IOException;

import it.unisa.dia.gas.jpbc.Element;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage2;

public class myMain {

	private static Out out;
	private static Agg agg;
	private static CC server;
	private static Meters[] meters;

	public static void main(String args[]) throws IOException {

		out = new Out("Boudia_AggVoteII¡ª¡ª2020-8-5-4.time");

		dataAggregation();
		printAndWrite("");
		dataAggregationMultipleTypes();

		out.close();
		Runtime.getRuntime().exec("shutdown -s");
	}

	private static void entitiesInitialization() throws IOException {
		server = new CC();
		ParamsECC ps = server.getParamsECC();
		agg = new Agg(ps);
	}

	private static void aggRegistration() throws IOException {
		RegMessage reg = agg.genRegMesssage();
		server.getAggRegMessage(reg);
	}

	private static void meterRegistration() throws IOException {
		RegMessage reg;
		Element pj;

		for (int i = 0; i < meters.length; i++) {
			reg = meters[i].genRegMesssage();
			pj = agg.getMeterRegMessage(reg);
			meters[i].getRegBackFromAgg(pj);
		}

	}

	/**
	 * simulate data aggregation meters reporting one type of data to the server
	 * analysis.
	 * 
	 * @throws IOException
	 */
	private static void dataAggregation() throws IOException {

		printAndWrite("Boudia varying meter number :  ");
		entitiesInitialization();
		aggRegistration();

		for (int meterNumber : Params.ARRAY_OF_METERS_NUM) {
			Params.METERS_NUM = meterNumber;

			printAndWrite("reg  reg  reg  varying meter number : " + Params.METERS_NUM);
			meterInitialization();
			getMeterRegTime();

			printAndWrite("rep rep rep  varying meter number : " + Params.METERS_NUM);
			meterRegistration();
			getMeterRepTime();
		}
	}
	
	
	/**
	 * simulate data aggregation meters reporting one type of data to the server
	 * analysis.
	 * 
	 * @throws IOException
	 */
	private static void dataAggregationMultipleTypes() throws IOException {

		printAndWrite("Boudia multiple types of data ");
		Params.METERS_NUM = 20;
		
		for (int type : Params.ARRAY_OF_REPORTING_DATA_TYPES) {
			Params.NUMBER_OF_REPORTING_DATA_TYPE = type;
			
			entitiesInitialization();
			aggRegistration();
			
			printAndWrite("reg  reg  reg multiple types : " + type);
			meterInitialization();
			getMeterRegTime();

			printAndWrite("rep rep rep multiple types : " + type);
			meterRegistration();
			getMeterRepTime();
		}
	}

	private static void getMeterRegTime() throws IOException {
		double regTime = 0;
		for (int j = 0; j < Params.EXPERIMENT_TIMES; j++) {
			regTime += oneTimeMeterRegTime();
		}
		regTime = regTime / 1000000;
		regTime = regTime / Params.EXPERIMENT_TIMES;
		printAndWriteData(regTime);

	}

	private static void getMeterRepTime() throws IOException {
		double repTime = 0;
		for (int j = 0; j < Params.EXPERIMENT_TIMES; j++) {
			repTime += oneTimeMeterRepTime();
		}
		
		repTime = repTime / 1000000;
		repTime = repTime / Params.EXPERIMENT_TIMES;
		printAndWriteData(repTime);
	}

	private static void meterInitialization() throws IOException {
		meters = new Meters[Params.METERS_NUM];
		ParamsECC ps = server.getParamsECC();
		for (int i = 0; i < meters.length; i++) {
			meters[i] = new Meters(ps);
		}
	}

	private static long oneTimeMeterRegTime() throws IOException {

		long sl = System.nanoTime();
		for (int i = 0; i < meters.length; i++) {
			RegMessage regUser = meters[i].genRegMesssage();
			Element pj = agg.getMeterRegMessage(regUser);
			meters[i].getRegBackFromAgg(pj);
		}
		long el = System.nanoTime();
		agg.reSetRegMessages();
		return (el - sl);
	}

	private static long oneTimeMeterRepTime() throws IOException {

		long sl = System.nanoTime();
		for (int i = 0; i < Params.METERS_NUM; i++) {
			RepMessage2 rep = meters[i].genRepMessage();
			RepMessage2 repAgg = agg.getRepMessage(rep);
			server.getRepMessage(repAgg);
		}
		long el = System.nanoTime();
		return (el - sl);
	}

	private static void printAndWriteData(double totalTime) {
		System.out.println(totalTime);
		out.println(totalTime);
		printAndWrite("");
	}

	private static void printAndWrite(String outStr) {
		System.out.println(outStr);
		out.println(outStr);
	}

}
