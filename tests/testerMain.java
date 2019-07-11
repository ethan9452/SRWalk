
public class testerMain
{
	public static void main( String[] a )
	{
		// Spiral, adding walkers.
		/**/
		int t = 0;
		
		int x = 0;
		int y = 0;
		int dx = 1;
		int dy = 0;
		while ( t < 20 )
		{
			System.out.println( x + "," + y );
			
			if(dy == 1)
			{
				if (y == x + 1)
				{
					int newDy = dx;
					int newDx = -dy;

					dx = newDx;
					dy = newDy;
				}
			}
			else if (Math.abs( x ) == Math.abs( y ) )
			{
				int newDy = dx;
				int newDx = -dy;

				dx = newDx;
				dy = newDy;
			}
			

			x += dx;
			y += dy;

			
			
			
			t++;
		}

	}
}
