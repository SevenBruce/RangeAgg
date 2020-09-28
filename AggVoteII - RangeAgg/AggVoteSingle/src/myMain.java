import java.io.IOException;

import it.unisa.dia.gas.jpbc.Element;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage;

public class myMain {

	private static Out out;

	private static Agg agg;
	private static UtilitySupplier supplier;
	private static SmartMeter[] sm;

	public static void main(String args[]) throws IOException {

		out = new Out("AggVoteII_8_10_2.time");

		multipleDataReportingPhaseSSSS();
		multipleDataReportingPhaseKKKK();
		aggPhaseVaryingMeterNumber();
		aggPhaseVaryingType();

		out.close();
		Runtime.getRuntime().exec("shutdown -s");
	}

	/**
	 * simulate the multiple reporting phase a meter report multiple types of data
	 * to the server analysis.
	 * 
	 * @throws IOException
	 */
	private static void multipleDataReportingPhaseKKKK() throws IOException {

		printAndWrite("groupSize groupSize groupSize ");

		Params.DEFAULT_DATA_TYPE = 1;
		int type = Params.DEFAULT_DATA_TYPE;
		Params.METERS_NUM = 20;
		Params.NUMBER_S = 1;

		for (int groupSize : Params.ARRAY_OF_GROUP_SIZE) {
			Params.DEFAULT_GROUP_SIZE = groupSize;

			supplier = new UtilitySupplier(type);
			ParamsECC ps = supplier.getParamsECC();
			agg = new Agg(ps);
			aggregatorRegistration();
			meterIntialiaztion(ps);

			double repTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				repTime += oneTimeMeterRegTime();
				clear();
			}
			printAndWrite("reg reg reg with groupSizer : " + groupSize);
			printAndWriteData(repTime / Params.EXPERIMENT_REPEART_TIMES);

			oneTimeMeterRegTime();
			repTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				repTime += oneTimeMeterRepTime(type);
			}

			printAndWrite("rep rep rep with groupSize : " + groupSize);
			printAndWriteData(repTime / Params.EXPERIMENT_REPEART_TIMES);
		}

	}

	/**
	 * simulate the multiple reporting phase a meter report multiple types of data
	 * to the server analysis.
	 * 
	 * @throws IOException
	 */
	private static void aggPhaseVaryingType() throws IOException {

		printAndWrite("meter number meter number meter number");

		Params.METERS_NUM = 20;
		Params.DEFAULT_GROUP_SIZE = 5;
		Params.NUMBER_S = 1;

		for (int type : Params.ARRAY_OF_REPORTING_DATA_TYPES) {
			Params.DEFAULT_DATA_TYPE = type;

			supplier = new UtilitySupplier(type);
			ParamsECC ps = supplier.getParamsECC();
			agg = new Agg(ps);
			aggregatorRegistration();

			meterIntialiaztion(ps);

			double totalTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				totalTime += oneTimeMeterRegTime();
				clear();
			}
			printAndWrite("reg reg reg with type type : " + type);
			printAndWriteData(totalTime / Params.EXPERIMENT_REPEART_TIMES);

			oneTimeMeterRegTime();
			totalTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				totalTime += oneTimeMeterRepTime(type);
			}

			printAndWrite("rep rep rep with type type : " + type);
			printAndWriteData(totalTime / Params.EXPERIMENT_REPEART_TIMES);
		}
	}

	/**
	 * simulate the multiple reporting phase a meter report multiple types of data
	 * to the server analysis.
	 * 
	 * @throws IOException
	 */
	private static void aggPhaseVaryingMeterNumber() throws IOException {

		printAndWrite("meter number meter number meter number");

		int type = 1;
		Params.DEFAULT_DATA_TYPE = 1;
		Params.DEFAULT_GROUP_SIZE = 5;
		Params.NUMBER_S = 1;

		for (int num : Params.ARRAY_OF_METERS_NUM) {
			Params.METERS_NUM = num;

			supplier = new UtilitySupplier(type);
			ParamsECC ps = supplier.getParamsECC();
			agg = new Agg(ps);
			aggregatorRegistration();

			meterIntialiaztion(ps);

			double totalTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				totalTime += oneTimeMeterRegTime();
				clear();
			}
			printAndWrite("reg reg reg with meter number : " + num);
			printAndWriteData(totalTime / Params.EXPERIMENT_REPEART_TIMES);

			oneTimeMeterRegTime();
			totalTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				totalTime += oneTimeMeterRepTime(type);
			}

			printAndWrite("rep rep rep with meter number : " + num);
			printAndWriteData(totalTime / Params.EXPERIMENT_REPEART_TIMES);
		}
	}

	/**
	 * simulate the multiple reporting phase a meter report multiple types of data
	 * to the server analysis.
	 * 
	 * @throws IOException
	 */
	private static void multipleDataReportingPhaseSSSS() throws IOException {

		printAndWrite("Varying SSSSSSSSSSSSSSSSSSSSSSSSSS");

		Params.DEFAULT_DATA_TYPE = 1;
		int type = Params.DEFAULT_DATA_TYPE;
		Params.DEFAULT_GROUP_SIZE = 15;
		Params.METERS_NUM = 30;

		for (int sss : Params.ARRAY_S) {
			Params.NUMBER_S = sss;

			supplier = new UtilitySupplier(type);
			ParamsECC ps = supplier.getParamsECC();
			agg = new Agg(ps);
			aggregatorRegistration();

			meterIntialiaztion(ps);

			double repTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				repTime += oneTimeMeterRegTime();
				clear();
			}
			printAndWrite("reg reg reg with SSSSSSSSSSSSSSSSSSSSSSSSSS : " + sss);
			printAndWriteData(repTime / Params.EXPERIMENT_REPEART_TIMES);

			oneTimeMeterRegTime();

			repTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				repTime += oneTimeMeterRepTime(type);
			}

			printAndWrite("reporting time when SSSSSSSSSSSSSSSSSSSSSSSSSS : " + sss);
			printAndWriteData(repTime / Params.EXPERIMENT_REPEART_TIMES);
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
		agg.setRs(supplier.getRs());

		RegMessage reg = agg.genRegMesssage();
		supplier.getAggRegMessage(reg);
	}

	private static void meterIntialiaztion(ParamsECC ps) throws IOException {
		sm = new SmartMeter[Params.METERS_NUM];

		for (int i = 0; i < sm.length; i++) {
			sm[i] = new SmartMeter(ps);
		}
	}

	private static void clear() throws IOException {
		agg.clear();
	}

	private static long oneTimeMeterRegTime() throws IOException {
		long sl = System.nanoTime();
		for (int i = 0; i < sm.length; i++) {
			RegMessage reg = sm[i].genRegMesssage();
			agg.getMeterRegMessage(reg);
		}

		for (int i = 0; i < sm.length; i++) {
			Element key = agg.assignMeterKeys(sm[i].getId(), Params.DEFAULT_GROUP_SIZE);
			sm[i].getRegBack(key);
		}
		long el = System.nanoTime();
		supplier.getAggRegBack(agg.genServerSum());
		return (el - sl);
	}

	private static long oneTimeMeterRepTime(int count) throws IOException {
		long sl = System.nanoTime();
		for (int i = 0; i < Params.METERS_NUM; i++) {
			RepMessage repMessage = sm[i].genRepMessage(count);
			RepMessage repAgg = agg.getRepMessage(repMessage);
			supplier.getRepMessage(repAgg);
		}
		long el = System.nanoTime();
		return (el - sl);
	}

}
