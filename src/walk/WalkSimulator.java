package walk;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import collisiondetectprovidor.ICollisionDetectionProvidor;
import collisiondetectprovidor.NestedLoopCollisionDetectionProvidor;
import randomprovidor.DefaultLibraryRandomChoiceProvidor;
import randomprovidor.IRandomChoiceProvidor;

/**
 * Simulates a point moving randomly in a X-Y cartiensn plane.
 * 
 * @author ethanlo1
 *
 */
public class WalkSimulator
{
	private IRandomChoiceProvidor	random;
	private ICollisionDetectionProvidor collisionDetector;

	private List<Point>				currentPoints;

	private int						iterCount		= 0;
	
	private boolean isCollisionsOn;

	// TODO: these variables are all "derived" from the simulation state, and
	// are for UI purposes only. maybe move out to anohter class
	private Map<Point, Integer>		pointsVisitedCount;
	private Map<Point, Double>		heatMapWeights	= new HashMap<>();

	private int						biggestX;
	private int						smallestX;
	private int						biggestY;
	private int						smallestY;

	public WalkSimulator( IRandomChoiceProvidor providor, ICollisionDetectionProvidor collisionDetector )
	{
		random = providor;
		this.collisionDetector = collisionDetector;
		
		currentPoints = new ArrayList<>();
		isCollisionsOn = false;

		resetSimulationState();
	}

	public WalkSimulator()
	{
		this( new DefaultLibraryRandomChoiceProvidor(), new NestedLoopCollisionDetectionProvidor() );
	}

	public void resetSimulationState()
	{
		for ( Point p : currentPoints )
		{
			p.move( 0, 0 );
		}

		pointsVisitedCount = new HashMap<Point, Integer>();
		biggestX = 0;
		biggestY = 0;
		smallestX = 0;
		smallestY = 0;

		iterCount = 0;
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
		currentPoints.add( new Point( 0, 0 ) );
	}

	public void removeWalker()
	{
		if ( !currentPoints.isEmpty() )
		{
			currentPoints.remove( currentPoints.size() - 1 );
		}
	}

	public void resetWalkersToOrigin()
	{
		for ( Point currentPoint : currentPoints )
		{
			currentPoint.move( 0, 0 );
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

	public void step()
	{
		recordCurrentPoint();
		makeRandomStep();

		iterCount++;
	}

	private void makeRandomStep()
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
			else
			{
				System.out.println( "Warning: rand choice should be in [0,3]. It is: " + choice );
			}

			currentPoint.move( x, y );

			biggestX = Math.max( x, biggestX );
			biggestY = Math.max( y, biggestY );
			smallestX = Math.min( x, smallestX );
			smallestY = Math.min( y, smallestY );
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

}
