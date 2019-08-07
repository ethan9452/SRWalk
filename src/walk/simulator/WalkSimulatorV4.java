package walk.simulator;

import java.awt.Point;
import java.util.List;

import walk.Magnet;
import walk.Walker;

/**
 * In this one, I try to use the idea that walls don't move
 * 
 * Cache the set of walls, when adding walls invalidate
 * 
 * @author ethanlo1
 *
 */
public class WalkSimulatorV4 extends WalkSimulatorV3
{
	private boolean[][]	wallCache;							// [x][y]
	private boolean		isWallCacheInvalid;

	private int			smallestWallX	= Integer.MAX_VALUE;
	private int			smallestWallY	= Integer.MAX_VALUE;
	private int			biggestWallX	= Integer.MIN_VALUE;
	private int			biggestWallY	= Integer.MIN_VALUE;

	public WalkSimulatorV4()
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

			resetAndPopulateCacheSizeInfo();
			wallCache = new boolean[biggestWallX - smallestWallX + 1][biggestWallY - smallestWallY + 1];

			for ( Point wall : currentWalls )
			{
				int cacheX = getCacheXCoord( wall.x );
				int cacheY = getCacheYCoord( wall.y );

				wallCache[cacheX][cacheY] = true;
			}

			isWallCacheInvalid = false;
		}

		for ( Walker walker : currentWalkers )
		{
			for ( Point move : Walker.ALL_POSSIBLE_MOVE_VECTORS )
			{
				Point possibleMoveLocation = walker.getPotentialMoveLocation( move );

				if ( pointIsInCurrentWallRange( possibleMoveLocation ) )
				{
					int possibleMoveCacheX = getCacheXCoord( possibleMoveLocation.x );
					int possibleMoveCacheY = getCacheYCoord( possibleMoveLocation.y );

					if ( wallCache[possibleMoveCacheX][possibleMoveCacheY] == true )
					{
						walker.removePossibleMove( move );
					}

				}

			}
		}
	}

	private boolean pointIsInCurrentWallRange( Point p )
	{
		if ( p.x < smallestWallX )
		{
			return false;
		}

		if ( p.y < smallestWallY )
		{
			return false;
		}

		if ( p.x > biggestWallX )
		{
			return false;
		}

		if ( p.y > biggestWallY )
		{
			return false;
		}

		return true;
	}

	private void resetAndPopulateCacheSizeInfo()
	{
		smallestWallX = Integer.MAX_VALUE;
		smallestWallY = Integer.MAX_VALUE;
		biggestWallX = Integer.MIN_VALUE;
		biggestWallY = Integer.MIN_VALUE;

		for ( Point wall : currentWalls )
		{
			biggestWallX = Math.max( wall.x, biggestWallX );
			biggestWallY = Math.max( wall.y, biggestWallY );

			smallestWallX = Math.min( wall.x, smallestWallX );
			smallestWallY = Math.min( wall.y, smallestWallY );
		}

	}

	private int getCacheXCoord( int logicalX )
	{
		return logicalX - smallestWallX;
	}

	private int getCacheYCoord( int logicalY )
	{
		return logicalY - smallestWallY;
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
