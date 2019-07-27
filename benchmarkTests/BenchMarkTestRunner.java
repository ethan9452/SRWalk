import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import walk.WalkSimulatorStateSaver;
import walk.simulator.WalkSimulator;

public class BenchMarkTestRunner
{
	static String							BENCHMARK_STATES_DIR	= "resources/benchmark_states/";
	static String							BENCHMARK_RESULTS_DIR	= "resources/benchmark_results/";

	static Class<? extends WalkSimulator>	simToTest;

	/*
	 * Dont change these
	 */
	static int[]							NUM_STEPS_TO_RUN		= { 100, 1000, 10000, 100000 };
	static int								MAX_RUN_MS				= 120 * 1000;

	public static void main( String[] argStrings )
	{
		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
		String benchmarkName;
		try
		{
			System.out.println( "Enter a name for this benchmark test: " );
			benchmarkName = reader.readLine();

			System.out.println( "Enter name of ? extends WalkSimulator class to test: " );
			String className = "walk.simulator." + reader.readLine();

			simToTest = (Class<? extends WalkSimulator>) Class.forName( className );

			System.out.println( "Confirm this is what you want " + simToTest.getName() + ". y/n" );
			String yesOrNo = reader.readLine();

			if ( !yesOrNo.equals( "y" ) )
			{
				return;
			}

		}
		catch ( IOException | ClassNotFoundException e )
		{
			System.out.println( "COuldn't run this benchmark test: " );
			e.printStackTrace();
			return;
		}

		
		try
		{
			runBenchmarks( benchmarkName );
		}
		catch ( InstantiationException | IllegalAccessException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void runBenchmarks( String benchmarkName ) throws InstantiationException, IllegalAccessException
	{
		List<String> statesToBenchMarkFilenames = WalkSimulatorStateSaver.getSaveFileNames( BENCHMARK_STATES_DIR );

		printMaxPossibleTime( statesToBenchMarkFilenames );

		Map<String, List<Integer>> runtimesPerFilename = new TreeMap<>();
		Map<String, List<Integer>> itersCompletedPerFilename = new TreeMap<>();

		for ( String filename : statesToBenchMarkFilenames )
		{
			System.out.println( "Running for " + filename );

			runtimesPerFilename.put( filename, new ArrayList<Integer>() );
			itersCompletedPerFilename.put( filename, new ArrayList<Integer>() );

			for ( int stepsToRun : NUM_STEPS_TO_RUN )
			{
				System.out.println( "Running for " + stepsToRun + " steps" );

				WalkSimulator simulator = simToTest.newInstance();
				WalkSimulatorStateSaver.loadSimulationState( BENCHMARK_STATES_DIR, filename, simulator );

				final long startTime = System.currentTimeMillis();

				int i;
				for ( i = 0; i < stepsToRun; i++ )
				{
					simulator.step();

					final long curTime = System.currentTimeMillis();
					if ( curTime - startTime >= MAX_RUN_MS )
					{
						break;
					}
				}

				final int runTime = (int) (System.currentTimeMillis() - startTime);

				runtimesPerFilename.get( filename ).add( runTime );
				itersCompletedPerFilename.get( filename ).add( i );
			}
		}
		//
		// System.out.println( runtimesPerFilename );
		// System.out.println( itersCompletedPerFilename );

		getFps( benchmarkName, runtimesPerFilename, itersCompletedPerFilename );
	}

	private static void printMaxPossibleTime( List<String> statesToBenchMarkFilenames )
	{
		long maxPossibleTimeMs = statesToBenchMarkFilenames.size() * NUM_STEPS_TO_RUN.length * MAX_RUN_MS;
		System.out.println( statesToBenchMarkFilenames.size() +
				" files for " +
				NUM_STEPS_TO_RUN.length +
				" trials, " +
				MAX_RUN_MS +
				" max ms per trial..." +
				((maxPossibleTimeMs / 1000) / 60) +
				" mins max" );
	}

	private static void getFps( String benchmarkName, Map<String, List<Integer>> runtimesPerFilename,
			Map<String, List<Integer>> itersCompletedPerFilename )
	{
		Map<String, List<Integer>> fpssPerFilename = new TreeMap<String, List<Integer>>();

		for ( String filename : runtimesPerFilename.keySet() )
		{
			fpssPerFilename.put( filename, new ArrayList<>() );

			List<Integer> iters = itersCompletedPerFilename.get( filename );
			List<Integer> runtimes = runtimesPerFilename.get( filename );

			for ( int i = 0; i < iters.size(); i++ )
			{
				double iter = (double) iters.get( i );
				double runtimeSec = (double) runtimes.get( i ) / 1000;

				fpssPerFilename.get( filename ).add( (int) (iter / runtimeSec) );
			}
		}

		persistBenchMarkResult( benchmarkName, fpssPerFilename );
	}

	private static void persistBenchMarkResult( String benchmarkName, Map<String, List<Integer>> fpssPerFilename )
	{
		DateFormat dateFormat = new SimpleDateFormat( "yyyy_MM_dd__HH_mm_ss" );
		Date date = new Date();
		// System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43

		final String fpsFilename = BENCHMARK_RESULTS_DIR
				+ benchmarkName
				+ "_"
				+ simToTest.getName()
				+ "_fps_"
				+ dateFormat.format( date )
				+ ".csv";

		PrintWriter fpsWriter;

		try
		{
			fpsWriter = new PrintWriter( fpsFilename, "UTF-8" );

			String firstRow = ",";
			for ( int step : NUM_STEPS_TO_RUN )
			{
				firstRow += step;
				firstRow += ",";
			}
			fpsWriter.println( firstRow );

			for ( String filename : fpssPerFilename.keySet() )
			{
				fpsWriter.print( filename + "," );
				for ( int runtimeMs : fpssPerFilename.get( filename ) )
				{
					fpsWriter.print( runtimeMs + "," );
				}
				fpsWriter.println();
			}
			fpsWriter.close();

		}
		catch ( FileNotFoundException | UnsupportedEncodingException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println( "Sorry! coudln't write test restuls to file." + e );
		}

		
		System.out.println( "DONE!!!!!!!!!!!!"  );
	}

}
