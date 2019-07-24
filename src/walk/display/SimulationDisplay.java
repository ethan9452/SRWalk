package walk.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.ietf.jgss.Oid;

import walk.Magnet;
import walk.WalkMain;
import walk.WalkSimulator;

public class SimulationDisplay extends JPanel
{
	WalkSimulator	simulator;

	final int		displayLengthPixels;
	int				displayPixelScale;	// logical to
										// display size

	public SimulationDisplay( WalkSimulator simulator, int displayLengthPixels, int displayPixelScale )
	{
		this.simulator = simulator;

		this.displayLengthPixels = displayLengthPixels;
		this.displayPixelScale = displayPixelScale;

		// setBackground( Color.LIGHT_GRAY );

	}

	public void setDisplayPixelScale( int displayPixelScale )
	{
		this.displayPixelScale = displayPixelScale;
	}

	public void paintIfTrue( boolean shouldPaint )
	{
		if ( shouldPaint )
		{
			repaint();
		}
	}

	@Override
	public void paintComponent( Graphics graphics )
	{
		super.paintComponent( graphics );

		paintHeatMap( graphics );

		for ( Magnet m : simulator.getCurrentMagnets() )
		{
			final int logicalX = m.x;
			final int logicalY = m.y;

			if ( m.getIsAttractive() )
			{
				paintPoint( logicalX, logicalY, graphics, Color.blue );
			}
			else
			{
				paintPoint( logicalX, logicalY, graphics, Color.red );
			}
		}

		for ( Point p : simulator.getCurrentPoints() )
		{
			final int logicalX = p.x;
			final int logicalY = p.y;

			paintPoint( logicalX, logicalY, graphics, Color.black );
		}

		for ( Point p : simulator.getCurrentWalls() )
		{
			final int logicalX = p.x;
			final int logicalY = p.y;

			paintPoint( logicalX, logicalY, graphics, Color.gray );
		}

	}

	/*
	 * Java paints the top left corner as 0,0 Y increases going down X increases
	 * going right.
	 * 
	 * I'd like 0,0 to be the middle of the screen Y increases going up X
	 * increases going right
	 */
	private Point logicalToDisplay( int logicalX, int logicalY )
	{
		final int paintX = (displayLengthPixels / 2) + (logicalX * displayPixelScale);
		final int paintY = (displayLengthPixels / 2) - (logicalY * displayPixelScale);

		return new Point( paintX, paintY );
	}

	public Point displayToLogical( int paintX, int paintY )
	{
		// hopefully this doesnt shit the bed
		final int scale = displayLengthPixels / (2 * displayPixelScale);
		final int logicalX = (paintX / displayPixelScale) - scale;
		final int logicalY = (-paintY / displayPixelScale) + scale;

		return new Point( logicalX, logicalY );
	}

	private void paintPoint( int logicalX, int logicalY, Graphics graphics, Color color )
	{
		Point center = logicalToDisplay( logicalX, logicalY );

		graphics.setColor( color );

		graphics.fillRect( center.x - (displayPixelScale / 2),
				center.y - (displayPixelScale / 2),
				displayPixelScale,
				displayPixelScale );
	}

	private void paintHeatMap( Graphics graphics )
	{
		Map<Point, Double> visitedPoints = simulator.updateAndGetHeatMapWeights();

		for ( Entry<Point, Double> visitedPoint : visitedPoints.entrySet() )
		{
			final int rDarknessMultiplier = (int) (visitedPoint.getValue() * 255.0);

			Color heatMapTrailColor = new Color( 255, 255, 255 - rDarknessMultiplier );

			final int logicalX = visitedPoint.getKey().x;
			final int logicalY = visitedPoint.getKey().y;

			paintPoint( logicalX, logicalY, graphics, heatMapTrailColor );
		}
	}

}
