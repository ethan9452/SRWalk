package walk;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import walk.display.MenuBarDisplay;
import walk.display.SimulationDisplay;

/**
 * Listens for mouse inputs and performs the corresponding operations on the
 * WalkSimulator
 * 
 * Note: I noticed that the MouseMotionListener callbacks don't get called as
 * frequently as I want. If the cursor is moved fast, it does not pick up all
 * the pixels touched. Therefore in this class I "estimate the points in the
 * middle" to get a smoother draw
 *
 * @author ethanlo1
 *
 */
public class SimulationDisplayMouseListener implements MouseListener, MouseMotionListener
{
	// Between 0 to pi/6 is closest to a horizontal, from pi/6 - pi/3 is closest
	// to a diagonal line, above is closest to a vertical line
	private static double		HORIZONTAL_MOVE_RISE_RUN_THRESHOLD	= Math.tan( Math.PI / 6. );
	private static double		DIAGONAL_MOVE_RISE_RUN_THRESHOLD	= Math.tan( Math.PI / 3. );

	private WalkSimulator		simulator;
	private SimulationDisplay	display;
	private MenuBarDisplay		menuBar;

	private boolean				isMousePressed;
	private Point				prevLogicalPoint;

	public SimulationDisplayMouseListener( WalkSimulator simulator, SimulationDisplay display, MenuBarDisplay menuBar )
	{
		this.simulator = simulator;
		this.display = display;
		this.menuBar = menuBar;

		isMousePressed = false;
		prevLogicalPoint = new Point();

	}

	@Override
	public void mouseDragged( MouseEvent e )
	{
		if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.NONE )
		{
			return;
		}

		Point logicalPoint = display.displayToLogical( e.getX(), e.getY() );
		
		List<Point> pointsToAdd = getPointsBetween( prevLogicalPoint, logicalPoint );
		
		for ( Point p : pointsToAdd )
		{
			addObjectBasedOnMenuBarState( p.x, p.y );
		}

		display.repaint();
		menuBar.updateStatsDisplays();
		menuBar.repaint();
		isMousePressed = true;
		prevLogicalPoint.move( logicalPoint.x, logicalPoint.y );
	}

	/**
	 * Given 2 points start and end, return a list of points that would make it
	 * "seem" like there is a line drawn between start and end
	 * 
	 * Note: will also return start and end
	 * 
	 * Algo Summary: each iteration, choose between a vertical, horizontal, or
	 * diagonal move: depending on which is closer to rise/run
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	protected static List<Point> getPointsBetween( Point start, Point end )
	{
		List<Point> pointsBetween = new ArrayList<Point>();
		pointsBetween.add( new Point( start ) );

		int verticalDistLeft = end.y - start.y;
		int horizontalDistLeft = end.x - start.x;

		Point curPoint = new Point ( start );
		
		while ( verticalDistLeft != 0 || horizontalDistLeft != 0 )
		{
			Point nextPointMove = getHorizontalVerticalOrDiagonalMove( horizontalDistLeft, verticalDistLeft );
			
			curPoint.translate( nextPointMove.x, nextPointMove.y );
			
			pointsBetween.add( new Point( curPoint ) );
			
			verticalDistLeft -= nextPointMove.y;
			horizontalDistLeft -= nextPointMove.x;
		}
		
		return pointsBetween;
	}

	protected static Point getHorizontalVerticalOrDiagonalMove( int horizontalDist, int verticalDist )
	{
		final int signOfHorizontal = (int) Math.signum( horizontalDist );
		final int signOfVertical = (int) Math.signum( verticalDist );

		if ( horizontalDist == 0 )
		{
			// to avoid divide by 0 err
			return new Point( 0, signOfVertical );
		}

		final double riseRunRatio = Math.abs( (double) verticalDist / (double) horizontalDist );

		if ( riseRunRatio < HORIZONTAL_MOVE_RISE_RUN_THRESHOLD )
		{
			return new Point( signOfHorizontal, 0 );
		}
		else if ( riseRunRatio < DIAGONAL_MOVE_RISE_RUN_THRESHOLD )
		{
			return new Point( signOfHorizontal, signOfVertical );
		}
		else
		{
			return new Point( 0, signOfVertical );
		}
	}

	@Override
	public void mousePressed( MouseEvent e )
	{
		if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.NONE )
		{
			return;
		}

		Point logicalPoint = display.displayToLogical( e.getX(), e.getY() );

		addObjectBasedOnMenuBarState( logicalPoint.x, logicalPoint.y );

		display.repaint();
		menuBar.updateStatsDisplays();
		menuBar.repaint();
		isMousePressed = true;
		prevLogicalPoint.move( logicalPoint.x, logicalPoint.y );
	}

	private void addObjectBasedOnMenuBarState( int x, int y )
	{
		if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.WALKER )
		{
			simulator.tryAddWalker( x, y );
		}
		else if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.WALL )
		{
			simulator.tryAddWall( x, y );
		}
		else if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.ATTRACTIVE_MAGNET )
		{
			simulator.tryAddMagnet( x, y, true );
		}
		else if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.REPELLANT_MAGNET )
		{
			simulator.tryAddMagnet( x, y, false );
		}
	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		isMousePressed = false;
	}

	////////////////
	//// Unused ////
	////////////////

	@Override
	public void mouseMoved( MouseEvent e )
	{
	}

	@Override
	public void mouseClicked( MouseEvent e )
	{
	}

	@Override
	public void mouseEntered( MouseEvent e )
	{
	}

	@Override
	public void mouseExited( MouseEvent e )
	{
	}
}
