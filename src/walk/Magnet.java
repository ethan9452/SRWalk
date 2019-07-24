package walk;

import java.awt.Point;

public class Magnet extends Point
{

	private final boolean	isAttractive;

	private final double	k	= 10000.;

	public Magnet( int x, int y, boolean isAttractive )
	{
		super( x, y );
		this.isAttractive = isAttractive;
	}

	public boolean getIsAttractive()
	{
		return isAttractive;
	}

	public double getK()
	{
		return k;
	}
}
