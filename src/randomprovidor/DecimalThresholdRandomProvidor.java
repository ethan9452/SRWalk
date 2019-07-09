package randomprovidor;

import java.util.Random;

public class DecimalThresholdRandomProvidor extends IRandomChoiceProvidor
{
	Random random;
	
	public DecimalThresholdRandomProvidor()
	{
		random = new Random();
	}

	private double rand()
	{
		return random.nextDouble();
	}

	@Override
	public int randomChoice( int numChoices )
	{
		if ( numChoices > 1 )
		{
			for ( int i = 0; i < numChoices; i++ )
			{
				final double threshold = (double ) ( i + 1) / (double) numChoices;
				if ( rand() < threshold )
				{
					return i;
				}				
			}
			
			// Should never happen.
			return numChoices - 1;
		}
		else
		{
			return 0;
		}
	}

}
