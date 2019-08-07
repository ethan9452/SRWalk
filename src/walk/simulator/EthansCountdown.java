package walk.simulator;

public class EthansCountdown
{
	private int count;

	public EthansCountdown( int c )
	{
		//
		count = c;
	}

	public void reset( int c)
	{
		synchronized ( this )
		{
			count = c;
		}
	}

	public void countDown()
	{
		synchronized ( this )
		{
			count--;
		}
	}

	public int getCount()
	{
		synchronized ( this )
		{
			return count;			
		}
	}
}
