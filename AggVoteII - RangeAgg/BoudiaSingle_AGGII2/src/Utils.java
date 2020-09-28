import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class Utils {
	
	/**
	 * Hashing a string to an Element in the Elliptic Curve
	 *
	 * @param input
	 *            String originalString
	 * @return Element Element of G1
	 */
	public static Element hash2ElementG1(String originalString, Pairing pairing) {
		byte[] oiginalBytes = originalString.getBytes(StandardCharsets.UTF_8);
		Element result = pairing.getG1().newElementFromHash(oiginalBytes, 0, oiginalBytes.length);
		return result;
	}
	
	/**
	 * Hashing 2 a big number mod by a public parameter
	 *
	 * @param input
	 *            String id, BigInteger xi, Element ci, BigInteger di, long ti
	 * @return BigInteger
	 */
	public static BigInteger hash2Big(String orgStr,BigInteger bigMod) {
		BigInteger bi = new BigInteger(orgStr.getBytes(StandardCharsets.UTF_8));
		bi = bi.mod(bigMod);
		return bi;
	}

	/**
	 * generate a random long identity
	 *
	 * @param input
	 *            null
	 * @return long
	 */
	public static long randomlong() {
		Random rnd = new Random();
		long seed = System.nanoTime();
		rnd.setSeed(seed);
		return rnd.nextLong();
	}
	
	/**
	 * generate a random int number that is less than Params.UPBOUND_LIMIT_OF_METER_DATA
	 *
	 * @param input
	 *            null
	 * @return int
	 */
	public static long randomInt() {
		Random rnd = new Random();
		long seed = System.nanoTime();
		rnd.setSeed(seed);
		int result = rnd.nextInt(Params.UPBOUND_LIMIT_OF_METER_DATA);
		return result;
	}
	

	/**
	 * generate a random big Integer
	 *
	 * @param input
	 *            null
	 * @return long
	 */
	public static BigInteger randomBig() {
		Random rnd = new Random();
		long seed = System.nanoTime();
		rnd.setSeed(seed);
		BigInteger ranBig = new BigInteger(1024, rnd);
		return ranBig;
	}

	/**
	 * generate a random random big Integer while mod
	 *
	 * @param input
	 *            null
	 * @return long
	 */
	public static BigInteger randomBig(BigInteger mod) {
		Random rnd = new Random();
		long seed = System.nanoTime();
		rnd.setSeed(seed);
		BigInteger ranBig = new BigInteger(1024, rnd);
		ranBig = ranBig.mod(mod);
		return ranBig;
	}



}
