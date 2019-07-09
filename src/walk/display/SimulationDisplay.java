package walk.display;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.ietf.jgss.Oid;

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

	@Override
	public void paintComponent( Graphics graphics )
	{
		super.paintComponent( graphics );

		paintHeatMap( graphics );

		List<Point> points = simulator.getCurrentPoints();
		for ( Point p : points )
		{
			final int logicalX = p.x;
			final int logicalY = p.y;

			paintWalker( logicalX, logicalY, graphics, Color.black );
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

	private void paintWalker( int logicalX, int logicalY, Graphics graphics, Color color )
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
		Map<Point, Double> visitedPoints = simulator.getHeatMapWeightsMap();

		for ( Entry<Point, Double> visitedPoint : visitedPoints.entrySet() )
		{
			final int rDarknessMultiplier = (int) (visitedPoint.getValue() * 255.0);

			Color redShadeColor = new Color( 255, 255 - rDarknessMultiplier, 255 - rDarknessMultiplier );

			final int logicalX = visitedPoint.getKey().x;
			final int logicalY = visitedPoint.getKey().y;

			paintWalker( logicalX, logicalY, graphics, redShadeColor );
		}
	}
}
