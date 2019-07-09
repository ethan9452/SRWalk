package randomprovidor;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import randomprovidor.DecimalThresholdRandomProvidor;
import randomprovidor.DefaultLibraryRandomChoiceProvidor;
import randomprovidor.IRandomChoiceProvidor;
import randomprovidor.ModAlgRandomProvidor;

class RandomProvidersTests
{

	@Test
	void testDecRandomProvidor()
	{
		DecimalThresholdRandomProvidor randomProvider = new DecimalThresholdRandomProvidor();
		randomChoiceTester( randomProvider, 100, 100 );
	}

	@Test
	void testDefaultRandomProvidor()
	{
		DefaultLibraryRandomChoiceProvidor randomProvider = new DefaultLibraryRandomChoiceProvidor();
		randomChoiceTester( randomProvider, 100, 100 );
	}

	@Test
	void testModRandomProvidor()
	{
		ModAlgRandomProvidor randomProvider = new ModAlgRandomProvidor();
		randomChoiceTester( randomProvider, 100, 100 );
	}

	void randomChoiceTester( IRandomChoiceProvidor provider, int iterations, int rangeToTest )
	{
		for ( int i = 0; i < iterations; i++ )
		{
			for ( int range = 0; range < rangeToTest; range++ )
			{
				final int choice = provider.randomChoice( range );
				assertTrue( choice >= 0 );

				if ( range > 0 )
				{
					assertTrue( choice < range );
				}
			}
		}
	}
}
