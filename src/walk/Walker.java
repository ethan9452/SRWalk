package walk;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import randomprovidor.IRandomChoiceProvidor;

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
	public static final List<Point>	ALL_POSSIBLE_MOVE_VECTORS	= new ArrayList<Point>()
																{
																	{
																		add( new Point( 0, 1 ) );
																		add( new Point( 1, 0 ) );
																		add( new Point( 0, -1 ) );
																		add( new Point( -1, 0 ) );
																	}
																};

	private ArrayListSet<Point>				possibleNextMoves;

	public Walker( int x, int y )
	{
		super( x, y );

		possibleNextMoves = new ArrayListSet<>( ALL_POSSIBLE_MOVE_VECTORS );
	}

	public Point getPotentialMoveLocation( Point move )
	{
		return new Point( this.x + move.x, this.y + move.y );
	}

	public void removePossibleMove( Point move )
	{
		possibleNextMoves.remove( move );
	}

	/**
	 * Note: call this only after `possibleNextMoves` is valid
	 * 
	 * @param random
	 */
	public void moveRandomlyBasedOnPossibleMoves( IRandomChoiceProvidor random )
	{
		if ( !possibleNextMoves.isEmpty() )
		{
			final int choiceIdx = random.randomChoice( possibleNextMoves.size() );
			Point chosenMove = possibleNextMoves.get( choiceIdx );

			translate( chosenMove.x, chosenMove.y );
		}

		resetPossibleNextMoves();
	}

	public void resetPossibleNextMoves()
	{
		possibleNextMoves.addAll( ALL_POSSIBLE_MOVE_VECTORS );
	}

}
