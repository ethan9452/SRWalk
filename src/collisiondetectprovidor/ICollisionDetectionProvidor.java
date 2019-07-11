package collisiondetectprovidor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


/* TODO only implement this if i plan to make multiple Collision dection classes
 * 
 * */

public abstract class ICollisionDetectionProvidor
{
	public static List<Class<? extends ICollisionDetectionProvidor>> IMPLEMENTED_PROVIDORS = new ArrayList<Class<? extends ICollisionDetectionProvidor>>()
	{
		{
			
		}
	};

	public abstract  boolean isColliding( Point p, List<Point> points);
	
}
