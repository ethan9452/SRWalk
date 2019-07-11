package walk;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class WalkUtil
{

	public static List<Point> getSpiralIterationPointsList( int iters )
	{
		List<Point> points = new ArrayList<Point>();

		int i = 0;

		int x = 0;
		int y = 0;
		int dx = 1;
		int dy = 0;
		while ( i < iters )
		{
//			System.out.println( x + "," + y );
			points.add( new Point( x, y ) );

			if ( dy == 1 )
			{
				if ( y == x + 1 )
				{
					int newDy = dx;
					int newDx = -dy;

					dx = newDx;
					dy = newDy;
				}
			}
			else if ( Math.abs( x ) == Math.abs( y ) )
			{
				int newDy = dx;
				int newDx = -dy;

				dx = newDx;
				dy = newDy;
			}

			x += dx;
			y += dy;

			i++;
		}

		return points;
	}

}
