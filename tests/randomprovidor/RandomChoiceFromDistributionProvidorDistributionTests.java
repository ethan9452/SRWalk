package randomprovidor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RandomChoiceFromDistributionProvidorDistributionTests
{
	private static int						NUM_TEST_ITERATIONS	= 10000000;

	private static List<double[]>			distsToTest			= new ArrayList<double[]>()
																{
																	{
																		add( new double[] { 1.0 } );
																		add( new double[] { 1.0, 1.0 } );
																		add( new double[] { 1.0, 1.0, 1.0 } );
																		add( new double[] { 1.0, 1.0, 1.0, 1.0 } );
																		add( new double[] { 1.0, 2.0 } );
																		add( new double[] { 1.0, 2.0, 3.0 } );
																		add( new double[] { 1.0, 100.0 } );
																	}
																};

	private static RandomChoiceFromDistributionProvidor	providor = new RandomChoiceFromDistributionProvidor();

	private static String getArrayString( double[] probRatio)
	{
		String d = "";
		for ( int i = 0; i < probRatio.length; i++ )
		{
			String formattedNumberString = NumberFormat.getNumberInstance( Locale.US ).format( probRatio[i] );
			d += formattedNumberString + "\t";
		}
		return d;
	}
	
	private static String getArrayString( int[] probRatio)
	{
		String d = "";
		for ( int i = 0; i < probRatio.length; i++ )
		{
			String formattedNumberString = NumberFormat.getNumberInstance( Locale.US ).format( probRatio[i] );
			d += formattedNumberString + "\t";
		}
		return d;
	}

	private static void printChoiceDist( double[] probRatio )
	{
		System.out.println( "ratio:\t" + getArrayString( probRatio ) );

		int[] choice = new int[probRatio.length];
		for ( int i = 0; i < NUM_TEST_ITERATIONS; i++ )
		{
			choice[providor.randomChoiceFromDist( probRatio )]++;
		}

		System.out.println( "\t" + getArrayString( choice ) );
		System.out.println( "=================================================================" );

	}

	public static void main( String[] arg )
	{
		for ( double[] dist : distsToTest )
		{
			printChoiceDist( dist );
		}
	}
}
