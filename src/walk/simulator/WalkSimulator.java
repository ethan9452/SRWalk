package walk.simulator;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import randomprovidor.DefaultLibraryRandomChoiceProvidor;
import randomprovidor.IRandomChoiceProvidor;
import randomprovidor.RandomChoiceFromDistributionProvidor;
import walk.Magnet;
import walk.WalkUtil;
import walk.Walker;

/**
 * Simulates a point moving randomly in a X-Y cartiensn plane.
 * 
 * @author ethanlo1
 *
 */
public class WalkSimulator implements Serializable
{
	private static final long						serialVersionUID						= 6550001421005846783L;

	private IRandomChoiceProvidor					random;
	private RandomChoiceFromDistributionProvidor	randomDist;

	private List<Walker>							currentWalkers;
	private List<Magnet>							currentMagnets;
	private List<Point>								currentWalls;

	private int										iterCount;

	private boolean									isCollisionsOn;

	private boolean									isReset;

	// TODO: these variables are all "derived" from the simulation state, and
	// are for UI purposes only. maybe move out to anohter class
	private Map<Point, Integer>						pointsVisitedCount;
	private Map<Point, Double>						heatMapWeights;

	private int										biggestX;
	private int										smallestX;
	private int										biggestY;
	private int										smallestY;

	private int										framesToAverageOverForFpsCalculation	= 10;
	private long									lastFpsMeasureTimeMs;
	private double									actualFps;

	public WalkSimulator( IRandomChoiceProvidor providor, RandomChoiceFromDistributionProvidor randomDistProv )
	{
		random = providor;
		randomDist = randomDistProv;

		currentWalkers = new ArrayList<>();
		currentMagnets = new ArrayList<>();
		currentWalls = new ArrayList<>();

		pointsVisitedCount = new HashMap<>();
		heatMapWeights = new HashMap<>();

		isCollisionsOn = false;
		isReset = true;
		iterCount = 0;

		resetSimulationState();
	}

	public WalkSimulator()
	{
		this( new DefaultLibraryRandomChoiceProvidor(), new RandomChoiceFromDistributionProvidor() );
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
				if ( !currentWalls.contains( spiralIterationPoints.get( spiralIdx ) ) )
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

	public void tryAddWalker( int x, int y )
	{
		Walker potentialNew = new Walker( x, y );

		if ( isCollisionsOn )
		{
			if ( currentWalkers.contains( potentialNew ) )
			{
				return;
			}
		}

		if ( currentWalls.contains( potentialNew ) )
		{
			return;
		}

		currentWalkers.add( potentialNew );
	}

	public void tryAddWall( int x, int y )
	{
		Point potentialNew = new Point( x, y );

		// check wall
		if ( currentWalls.contains( potentialNew ) )
		{
			return;

		}

		if ( currentWalkers.contains( potentialNew ) )
		{
			return;
		}

		currentWalls.add( potentialNew );
	}

	public void tryAddMagnet( int x, int y, boolean isAttractive )
	{
		currentMagnets.add( new Magnet( x, y, isAttractive ) );
	}

	public void clearWalls()
	{
		currentWalls.clear();
	}

	// TODO could move this to another function, since this is not strictly
	// related to the sim state
	// this method is for drawing the "heatmap"
	public Map<Point, Double> updateAndGetHeatMapWeights()
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

	protected void makeRandomStep()
	{
		debugNoCollisionsCheck(); // TODO remove if not debugging

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
		// Factor in magnets
		//
		// TODO something is wrong here
		// hahhahahahahahaahhhahhahahahahahahahahahahahahahahahahahahahahahahah
		for ( Walker curWalker : currentWalkers )
		{
			for ( Magnet magnet : currentMagnets )
			{
				double dist = curWalker.distance( magnet );

				if ( dist == 0 )
					continue;

				double distX = curWalker.x - magnet.x; // assuming repel
				double distY = curWalker.y - magnet.y;

				if ( magnet.getIsAttractive() )
				{
					distX = -distX;
					distY = -distY;
				} // todo i think effect hast o be abs val

				double effectX = (distX / dist) * (magnet.getK() / (dist * dist));
				double effectY = (distY / dist) * (magnet.getK() / (dist * dist));

				Point horizontalEffect = new Point( (int) Math.signum( effectX ), 0 );
				Point verticalEffect = new Point( 0, (int) Math.signum( effectY ) );

				effectX = Math.abs( effectX );
				effectY = Math.abs( effectY );

				if ( curWalker.getPossibleNextMoveWeights().containsKey( horizontalEffect ) )
				{
					double curWeight = curWalker.getPossibleNextMoveWeights().get( horizontalEffect );
					curWalker.getPossibleNextMoveWeights().put( horizontalEffect, curWeight + effectX );
				}

				if ( curWalker.getPossibleNextMoveWeights().containsKey( verticalEffect ) )
				{
					double curWeight = curWalker.getPossibleNextMoveWeights().get( verticalEffect );
					curWalker.getPossibleNextMoveWeights().put( verticalEffect, curWeight + effectY );
				}
			}
		}

		//
		// make the move, based off possible moves
		//
		for ( Walker walker : currentWalkers )
		{
			walker.moveRandomlyBasedOnPossibleMoves( randomDist );

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

	public int getNumWalls()
	{
		return currentWalls.size();
	}

	public int getNumMagnets()
	{
		return currentMagnets.size();
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

	public List<Magnet> getCurrentMagnets()
	{
		return currentMagnets;
	}

	public void debugNoCollisionsCheck()
	{
		// if ( isCollisionsOn )
		// {
		// for ( int i = 0; i < currentWalkers.size(); i++ )
		// {
		// for ( int j = i + 1; j < currentWalkers.size(); j++ )
		// {
		// if ( currentWalkers.get( i ).equals( currentWalkers.get( j ) ) )
		// {
		// System.out.println( "Error: walker collision when isCollisionsOn: " +
		// currentWalkers.get( i )
		// + currentWalkers.get( j ) );
		// }
		// }
		// }
		// }

		// Walkers cannot intersect wall
		for ( int i = 0; i < currentWalkers.size(); i++ )
		{
			for ( int j = i + 1; j < currentWalls.size(); j++ )
			{
				if ( currentWalkers.get( i ).equals( currentWalls.get( j ) ) )
				{
					System.out.println( "Error: walker collision with wall: " + currentWalkers.get( i )
							+ currentWalls.get( j ) );
				}
			}
		}

		// Wall cannot intersect wall
		for ( int i = 0; i < currentWalls.size(); i++ )
		{
			for ( int j = i + 1; j < currentWalls.size(); j++ )
			{
				if ( currentWalls.get( i ).equals( currentWalls.get( j ) ) )
				{
					System.out.println( "Error: wall collision with wall: " + currentWalls.get( i )
							+ currentWalls.get( j ) );
				}
			}
		}
	}

	//
	// This is for saved state
	//

	public IRandomChoiceProvidor getRandom()
	{
		return random;
	}

	public void setRandom( IRandomChoiceProvidor random )
	{
		this.random = random;
	}

	public RandomChoiceFromDistributionProvidor getRandomDist()
	{
		return randomDist;
	}

	public void setRandomDist( RandomChoiceFromDistributionProvidor randomDist )
	{
		this.randomDist = randomDist;
	}

	public boolean isCollisionsOn()
	{
		return isCollisionsOn;
	}

	public void setCollisionsOn( boolean isCollisionsOn )
	{
		this.isCollisionsOn = isCollisionsOn;
	}

	public boolean isReset()
	{
		return isReset;
	}

	public void setReset( boolean isReset )
	{
		this.isReset = isReset;
	}

	public Map<Point, Integer> getPointsVisitedCount()
	{
		return pointsVisitedCount;
	}

	public void setPointsVisitedCount( Map<Point, Integer> pointsVisitedCount )
	{
		this.pointsVisitedCount = pointsVisitedCount;
	}

	public Map<Point, Double> getHeatMapWeights()
	{
		return heatMapWeights;
	}

	public void setHeatMapWeights( Map<Point, Double> heatMapWeights )
	{
		this.heatMapWeights = heatMapWeights;
	}

	public int getFramesToAverageOverForFpsCalculation()
	{
		return framesToAverageOverForFpsCalculation;
	}

	public void setFramesToAverageOverForFpsCalculation( int framesToAverageOverForFpsCalculation )
	{
		this.framesToAverageOverForFpsCalculation = framesToAverageOverForFpsCalculation;
	}

	public long getLastFpsMeasureTimeMs()
	{
		return lastFpsMeasureTimeMs;
	}

	public void setLastFpsMeasureTimeMs( long lastFpsMeasureTimeMs )
	{
		this.lastFpsMeasureTimeMs = lastFpsMeasureTimeMs;
	}

	public List<Walker> getCurrentWalkers()
	{
		return currentWalkers;
	}

	public void setBiggestX( int biggestX )
	{
		this.biggestX = biggestX;
	}

	public void setSmallestX( int smallestX )
	{
		this.smallestX = smallestX;
	}

	public void setBiggestY( int biggestY )
	{
		this.biggestY = biggestY;
	}

	public void setSmallestY( int smallestY )
	{
		this.smallestY = smallestY;
	}

	public void setActualFps( double actualFps )
	{
		this.actualFps = actualFps;
	}

	public void setCurrentWalkers( List<Walker> walkers )
	{
		currentWalkers = walkers;
	}

	public void setCurrentMagnets( List<Magnet> magnets )
	{
		currentMagnets = magnets;
	}

	public void setCurrentWalls( List<Point> wall )
	{
		currentWalls = wall;
	}

	public void setIterCount( int itc )
	{
		iterCount = itc;
	}

}
