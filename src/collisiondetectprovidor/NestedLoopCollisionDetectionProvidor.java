package collisiondetectprovidor;

import java.awt.Point;
import java.util.List;

public class NestedLoopCollisionDetectionProvidor implements ICollisionDetectionProvidor
{

	@Override
	public void runCollisionDetect( List<Point> points, ICollisionCallback detectionCallback )
	{
		for ( int i = 0; i < points.size(); i++ )
		{
			for ( int j = i + 1; j < points.size(); j++ )
			{
				Point point1 = points.get(i);
				Point point2  = points.get(j);
				if ( point1.x == point2.x &&
						point1.y == point2.y )
				{
					detectionCallback.collisionCallback( point1, point2 );
				}
			}
		}
	}

}
