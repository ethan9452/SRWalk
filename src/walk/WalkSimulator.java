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

import randomprovidor.DefaultLibraryRandomChoiceProvidor;
import randomprovidor.IRandomChoiceProvidor;
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

	private List<Point>				currentPoints;

	private int						iterCount		= 0;

	private boolean					isCollisionsOn;
	private boolean					isReset;

	// TODO: these variables are all "derived" from the simulation state, and
	// are for UI purposes only. maybe move out to anohter class
	private Map<Point, Integer>		pointsVisitedCount;
	private Map<Point, Double>		heatMapWeights	= new HashMap<>();

	private int						biggestX;
	private int						smallestX;
	private int						biggestY;
	private int						smallestY;

	public WalkSimulator( IRandomChoiceProvidor providor )
	{
		random = providor;

		currentPoints = new ArrayList<>();

		isCollisionsOn = false;
		isReset = true;

		resetSimulationState();
	}

	public WalkSimulator()
	{
		this( new DefaultLibraryRandomChoiceProvidor() );
	}

	public void resetSimulationState()
	{
		if ( isCollisionsOn )
		{
			List<Point> spiralIterationPoints = WalkUtil.getSpiralIterationPointsList( currentPoints.size() );
			for ( int i = 0; i < currentPoints.size(); i++ )
			{
				Point newLoc = spiralIterationPoints.get( i );
				currentPoints.get( i ).move( newLoc.x, newLoc.y );
			}
		}
		else
		{
			for ( Point p : currentPoints )
			{
				p.move( 0, 0 );
			}
		}

		biggestX = 0;
		biggestY = 0;
		smallestX = 0;
		smallestY = 0;

		iterCount = 0;

		pointsVisitedCount = new HashMap<Point, Integer>();

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
			int size = currentPoints.size();
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
				currentPoints.add( new Point( 0, 0 ) );
				resetSimulationState();
			}
			else
			{
				System.out.println( "ERROR: Cannot add walker until sim is reset" );
			}
		}
		else
		{
			currentPoints.add( new Point( 0, 0 ) );
		}

	}

	public void removeWalker()
	{
		if ( !currentPoints.isEmpty() )
		{
			currentPoints.remove( currentPoints.size() - 1 );
		}
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
		if ( isCollisionsOn )
		{
			// TODO can def optimize, and clean
			Point[] possibleMoveVectors = { new Point( 0, 1 ),
					new Point( 1, 0 ),
					new Point( 0, -1 ),
					new Point( -1, 0 ) };

			// TODO this is order dependedt. cannot resort currentPoints
			List<Set<Point>> blockedMovesForPoints = new ArrayList<>();

			for ( Point currentPoint : currentPoints )
			{
				Set<Point> blockedMovesForPoint = new HashSet<>();

				for ( Point otherPoint : currentPoints )
				{

					for ( Point moveVector : possibleMoveVectors )
					{
						if ( currentPoint.x + moveVector.x == otherPoint.x &&
								currentPoint.y + moveVector.y == otherPoint.y )
						{
							blockedMovesForPoint.add( moveVector );
						}

					}
				}

				blockedMovesForPoints.add( blockedMovesForPoint );
			}

			for ( int i = 0; i < currentPoints.size(); i++ )
			{
				Set<Point> blockedMoves = blockedMovesForPoints.get( i );
				List<Point> possibleMoves = new ArrayList<>( Arrays.asList( possibleMoveVectors ) );
				possibleMoves.removeAll( blockedMoves );

				if ( !possibleMoves.isEmpty() )
				{
					final int choice = random.randomChoice( possibleMoves.size() );

					int x = currentPoints.get( i ).x;
					int y = currentPoints.get( i ).y;
					
					Point chosenMove = possibleMoves.get( choice );
					
					x += chosenMove.x;
					y += chosenMove.y;
					
					currentPoints.get(i).move( x, y );

					biggestX = Math.max( x, biggestX );
					biggestY = Math.max( y, biggestY );
					smallestX = Math.min( x, smallestX );
					smallestY = Math.min( y, smallestY );
					
					// jesus christtt
				}
			}
		}
		else
		{
			for ( Point currentPoint : currentPoints )
			{
				int x = currentPoint.x;
				int y = currentPoint.y;

				final int choice = random.randomChoice( 4 );

				if ( choice == 0 )
				{
					x++;
				}
				else if ( choice == 1 )
				{
					x--;
				}
				else if ( choice == 2 )
				{
					y++;
				}
				else if ( choice == 3 )
				{
					y--;
				}

				currentPoint.move( x, y );

				biggestX = Math.max( x, biggestX );
				biggestY = Math.max( y, biggestY );
				smallestX = Math.min( x, smallestX );
				smallestY = Math.min( y, smallestY );
			}
		}

	}

	private void recordCurrentPoint()
	{
		for ( Point currentPoint : currentPoints )
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

	public void turnCollisionOff()
	{
		isCollisionsOn = false;
	}

	public void turnCollisionOn()
	{
		if ( isReset )
		{
			isCollisionsOn = true;
		}
		else
		{
			System.out.println( "ERROR: cannot turn on collisions unless sim is reset" );
		}
	}

	public int getIterCount()
	{
		return iterCount;
	}

	public int getNumWalkers()
	{
		return currentPoints.size();
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

	public List<Point> getCurrentPoints()
	{
		return currentPoints;
	}

}
