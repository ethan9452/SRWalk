package walk;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Point;

import org.junit.jupiter.api.Test;

import junit.framework.Assert;
import walk.display.MenuBarDisplay;
import walk.display.SimulationDisplay;
import walk.simulator.WalkSimulator;

class SimulationDisplayMouseListenerTests extends SimulationDisplayMouseListener
{
	static WalkSimulator		simulator;
	static SimulationDisplay	display;
	static MenuBarDisplay		menuBar;

	public SimulationDisplayMouseListenerTests()
	{
		super( simulator, display, menuBar );
		// TODO Auto-generated constructor stub
	}

	@Test
	void testGetHorizontalVerticalOrDiagonalMove()
	{
		Point move;

		move = SimulationDisplayMouseListenerTests.getHorizontalVerticalOrDiagonalMove( 0, 10 );
		assertTrue( move.equals( new Point( 0, 1 ) ) );

		move = SimulationDisplayMouseListenerTests.getHorizontalVerticalOrDiagonalMove( 10, 10 );
		assertTrue( move.equals( new Point( 1, 1 ) ) );

		move = SimulationDisplayMouseListenerTests.getHorizontalVerticalOrDiagonalMove( 1, 0 );
		assertTrue( move.equals( new Point( 1, 0 ) ) );

		move = SimulationDisplayMouseListenerTests.getHorizontalVerticalOrDiagonalMove( 0, 10 );
		assertTrue( move.equals( new Point( 0, 1 ) ) );

		move = SimulationDisplayMouseListenerTests.getHorizontalVerticalOrDiagonalMove( 0, -1 );
		assertTrue( move.equals( new Point( 0, -1 ) ) );

		move = SimulationDisplayMouseListenerTests.getHorizontalVerticalOrDiagonalMove( 1, 0 );
		assertTrue( move.equals( new Point( 1, 0 ) ) );

		move = SimulationDisplayMouseListenerTests.getHorizontalVerticalOrDiagonalMove( -33, -35 );
		assertTrue( move.equals( new Point( -1, -1 ) ) );

	}

	@Test
	void testGetPointsBetween()
	{
		List<Point> p = SimulationDisplayMouseListener.getPointsBetween( new Point( 3, 10 ), new Point( 10, 10 ) );

		System.out.println(p);
		
		List<Point> shouldBeThere = new ArrayList<Point>( Arrays.asList(
				new Point( 3, 10 ),
				new Point( 4, 10 ),
				new Point( 5, 10 ),
				new Point( 6, 10 ),
				new Point( 7, 10 ),
				new Point( 8, 10 ),
				new Point( 9, 10 ),
				new Point( 3, 10 ) ) );

		for ( Point shouldBe : shouldBeThere )
		{
			assertTrue( p.contains( shouldBe ) );
		}
	}

}
