package randomprovidor;

import java.util.Random;

/**
 * This class tries to implement a randomChoice that's better than `DecimalThresholdRandomProvidor`s
 * 
 * The one in `DecimalThresholdRandomProvidor` seems not to be uniform
 * 
 * @author ethanlo1
 *
 */
public class ModAlgRandomProvidor extends IRandomChoiceProvidor
{
	Random random;
	
	public ModAlgRandomProvidor()
	{
		random = new Random();	
	}
	
	@Override
	public int randomChoice( int numChoices )
	{
		if ( numChoices == 0 )
		{
			return 0;
		}
		
		final int randBase = Math.abs( random.nextInt() );
	
		return randBase % numChoices;
	}
	
	
}
