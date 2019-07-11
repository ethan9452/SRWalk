//package spiraliterationprovodor;
//
///**
// * Iterate x-y points in spiral fashion
// * 
// * @author ethanlo1
// *
// */
//public class SpiralIterationProvidor
//{
//	public static void sprial( int iters, PointCallback callback )
//	{
//		int x = 0;
//		int y = 0;
//		int dx = 1;
//		int dy = 0;
//		while ( iters < 20 )
//		{
//			System.out.println( x + "," + y );
//			callback.pointCallback( x, y );
//
//			if ( dy == 1 )
//			{
//				if ( y == x + 1 )
//				{
//					int newDy = dx;
//					int newDx = -dy;
//
//					dx = newDx;
//					dy = newDy;
//				}
//			}
//			else if ( Math.abs( x ) == Math.abs( y ) )
//			{
//				int newDy = dx;
//				int newDx = -dy;
//
//				dx = newDx;
//				dy = newDy;
//			}
//
//			x += dx;
//			y += dy;
//
//			iters++;
//		}
//
//	}
//
//}
