package collisiondetectprovidor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public interface ICollisionDetectionProvidor
{
	public static List<Class<? extends ICollisionDetectionProvidor>> IMPLEMENTED_PROVIDORS = new ArrayList<Class<? extends ICollisionDetectionProvidor>>()
	{
		{
			add( NestedLoopCollisionDetectionProvidor.class );
		}
	};

	/**
	 * Should find all colliding paris in `points` and call `detectionCallback`
	 * on each pair
	 *
	 * Note: implementing methods must call `detectionCallback` EXACTLY ONCE on
	 * each collided pair
	 *
	 * Note: 2 same points shouldn't count as a collision
	 * 
	 * @param points
	 * @param detectionCallback
	 */
	public void runCollisionDetect( List<Point> points, ICollisionCallback detectionCallback );
	
	
}
