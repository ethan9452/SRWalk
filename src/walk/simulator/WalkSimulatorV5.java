package walk.simulator;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import walk.Walker;

/**
 * In V4, the bool[][] might have been a mistake, since walls are usually sparse
 * (not dense)
 * 
 * Switch back to hashset, but also employ the `isWallCacheInvalid` technique
 * 
 * @author ethanlo1
 *
 */
public class WalkSimulatorV5 extends WalkSimulatorV3
{
	private Set<Point>	wallsOptDataStruct	= new HashSet<>();

	private boolean		isWallCacheInvalid;

	public WalkSimulatorV5()
	{
		super();
		isWallCacheInvalid = true;
	}

	@Override
	protected void processWalls()
	{
		if ( isWallCacheInvalid )
		{
//			System.out.println( "regenerating wall cache" );

			wallsOptDataStruct.clear();

			for ( Point wall : currentWalls )
			{
				wallsOptDataStruct.add( wall );
			}

			isWallCacheInvalid = false;
		}

		for ( Walker walker : currentWalkers )
		{
			for ( Point move : Walker.ALL_POSSIBLE_MOVE_VECTORS )
			{
				Point possibleMoveLocation = walker.getPotentialMoveLocation( move );

				if ( wallsOptDataStruct.contains( possibleMoveLocation ) )
				{
					walker.removePossibleMove( move );
				}

			}
		}

	}

	@Override
	public void clearAllObjects()
	{
		super.clearAllObjects();
		isWallCacheInvalid = true;
	}

	@Override
	public void clearWalls()
	{
		super.clearWalls();
		isWallCacheInvalid = true;
	}

	@Override
	public void resetSimulationState()
	{
		super.resetSimulationState();
		isWallCacheInvalid = true;
	}

	@Override
	public void setCurrentWalls( List<Point> wall )
	{
		super.setCurrentWalls( wall );
		isWallCacheInvalid = true;
	}

	@Override
	public void tryAddWall( int x, int y )
	{
		super.tryAddWall( x, y );
		isWallCacheInvalid = true;
	}
}
