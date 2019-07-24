package walk;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.management.RuntimeErrorException;

import randomprovidor.IRandomChoiceProvidor;
import randomprovidor.RandomChoiceFromDistributionProvidor;

/**
 * Intended Flow:
 * 
 * 1. set possibleNextMoves based on simulation rules
 * 
 * 2. move walker -> which resets possibleNextMoves
 * 
 * @author ethanlo1
 *
 */
public class Walker extends Point
{
	public static final List<Point>			ALL_POSSIBLE_MOVE_VECTORS	= new ArrayList<Point>()
																		{
																			{
																				add( new Point( 0, 1 ) );
																				add( new Point( 1, 0 ) );
																				add( new Point( 0, -1 ) );
																				add( new Point( -1, 0 ) );
																			}
																		};

	private Map<Point, java.lang.Double>	possibleNextMoveWeights;

	public Walker( int x, int y )
	{
		super( x, y );

		possibleNextMoveWeights = new LinkedHashMap<>();
		resetPossibleNextMoves();
	}

	public Point getPotentialMoveLocation( Point move )
	{
		return new Point( this.x + move.x, this.y + move.y );
	}

	public void removePossibleMove( Point move )
	{
		possibleNextMoveWeights.remove( move );
	}

	/**
	 * Note: call this only after `possibleNextMoves` is valid
	 * 
	 * @param random
	 */
	public void moveRandomlyBasedOnPossibleMoves( RandomChoiceFromDistributionProvidor random )
	{
		if ( !possibleNextMoveWeights.isEmpty() )
		{
			final int choiceIdx = random.randomChoiceFromDist( getPossibleMoveWeights() );
			Point chosenMove = getNthFromPossibleNextMoves( choiceIdx );

			translate( chosenMove.x, chosenMove.y );
		}

		resetPossibleNextMoves();
	}

	private double[] getPossibleMoveWeights()
	{
		double[] ret = new double[possibleNextMoveWeights.size()];

		int i = 0;
		for ( java.lang.Double weight : possibleNextMoveWeights.values() )
		{
			ret[i] = weight;
			i++;
		}

		return ret;
	}

	private Point getNthFromPossibleNextMoves( int n )
	{
		int i = 0;
		for ( Point p : possibleNextMoveWeights.keySet() )
		{
			if ( n == i )
			{
				return p;
			}
			i++;
		}
		throw new IllegalArgumentException( "n is out of range" );
	}

	public void resetPossibleNextMoves()
	{
		for ( Point vec : ALL_POSSIBLE_MOVE_VECTORS )
		{
			possibleNextMoveWeights.put( vec, 1.0 );
		}
	}
	
	public Map<Point, java.lang.Double> getPossibleNextMoveWeights()
	{
		return possibleNextMoveWeights;
	}

}
