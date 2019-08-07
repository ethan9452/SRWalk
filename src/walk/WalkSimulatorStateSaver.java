package walk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import walk.config.WalkConfig;
import walk.simulator.WalkSimulator;

public class WalkSimulatorStateSaver
{
	private static String SERIAL_FILE_EXT = ".ser";

	public static void loadSimulationState( String saveFileName, WalkSimulator stateToOverwrite )
	{
		loadSimulationState( WalkConfig.SAVED_STATE_DIR, saveFileName, stateToOverwrite );
	}
	
	public static void loadSimulationState( String dir, String saveFileName, WalkSimulator stateToOverwrite )
	{
		String fullFileName = dir + saveFileName + SERIAL_FILE_EXT;
		WalkSimulator loadedState;

		try
		{
			// Reading the object from a file
			FileInputStream file = new FileInputStream( fullFileName );
			ObjectInputStream in = new ObjectInputStream( file );

			// Method for deserialization of object
			loadedState = (WalkSimulator) in.readObject();

			in.close();
			file.close();

		}
		catch ( IOException | ClassNotFoundException ex )
		{
			System.out.println( "Could not load saved state " + ex );
			return;
		}

		transferWalkSimulationState( loadedState, stateToOverwrite );
		
		stateToOverwrite.postDeserializationInit();
	}

	private static void transferWalkSimulationState( WalkSimulator source, WalkSimulator destination )
	{
		destination.setRandom( source.getRandom() );
		destination.setRandomDist( source.getRandomDist() );
		destination.setCurrentWalkers( source.getCurrentWalkers() );
		destination.setCurrentMagnets( source.getCurrentMagnets() );
		destination.setCurrentWalls( source.getCurrentWalls() );
		destination.setIterCount( source.getIterCount() );
		destination.setCollisionsOn( source.isCollisionsOn() );
		destination.setReset( source.isReset() );
		destination.setPointsVisitedCount( source.getPointsVisitedCount() );
		destination.setHeatMapWeights( source.getHeatMapWeights() );
		destination.setBiggestX( source.getBiggestX() );
		destination.setBiggestY( source.getBiggestY() );
		destination.setSmallestX( source.getSmallestX() );
		destination.setSmallestY( source.getSmallestY() );
		destination.setFramesToAverageOverForFpsCalculation( source.getFramesToAverageOverForFpsCalculation() );
		destination.setLastFpsMeasureTimeMs( source.getLastFpsMeasureTimeMs() );
		destination.setActualFps( source.getActualFps() );
	}

	public static void saveSimulationState( String saveFileName, WalkSimulator stateToSave )
	{
		String fullFileName = WalkConfig.SAVED_STATE_DIR + saveFileName + SERIAL_FILE_EXT;

		try
		{
			FileOutputStream file = new FileOutputStream( fullFileName );
			ObjectOutputStream out = new ObjectOutputStream( file );

			// Method for serialization of object
			out.writeObject( stateToSave );

			out.close();
			file.close();
		}
		catch ( IOException e )
		{
			System.out.println( "Couldn't save state: " + e );
		}
	}

	public static List<String> getSaveFileNames()
	{
		return getSaveFileNames( WalkConfig.SAVED_STATE_DIR );
	}

	public static List<String> getSaveFileNames( String dir )
	{
		List<String> saveFileNames = new ArrayList<>();

		final File folder = new File( dir );

		for ( final File fileEntry : folder.listFiles() )
		{
			if ( !fileEntry.isDirectory() )
			{
				final String fileName = fileEntry.getName();

				// String to be scanned to find the pattern.
				String pattern = "(.*)\\.ser"; // .ser

				// Create a Pattern object
				Pattern r = Pattern.compile( pattern );

				// Now create matcher object.
				Matcher m = r.matcher( fileName );
				if ( m.find() )
				{
					saveFileNames.add( m.group( 1 ) );
				}
				else
				{
					System.out.println( "invalid file name " + fileName );
				}
			}
		}

		return saveFileNames;
	}
}
