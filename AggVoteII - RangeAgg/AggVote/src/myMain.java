import java.io.IOException;

import messages.ParamsECC;
import messages.RegMessage;
import messages.RepMessage;

public class myMain {

	private static Out out;

	private static Agg agg;
	private static Server server;
	private static SmartMeter[] sm;

	public static void main(String args[]) throws IOException {

		out = new Out("AggVoteMutipleTypes_8_6-5.time");

		multipleDataReportingPhase();
		multipleMeterReportingPhase();
		out.close();
		 Runtime.getRuntime().exec("shutdown -s");
	}

	/**
	 * simulate the multiple reporting phase 
	 *  a meter report multiple types of data to the server analysis. 
	 * 
	 * @throws IOException
	 */
	private static void multipleDataReportingPhase() throws IOException {

		printAndWrite("AggVote for multiple types of data");
		
		server = new Server();
		agg = new Agg(server.getParamsECC());
		aggregatorRegistration();
		
		meterIntialiaztion();
		
		
		for (int type : Params.ARRAY_OF_REPORTING_DATA_TYPES) {
			Params.NUMBER_OF_REPORTING_DATA_TYPE = type;

			double regTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				regTime += oneTimeMeterRegTime();
				agg.clearReg();
			}
			printAndWrite("regTime time when data tyeps : " + type);
			printAndWriteData(regTime / Params.EXPERIMENT_REPEART_TIMES);
			
			oneTimeMeterRegTime();
			double repTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				repTime += oneTimeMeterRepTime(type);
			}

			printAndWrite("reporting time when data tyeps : " + type);
			printAndWriteData(repTime / Params.EXPERIMENT_REPEART_TIMES);
		}
	}
	
	/**
	 * simulate the multiple reporting phase 
	 *  a meter report multiple types of data to the server analysis. 
	 * 
	 * @throws IOException
	 */
	private static void multipleMeterReportingPhase() throws IOException {

		printAndWrite("AggVote for varying meters ");
		
		Params.NUMBER_OF_REPORTING_DATA_TYPE=1;
		server = new Server();
		
		for (int num : Params.ARRAY_OF_METERS_NUM) {
			Params.METERS_NUM = num;
			
			agg = new Agg(server.getParamsECC());
			aggregatorRegistration();
			meterIntialiaztion();
			
			
			double regTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				regTime += oneTimeMeterRegTime();
				agg.clearReg();
			}
			printAndWrite("regTime time meters : " + num);
			printAndWriteData(regTime / Params.EXPERIMENT_REPEART_TIMES);
			
			oneTimeMeterRegTime();
			
			double repTime = 0;
			for (int j = 0; j < Params.EXPERIMENT_REPEART_TIMES; j++) {
				repTime += oneTimeMeterRepTime(1);
			}

			printAndWrite("reporting time with meters : " + num);
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
		agg.setRs(server.getRs());
		
		RegMessage reg = agg.genRegMesssage();
		server.getAggRegMessage(reg);
		
	}
	
	private static void meterIntialiaztion() throws IOException {
		sm = new SmartMeter[Params.METERS_NUM];
		ParamsECC ps = server.getParamsECC();

		for (int i = 0; i < sm.length; i++) {
			sm[i] = new SmartMeter(ps);
		}
	}
	
	
	private static long oneTimeMeterRegTime() throws IOException {
		long sl = System.nanoTime();
		for (int i = 0; i < sm.length; i++) {
			RegMessage reg = sm[i].genRegMesssage();
			agg.getMeterRegMessage(reg);
		}

		for (int i = 0; i < sm.length; i++) {
			sm[i].getRegBack(agg.generateReplayForMeter(sm[i].getId()));
		}
		long el = System.nanoTime();
		server.getAggRegBack(agg.genServerRegBack());
		return (el - sl);
	}

	private static long oneTimeMeterRepTime(int count) throws IOException {

		long sl = System.nanoTime();
		for (int i = 0; i < Params.METERS_NUM; i++) {
			RepMessage repMessage = sm[i].genRepMessage(count);
			RepMessage repAgg = agg.getRepMessage(repMessage);
			server.getRepMessage(repAgg);
		}
		long el = System.nanoTime();
		return (el - sl);
	}
	
}
