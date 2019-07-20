package walk;

import java.awt.Point;

public class Magnet extends Point
{

	private final boolean	isAttractive;

	private final double	k	= 1.;

	public Magnet( boolean isAttractive )
	{
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
