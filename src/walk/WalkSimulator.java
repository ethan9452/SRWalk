package walk;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import randomprovidor.DecimalThresholdRandomProvidor;
import randomprovidor.DefaultLibraryRandomChoiceProvidor;
import randomprovidor.IRandomChoiceProvidor;
import randomprovidor.ModAlgRandomProvidor;
import spiraliterationprovodor.PointCallback;

/**
 * Simulates a point moving randomly in a X-Y cartiensn plane.
 * 
 * @author ethanlo1
 *
 */
public class WalkSimulator
{
	private IRandomChoiceProvidor	random;

	private List<Walker>			currentWalkers;
	private List<Point>				currentMagnets;
	private List<Point>				currentWalls;

	private int						iterCount								= 0;

	private boolean					isCollisionsOn;
	private boolean					isReset;

	// TODO: these variables are all "derived" from the simulation state, and
	// are for UI purposes only. maybe move out to anohter class
	private Map<Point, Integer>		pointsVisitedCount;
	private Map<Point, Double>		heatMapWeights;

	private int						biggestX;
	private int						smallestX;
	private int						biggestY;
	private int						smallestY;

	private int						framesToAverageOverForFpsCalculation	= 10;
	private long					lastFpsMeasureTimeMs;
	private double					actualFps;

	public WalkSimulator( IRandomChoiceProvidor providor )
	{
		random = providor;

		currentWalkers = new ArrayList<>();
		currentMagnets = new ArrayList<>();
		currentWalls = new ArrayList<>();

		pointsVisitedCount = new HashMap<>();
		heatMapWeights = new HashMap<>();

		isCollisionsOn = false;
		isReset = true;

		resetSimulationState();
	}

	public WalkSimulator()
	{
		this( new DefaultLibraryRandomChoiceProvidor() );
		// this( new DecimalThresholdRandomProvidor() );
		// this( new ModAlgRandomProvidor() );
	}

	public void clearAllObjects()
	{
		currentWalkers.clear();
		currentMagnets.clear();
		currentWalls.clear();

		resetSimulationStats();
	}

	public void resetSimulationState()
	{
		if ( isCollisionsOn )
		{
			// TODO this is shit, ideally have a object with
			// "getNextSpiralCoord"
			final int maximumPossiblePointsToTraverse = currentWalkers.size() + currentWalls.size();

			List<Point> spiralIterationPoints = WalkUtil
					.getSpiralIterationPointsList( maximumPossiblePointsToTraverse );

			int spiralIdx = 0;
			int walkerIdx = 0;
			while ( walkerIdx < currentWalkers.size() )
			{
				// Check that it doesn't intersect with a wall
				if ( !currentWalls.contains( currentWalkers.get( walkerIdx ) ) )
				{
					Point newLoc = spiralIterationPoints.get( spiralIdx );
					currentWalkers.get( walkerIdx ).move( newLoc.x, newLoc.y );
					currentWalkers.get( walkerIdx ).resetPossibleNextMoves();

					walkerIdx++;
				}

				spiralIdx++;
			}
		}
		else
		{
			for ( Walker p : currentWalkers )
			{
				p.move( 0, 0 );
				p.resetPossibleNextMoves();
			}
		}

		resetSimulationStats();
	}

	private void resetSimulationStats()
	{
		biggestX = 0;
		biggestY = 0;
		smallestX = 0;
		smallestY = 0;

		lastFpsMeasureTimeMs = System.currentTimeMillis();
		actualFps = 0;

		iterCount = 0;

		pointsVisitedCount.clear();

		isReset = true;
	}

	public void setRandomProvidor( Class<IRandomChoiceProvidor> clazz )
	{
		try
		{
			random = clazz.newInstance();
		}
		catch ( InstantiationException | IllegalAccessException e )
		{
			System.out.println( "Unable to set randomProvidor" );
			e.printStackTrace();
		}
	}

	public void setWalkerCount( int numWalkers )
	{
		if ( numWalkers >= 0 )
		{
			int size = currentWalkers.size();
			if ( size > numWalkers )
			{
				for ( int i = 0; i < size - numWalkers; i++ )
				{
					removeWalker();
				}
			}
			else if ( size < numWalkers )
			{
				for ( int i = 0; i < numWalkers - size; i++ )
				{
					addWalker();
				}
			}
		}
	}

	public void addWalker()
	{
		if ( isCollisionsOn )
		{
			if ( isReset )
			{
				// TODO this seems like it can be optimized
				currentWalkers.add( new Walker( 0, 0 ) );
				resetSimulationState();
			}
			else
			{
				System.out.println( "ERROR: Cannot add walker until sim is reset" );
			}
		}
		else
		{
			currentWalkers.add( new Walker( 0, 0 ) );
		}
	}

	public void removeWalker()
	{
		if ( !currentWalkers.isEmpty() )
		{
			currentWalkers.remove( currentWalkers.size() - 1 );
		}
	}

	public void addWall( int x, int y )
	{
		currentWalls.add( new Point( x, y ) );
	}

	public void clearWalls()
	{
		currentWalls.clear();
	}

	// TODO could move this to another function, since this is not strictly
	// related to the sim state
	// this method is for drawing the "heatmap"
	public Map<Point, Double> getHeatMapWeightsMap()
	{
		updateHeatMapWeights();

		return heatMapWeights;
	}

	public void updateHeatMapWeights()
	{
		Map<Point, Double> pointsVisitedFractionOfMaxMap = new HashMap<>();

		int maxVisitedCount = 0;

		for ( Entry<Point, Integer> pointCount : pointsVisitedCount.entrySet() )
		{
			maxVisitedCount = Math.max( maxVisitedCount, pointCount.getValue() );
		}

		for ( Entry<Point, Integer> pointCount : pointsVisitedCount.entrySet() )
		{
			final double fractionOfMax = ((double) pointCount.getValue() / (double) maxVisitedCount);

			pointsVisitedFractionOfMaxMap.put( new Point( pointCount.getKey() ), fractionOfMax );
		}

		heatMapWeights = pointsVisitedFractionOfMaxMap;
	}

	public void step()
	{

		recordCurrentPoint();
		makeRandomStep();

		iterCount++;
		isReset = false;
	}

	private void makeRandomStep()
	{
		
		updateActualFps();

		if ( isCollisionsOn )
		{
			//
			// set walkers' possible moves such that they cannot move into a
			// space currently occipied by a walker
			//
			for ( Walker curWalker : currentWalkers )
			{
				for ( Walker otherWalker : currentWalkers )
				{
					for ( Point move : Walker.ALL_POSSIBLE_MOVE_VECTORS )
					{
						Point possibleMoveLocation = curWalker.getPotentialMoveLocation( move );

						if ( otherWalker.equals( possibleMoveLocation ) )
						{
							curWalker.removePossibleMove( move );
						}
					}
				}
			}
		}

		//
		// factor in Walls
		//
		for ( Walker curWalker : currentWalkers )
		{
			for ( Point wall : currentWalls )
			{
				for ( Point move : Walker.ALL_POSSIBLE_MOVE_VECTORS )
				{
					Point possibleMoveLocation = curWalker.getPotentialMoveLocation( move );

					if ( wall.equals( possibleMoveLocation ) )
					{
						curWalker.removePossibleMove( move );
					}
				}
			}
		}

		//
		// make the move, based off possible moves
		//
		for ( Walker walker : currentWalkers )
		{
			walker.moveRandomlyBasedOnPossibleMoves( random );

			biggestX = Math.max( walker.x, biggestX );
			biggestY = Math.max( walker.y, biggestY );
			smallestX = Math.min( walker.x, smallestX );
			smallestY = Math.min( walker.y, smallestY );
		}

	}

	private void updateActualFps()
	{
		if ( iterCount % framesToAverageOverForFpsCalculation == 0 )
		{
			final long currentTimeMs = System.currentTimeMillis();
			long timePassed = currentTimeMs - lastFpsMeasureTimeMs;

			if ( timePassed == 0 )
			{
				timePassed = 1;
			}

			// I hope currentTime - lastMeasuredTime would never bust out of
			// int...
			actualFps = ((double) framesToAverageOverForFpsCalculation / (double) timePassed) * 1000;

			lastFpsMeasureTimeMs = currentTimeMs;
		}
	}

	private void recordCurrentPoint()
	{
		for ( Point currentPoint : currentWalkers )
		{
			if ( pointsVisitedCount.containsKey( currentPoint ) )
			{
				final Integer newCount = pointsVisitedCount.get( currentPoint ) + 1;
				pointsVisitedCount.put( new Point( currentPoint ), newCount );
			}
			else
			{
				pointsVisitedCount.put( new Point( currentPoint ), 1 );
			}
		}
	}

	public boolean getIsCollisionOn()
	{
		return isCollisionsOn;
	}

	public void turnCollisionOff()
	{
		isCollisionsOn = false;
	}

	public void tryTurnCollisionOn()
	{
		if ( isReset )
		{
			isCollisionsOn = true;

			// NOTE: this is a bit hacky. this is to imediatly un-collide the
			// walkers
			resetSimulationState();
		}
		else
		{
			System.out.println( "INFO: cannot turn on collisions unless sim is reset" );
		}
	}

	public int getIterCount()
	{
		return iterCount;
	}

	public int getNumWalkers()
	{
		return currentWalkers.size();
	}

	public int getBiggestX()
	{
		return biggestX;
	}

	public int getSmallestX()
	{
		return smallestX;
	}

	public int getBiggestY()
	{
		return biggestY;
	}

	public int getSmallestY()
	{
		return smallestY;
	}

	public double getActualFps()
	{
		return actualFps;
	}

	public List<? extends Point> getCurrentPoints()
	{
		return currentWalkers;
	}

	public List<Point> getCurrentWalls()
	{
		return currentWalls;
	}

	public List<Point> getCurrentMagnets()
	{
		return currentMagnets;
	}

	public void debugNoCollisionsCheck()
	{
		if ( isCollisionsOn )
		{
			for ( int i = 0; i < currentWalkers.size(); i++ )
			{
				for ( int j = i + 1; j < currentWalkers.size(); j++ )
				{
					if ( currentWalkers.get( i ).equals( currentWalkers.get( j ) ) )
					{
						System.out.println( "Error: walker collision when isCollisionsOn: " + currentWalkers.get( i )
								+ currentWalkers.get( j ) );
					}
				}
			}
		}
	}

}
