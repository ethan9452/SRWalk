package util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import walk.WalkUtil;

class UtilsTest
{

	@Test
	void testGetSpiralIterationPointsList()
	{
		assertEquals( WalkUtil.getSpiralIterationPointsList(10).size(),	10 );	
	}

}
