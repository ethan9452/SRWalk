package walk.simulator;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import walk.Walker;

/**
 * This one tries to do collisions with a hash set (wall + walker, not magnet)
 * 
 * @author ethanlo1
 *
 */
public class WalkSimulatorV3 extends WalkSimulatorV2
{
	private static final long	serialVersionUID		= 6684216469772281399L;

	// data structures for optimization
	protected Set<Point>		walkersOptDataStruct	= new HashSet<>();
	protected Set<Point>		wallsOptDataStruct		= new HashSet<>();

	@Override
	protected void processCollision()
	{
		if ( isCollisionsOn )
		{
			for ( Walker walker : currentWalkers )
			{
				walkersOptDataStruct.add( walker );
			}

			for ( Walker walker : currentWalkers )
			{
				for ( Point move : Walker.ALL_POSSIBLE_MOVE_VECTORS )
				{
					Point possibleMoveLocation = walker.getPotentialMoveLocation( move );

					if ( walkersOptDataStruct.contains( possibleMoveLocation ) )
					{
						walker.removePossibleMove( move );
					}
				}
			}

		}

		walkersOptDataStruct.clear();
	}

	@Override
	protected void processWalls()
	{
		for ( Point wall : currentWalls )
		{
			wallsOptDataStruct.add( wall );
		}

		for ( Walker walker : currentWalkers )
		{
			for ( Point move : Walker.ALL_POSSIBLE_MOVE_VECTORS )
			{
				Point possibleMoveLocation = walker.getPotentialMoveLocation( move );
				
				if ( wallsOptDataStruct.contains( possibleMoveLocation ))
				{
					walker.removePossibleMove( move );
				}

			}
		}

		wallsOptDataStruct.clear();
	}

}
