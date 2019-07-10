package collisiondetectprovidor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class ICollisionDetectionProvidor
{
	public static List<Class<? extends ICollisionDetectionProvidor>> IMPLEMENTED_PROVIDORS = new ArrayList<Class<? extends ICollisionDetectionProvidor>>()
	{
		{
			add( NestedLoopCollisionDetectionProvidor.class );
		}
	};

	public boolean isColliding( Point p, List<Point> points);
	
}
