import java.io.IOException;

import it.unisa.dia.gas.jpbc.Element;
import messages.BroadcastMessage;
import messages.ParamsECC;
import messages.RegBack;
import messages.RegMessage;
import messages.RepMessage;
import messages.RepReply;

public class myMain {

	private static Out out;

	private static DataControlUnit dcu;
	private static OperationCenter operationCenter;
	private static SmartMeter[] sm;

	public static void main(String args[]) throws IOException {

		out = new Out("Liu2019Liu2019Liu2019_8_7_4.time");

		dataAggregationPhase();

		out.close();
		Runtime.getRuntime().exec("shutdown -s");
	}

	/**
	 * simulate the multiple reporting phase a meter report multiple types of
	 * data to the server analysis.
	 * 
	 * @throws IOException
	 */
	private static void dataAggregationPhase() throws IOException {

		printAndWrite("Liu2019Liu2019Liu2019:  ");

		for (int meterNum : Params.ARRAY_OF_METERS_NUM) {
			Params.METERS_NUM = meterNum;

			operationCenter = new OperationCenter();
			ParamsECC ps = operationCenter.getParamsECC();
			dcu = new DataControlUnit(ps);
			meterIntialiaztion(ps);

			double totalTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_TIMES; j++) {
				totalTime += oneTimeMeterRegTime();
			}
			printAndWrite(" Liu2019Liu2019Liu2019 reg reg reg reg : " + meterNum);
			printAndWriteData(totalTime / Params.EXPERIMENT_TIMES);

			totalTime = 0;
			meterRegistraion();
			aggregatorRegistration();
			for (int j = 0; j < Params.EXPERIMENT_TIMES; j++) {
				totalTime += oneTimeMeterRepTime();
			}

			printAndWrite(" Liu2019Liu2019Liu2019 rep rep rep rep : " + meterNum);
			printAndWriteData(totalTime / Params.EXPERIMENT_TIMES);

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

	private static void aggregatorRegistration() throws IOException {
		RegMessage reg = dcu.genRegMesssage();
		RegBack back = operationCenter.getRegMessage(reg);
		dcu.getRegBack(back);
	}

	private static void meterIntialiaztion(ParamsECC ps) throws IOException {
		sm = new SmartMeter[Params.METERS_NUM];

		for (int i = 0; i < sm.length; i++) {
			sm[i] = new SmartMeter(ps);
			RegMessage reg = sm[i].genRegMesssage();
			dcu.getMeterRegMessage(reg);
		}
	}

	private static void meterRegistraion() throws IOException {
		for (int i = 0; i < sm.length; i++) {
			RegMessage reg = sm[i].genRegMesssage();
			RegBack back = operationCenter.getRegMessage(reg);
			sm[i].getRegBack(back);
		}

		for (int i = 0; i < sm.length; i++) {
			BroadcastMessage bd = sm[i].genBroadcastMessage();
			for (int j = 0; j < sm.length; j++) {
				if (j == i)
					continue;
				sm[j].getBroadcrastMessage(bd);
			}
		}
	}

	private static long oneTimeMeterRegTime() throws IOException {

		long sl = System.nanoTime();
		meterRegistraion();
		long el = System.nanoTime();
		clearRegInformation();
		return (el - sl);
	}

	private static void clearRegInformation() throws IOException {
		operationCenter.clear();
	}

	private static long oneTimeMeterRepTime() throws IOException {

		long sl = System.nanoTime();
		for (int i = 0; i < Params.METERS_NUM; i++) {
			RepMessage repMessage = sm[i].genRepMessage();
			RepMessage repAgg = dcu.getRepMessage(repMessage);
			operationCenter.getRepMessage(repAgg);
		}

		Element sumCA = operationCenter.getCa();
		for (int i = 0; i < Params.METERS_NUM; i++) {
			RepReply reply = sm[i].genRepReply(sumCA);
			operationCenter.getReply(reply);
		}

		long el = System.nanoTime();
		return (el - sl);
	}

}
