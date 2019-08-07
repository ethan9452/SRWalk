package walk.simulator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;

import org.junit.jupiter.api.Test;

import walk.Magnet;
import walk.Walker;

public class WalkSimulatorV6Test extends WalkSimulatorV6
{

	@Test
	void testProcessCollision()
	{
		isCollisionsOn = true;

		currentWalkers.add( new Walker( 0, 0 ) );
		currentWalkers.add( new Walker( 0, 1 ) );
		
		processCollision();
		
		
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().size() == 3 );
		assertTrue( currentWalkers.get( 1 ).getPossibleNextMoveWeights().size() == 3 );
		
		assertTrue( !currentWalkers.get( 0 ).getPossibleNextMoveWeights().containsKey( new Point( 0, 1 ) ) );
		assertTrue( !currentWalkers.get( 1 ).getPossibleNextMoveWeights().containsKey( new Point( 0, 0 ) ) );
	}
	
	@Test
	void testProcessWalls()
	{
		currentWalls.add( new Point( 1, 0 ) );
		currentWalls.add( new Point( -1, 0 ) );
		
		currentWalkers.add( new Walker( 0, 0 ) );
		
		processWalls();
		
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().size() == 2 );
		
		assertTrue( !currentWalkers.get( 0 ).getPossibleNextMoveWeights().containsKey( new Point( 1, 0 ) ) );
		assertTrue( !currentWalkers.get( 0 ).getPossibleNextMoveWeights().containsKey( new Point( -1, 0 ) ) );
	}
	
	@Test
	void testProcessMagnets1()
	{
		currentWalkers.add( new Walker( 0, 0 ) );
		
		currentMagnets.add( new Magnet( 10, 0, true ) );
		
		processMagets();
		
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().get( new Point( 0, 1 ) ) == 1.0 );
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().get( new Point( 1, 0 ) ) == 101.0 );
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().get( new Point( 0, -1 ) ) == 1.0 );
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().get( new Point( -1, 0 ) ) == 1.0 );
	}
	
	@Test
	void testProcessMagnets2()
	{
		currentWalkers.add( new Walker( 0, 0 ) );
		
		currentMagnets.add( new Magnet( 0, 0, true ) );
		
		processMagets();
		
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().get( new Point( 0, 1 ) ) == 1.0 );
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().get( new Point( 1, 0 ) ) == 1.0 );
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().get( new Point( 0, -1 ) ) == 1.0 );
		assertTrue( currentWalkers.get( 0 ).getPossibleNextMoveWeights().get( new Point( -1, 0 ) ) == 1.0 );
	}
}
