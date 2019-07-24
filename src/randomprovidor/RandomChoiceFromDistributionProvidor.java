package randomprovidor;

import java.io.Serializable;
import java.util.Random;

public class RandomChoiceFromDistributionProvidor implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9029851005476229388L;
	Random random;
	
	public RandomChoiceFromDistributionProvidor()
	{
		random = new Random();
	}
	
	/**
	 * Chooses an element index from `probabilityRatios`. The elements of
	 * `probabilityRatios` are "un-normalized" probabilities
	 * 
	 * probability(i) = probabilityRatios[i] / sum( probabilityRatios )
	 * 
	 * @param probabilityRatios
	 * @return
	 */
	public int randomChoiceFromDist( double[] probabilityRatios )
	{
		if ( probabilityRatios.length == 0 )
		{
			throw new IllegalArgumentException( "input list cannot be empty" );
		}
		
		double sum = 0;
		for ( double ratio : probabilityRatios )
		{
			sum += ratio;
		}
		
		final double scaledRand = random.nextDouble() * sum;
		
		double threshhold = 0.;
		
		for ( int i = 0; i < probabilityRatios.length; i++ )
		{
			threshhold += probabilityRatios[i];
			
			if ( scaledRand < threshhold )
			{
				return i;
			}
		}
		
		throw new RuntimeException( "wtf" );
	}
}
