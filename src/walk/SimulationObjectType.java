package walk;

import java.util.Map;
import java.util.TreeMap;

public enum SimulationObjectType
{
	NONE,
	WALKER,
	ATTRACTIVE_MAGNET,
	REPELLANT_MAGNET,
	WALL;

	public static Map<String, SimulationObjectType> displayNameToEnum = new TreeMap<String, SimulationObjectType>()
	{
		{
			put( "None", NONE );
			put( "Walker", WALKER );
			put( "Magnet (attract)", ATTRACTIVE_MAGNET );
			put( "Magnet (repel)", REPELLANT_MAGNET );
			put( "Wall", WALL );
		}
	};
	
	public static String[] getDisplayNames()
	{
		String[] ret = new String[displayNameToEnum.size()];
		
		int haha = 0;
		for ( String d : displayNameToEnum.keySet())
		{
			ret[haha] = d;
			haha++;
		}
		
		return ret;
	}
}
