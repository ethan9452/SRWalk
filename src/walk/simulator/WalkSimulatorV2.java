package walk.simulator;

import java.awt.Point;

import walk.Magnet;
import walk.Walker;

/**
 * All this one does was remove the debug check and adds some refactor
 * 
 * This is also the highest class in the heirarchy to have the `processCollision`, `processWall`, and `processMagnets` 
 * methods, which is needed for some of the benchmark testes
 * 
 * @author ethanlo1
 *
 */
public class WalkSimulatorV2 extends WalkSimulator
{
	private static final long serialVersionUID = 8192248024076791293L;

	protected void processCollision()
	{
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
	}

	protected void processWalls()
	{
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
	}

	protected void processMagets()
	{
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

	}

	@Override
	protected void makeRandomStep()
	{
		updateActualFps();

		processCollision();

		processWalls();

		processMagets();

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
	

