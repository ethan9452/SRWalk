import java.util.ArrayList;
import java.util.List;

import walk.simulator.WalkSimulatorV2;
import walk.simulator.WalkSimulatorV3;
import walk.simulator.WalkSimulatorV4;
import walk.simulator.WalkSimulatorV5;
import walk.simulator.WalkSimulatorV6;

/**
 * Use this if you want a quick run, since BenchMarkTestRunner.java could take
 * upto an hr
 * 
 * 
 * @author ethanlo1
 *
 */
public class QuickBenchMarkTestRunner
{
	static int TIME_PER_METHOD_MS = 5000;

	// public static void main( String args[] ) throws InterruptedException
	// {
	//
	// for ( int i = 2; i < 30; i++ )
	// {
	// System.out.println( i + " threads test" );
	//
	// Walk6 walko = new Walk6( i );
	// setupWalkSimForTest( (WalkSimulatorV2) walko );
	//
	// testAndPrint( walko );
	// Thread.sleep( 7000 );
	//
	// }
	// }

	public static void main( String args[] )
	{
		List<TestableWalkSimulator> simsToTest = new ArrayList<TestableWalkSimulator>();
		 simsToTest.add( new Walk2() );
		 simsToTest.add( new Walk3() );
		 simsToTest.add( new Walk4() );
		 simsToTest.add( new Walk5() );
		simsToTest.add( new Walk6() );

		for ( TestableWalkSimulator sim : simsToTest )
		{
			setupWalkSimForTest( (WalkSimulatorV2) sim ); // !!!!! haha
		}

		System.out.println( "Testing process collision, walls, and magnets for each walk simulator." );
		for ( TestableWalkSimulator sim : simsToTest )
		{
			testAndPrint( sim );
		}

		for ( TestableWalkSimulator sim : simsToTest )
		{
			((WalkSimulatorV2) sim).terminateForShutdown();
		}
	}

	public static void setupWalkSimForTest( WalkSimulatorV2 sim )
	{
		sim.setCollisionsOn( true );

		for ( int i = 0; i < 5000; i++ )
		{
			sim.tryAddWalker( i, i );
			sim.tryAddWall( i + 2, i );
		}
		for ( int i = 0; i < 400; i++ )
		{
			sim.tryAddMagnet( -1, i, false );
		}
	}

	public static void testAndPrint( TestableWalkSimulator sim )
	{
		long processCollisionStartTime = System.currentTimeMillis();
		int processCollisionIters = 0;
		while ( System.currentTimeMillis() - processCollisionStartTime < TIME_PER_METHOD_MS )
		{
			sim.publicProcessCollision();
			processCollisionIters++;
		}

		long processWallsStartTime = System.currentTimeMillis();
		int processWallsIters = 0;
		while ( System.currentTimeMillis() - processWallsStartTime < TIME_PER_METHOD_MS )
		{
			sim.publicProcessWalls();
			processWallsIters++;
		}

		long processMagnetsStartTime = System.currentTimeMillis();
		int processMagnetsIters = 0;
		while ( System.currentTimeMillis() - processMagnetsStartTime < TIME_PER_METHOD_MS )
		{
			sim.publicProcessMagnets();
			processMagnetsIters++;
		}

		System.out.println( sim.getClass().getSimpleName() +
				": \t processCollisions iters: \t" + processCollisionIters +
				"\t processWalls iters: \t" + processWallsIters +
				"\t processMagnets iters: \t" + processMagnetsIters );
	}
}

interface TestableWalkSimulator
{
	public void publicProcessCollision();

	public void publicProcessWalls();

	public void publicProcessMagnets();
}

// TODO this is alota copypasta ... is there a better way?

class Walk2 extends WalkSimulatorV2 implements TestableWalkSimulator
{
	@Override
	public void publicProcessCollision()
	{
		super.processCollision();
	}

	@Override
	public void publicProcessWalls()
	{
		super.processWalls();
	}

	@Override
	public void publicProcessMagnets()
	{
		super.processMagets();
	}
}

class Walk3 extends WalkSimulatorV3 implements TestableWalkSimulator
{
	@Override
	public void publicProcessCollision()
	{
		super.processCollision();
	}

	@Override
	public void publicProcessWalls()
	{
		super.processWalls();
	}

	@Override
	public void publicProcessMagnets()
	{
		super.processMagets();
	}
}

class Walk4 extends WalkSimulatorV4 implements TestableWalkSimulator
{
	@Override
	public void publicProcessCollision()
	{
		super.processCollision();
	}

	@Override
	public void publicProcessWalls()
	{
		super.processWalls();
	}

	@Override
	public void publicProcessMagnets()
	{
		super.processMagets();
	}
}

class Walk5 extends WalkSimulatorV5 implements TestableWalkSimulator
{
	@Override
	public void publicProcessCollision()
	{
		super.processCollision();
	}

	@Override
	public void publicProcessWalls()
	{
		super.processWalls();
	}

	@Override
	public void publicProcessMagnets()
	{
		super.processMagets();
	}
}

class Walk6 extends WalkSimulatorV6 implements TestableWalkSimulator
{
	@Override
	public void publicProcessCollision()
	{
		super.processCollision();
	}

	@Override
	public void publicProcessWalls()
	{
		super.processWalls();
	}

	@Override
	public void publicProcessMagnets()
	{
		super.processMagets();
	}
}
