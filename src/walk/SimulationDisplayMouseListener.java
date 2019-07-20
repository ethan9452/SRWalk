package walk;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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


		if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.WALKER )
		{

		}
		else if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.WALL )
		{

		}
		else if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.MAGNET )
		{

		}

		display.repaint();
		isMousePressed = true;
		prevLogicalPoint.move( logicalPoint.x, logicalPoint.y );
	}

	@Override
	public void mousePressed( MouseEvent e )
	{
		if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.NONE )
		{
			return;
		}

		Point logicalPoint = display.displayToLogical( e.getX(), e.getY() );

		if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.WALKER )
		{
			simulator.tryAddWalker( logicalPoint.x, logicalPoint.y );
		}
		else if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.WALL )
		{
			simulator.tryAddWall( logicalPoint.x, logicalPoint.y );
		}
		else if ( menuBar.getPaintBrushObjectType() == SimulationObjectType.MAGNET )
		{
			simulator.tryAddMagnet( logicalPoint.x, logicalPoint.y );
		}

		display.repaint();
		isMousePressed = true;
		prevLogicalPoint.move( logicalPoint.x, logicalPoint.y );
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
