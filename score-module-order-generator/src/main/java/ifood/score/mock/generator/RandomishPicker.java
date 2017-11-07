package ifood.score.mock.generator;

import java.math.BigDecimal;
import java.util.Random;

public class RandomishPicker {

	private static Double gaussianDouble(){
		Random r = new Random();
		return r.nextGaussian();
	}
	
	public static Integer _int(int lower, int upper){
		if(lower == upper){
			return upper;
		}
		double seed = gaussianDouble();
		return lower + (Math.abs((int) ((upper - lower) * seed))%(upper-lower));
	}
	
	public static BigDecimal _price(){
		double seed = gaussianDouble();
		BigDecimal price = new BigDecimal(2 + Math.abs(40 * seed));
		if(price.intValue() > 40 && price.intValue() % 4 != 0){
			price = price.divide(new BigDecimal(2));
		}
		return price.setScale(1, BigDecimal.ROUND_HALF_EVEN);
	}
	
}
