package randomprovidor;

import java.util.Random;

/**
 * This one uses random choice from the java library
 * 
 * @author ethanlo1
 *
 */
public class DefaultLibraryRandomChoiceProvidor extends IRandomChoiceProvidor
{

	Random random;

	public DefaultLibraryRandomChoiceProvidor()
	{
		random = new Random();
	}

	@Override
	public int randomUniformChoice( int numChoices )
	{
		if ( numChoices == 0 )
		{
			return 0;
		}

		return random.nextInt( numChoices );
	}

}
