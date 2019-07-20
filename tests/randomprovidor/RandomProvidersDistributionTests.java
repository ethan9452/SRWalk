package randomprovidor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import randomprovidor.DecimalThresholdRandomProvidor;
import randomprovidor.DefaultLibraryRandomChoiceProvidor;
import randomprovidor.IRandomChoiceProvidor;
import randomprovidor.ModAlgRandomProvidor;

public class RandomProvidersDistributionTests
{
	private static int					NUM_TEST_ITERATIONS	= 10000000;

	private List<IRandomChoiceProvidor>	providors;

	public RandomProvidersDistributionTests()
	{
		providors = new ArrayList<IRandomChoiceProvidor>();

		for ( Class<? extends IRandomChoiceProvidor> shit : IRandomChoiceProvidor.IMPLEMENTED_PROVIDORS )
		{
			try
			{
				providors.add( shit.newInstance() );
			}
			catch ( InstantiationException | IllegalAccessException e )
			{
				// TODO Auto-generated catch block
				System.out.println( "oh shit!" );
				e.printStackTrace();
			}
		}

	}

	public void runit()
	{
		for ( IRandomChoiceProvidor providor : providors )
		{
			printChoiceDist( 4, providor );
		}
	}

	private void printChoiceDist( int numChoices, IRandomChoiceProvidor providor )
	{
		int[] choices = new int[numChoices];

		for ( int i = 0; i < NUM_TEST_ITERATIONS; i++ )
		{
			final int choice = providor.randomUniformChoice( numChoices );
			choices[choice]++;
		}

		System.out.println( providor.getClass().getName() );
		for ( int i = 0; i < numChoices; i++ )
		{
			String formattedNumberString = NumberFormat.getNumberInstance( Locale.US ).format( choices[i] );
			System.out.print( formattedNumberString + "\t" );
		}
		System.out.println();
	}

	public static void main( String[] argStrings )
	{
		RandomProvidersDistributionTests tests = new RandomProvidersDistributionTests();
		tests.runit();
	}

}
