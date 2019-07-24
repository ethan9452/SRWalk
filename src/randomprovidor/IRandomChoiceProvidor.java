package randomprovidor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class IRandomChoiceProvidor implements Serializable
{
	// TODO: this is kind of lame, is there a way to do this automatically ?
	// Reflection?
	public static List<Class<? extends IRandomChoiceProvidor>> IMPLEMENTED_PROVIDORS = new ArrayList<Class<? extends IRandomChoiceProvidor>>()
	{
		{
			add( DecimalThresholdRandomProvidor.class );
			add( DefaultLibraryRandomChoiceProvidor.class );
			add( ModAlgRandomProvidor.class );
		}
	};

	/**
	 * Returns a choice in [ 0, numChoices )
	 * 
	 * @param numChoices
	 * @return
	 */
	public abstract int randomUniformChoice( int numChoices );
}
