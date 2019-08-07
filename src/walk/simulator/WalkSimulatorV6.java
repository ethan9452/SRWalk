package walk.simulator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import walk.Magnet;
import walk.Walker;

/**
 * Try use threads to process walker-magnet interaction faster
 * 
 * @author ethanlo1
 *
 */
public class WalkSimulatorV6 extends WalkSimulatorV5
{
	public int							numThreads;

	private List<ProcessMagnetsTask>	tasks;

	private ExecutorService				executorService;

	private EthansCountdown				latch;

	private int							lastNumWalkers;

	public WalkSimulatorV6()
	{
		this( 10 );
	}

	public WalkSimulatorV6( int numThreads )
	{
		super();

		this.numThreads = numThreads;

		lastNumWalkers = 0;

		executorService = Executors.newFixedThreadPool( numThreads );

		latch = new EthansCountdown( numThreads );

		tasks = new ArrayList<>();
		for ( List<Integer> indexes : getIndexesForEachThread( currentWalkers.size(), numThreads ) )
		{
			ProcessMagnetsTask task = new ProcessMagnetsTask( indexes, currentWalkers, currentMagnets, latch );

			tasks.add( task );
		}
	}
	
	
	@Override
	public void postDeserializationInit()
	{
		// This is needed because the `ProcessMagnetsTask`'s are not saved during serialization 
		
		super.postDeserializationInit();
		
		tasks = new ArrayList<>();
		for ( List<Integer> indexes : getIndexesForEachThread( currentWalkers.size(), numThreads ) )
		{
			ProcessMagnetsTask task = new ProcessMagnetsTask( indexes, currentWalkers, currentMagnets, latch );

			tasks.add( task );
		}
	}

	// Walkers, but not magnets are altered
	// so each thread should get a subset of the walkers to work on
	@Override
	protected void processMagets()
	{
		try 
		{
			maybeResetWalkerIndexesToProcess();

			lastNumWalkers = currentWalkers.size();

			for ( ProcessMagnetsTask task : tasks )
			{
				executorService.submit( task );
			}

			while ( latch.getCount() != 0 )
			{
				Thread.yield();
			}

			latch.reset( numThreads );			
		}
		catch (Exception e) {
			System.out.println( "fuck me!!" + e );
			e.printStackTrace();
			System.exit( 1 );
		}

	}

	private void maybeResetWalkerIndexesToProcess()
	{
		if ( currentWalkers.size() != lastNumWalkers )
		{
			List<List<Integer>> indexes = getIndexesForEachThread( currentWalkers.size(), numThreads );
			for ( int i = 0; i < indexes.size(); i++ )
			{
				tasks.get( i ).setWakerIndexesToProcess( indexes.get( i ) );
			}
		}
	}

	protected List<List<Integer>> getIndexesForEachThread( int listSize, int numThreads )
	{
		List<List<Integer>> indexes = new ArrayList<List<Integer>>();
		for ( int i = 0; i < numThreads; i++ )
		{
			indexes.add( new ArrayList<>() );
		}

		final int baseSublistSize = (listSize / numThreads) + 1;
		int curSublistIdx = 0;
		int curSublistSize = 0;
		for ( int i = 0; i < listSize; i++ )
		{
			indexes.get( curSublistIdx ).add( i );
			curSublistSize++;

			if ( curSublistSize == baseSublistSize )
			{
				curSublistSize = 0;
				curSublistIdx++;
			}
		}

		return indexes;
	}
	
	@Override
	public void terminateForShutdown()
	{
		super.terminateForShutdown();
		
		executorService.shutdown();
	}
}

class ProcessMagnetsTask implements Runnable
{
	List<Integer>	walkerIndexesToProcess;
	List<Walker>	walkers;
	List<Magnet>	magnets;
	EthansCountdown	latch;

	public ProcessMagnetsTask( List<Integer> walkerIndexesToProcess, List<Walker> walkers, List<Magnet> magnets,
			EthansCountdown latch )
	{
		this.walkerIndexesToProcess = walkerIndexesToProcess;
		this.walkers = walkers;
		this.magnets = magnets;
		this.latch = latch;
	}

	public void setWakerIndexesToProcess( List<Integer> idxs )
	{
		walkerIndexesToProcess = idxs;
	}

	@Override
	public void run()
	{
		for ( Integer i : walkerIndexesToProcess )
		{
			Walker curWalker = walkers.get( i );

			for ( Magnet magnet : magnets )
			{
				double dist = curWalker.distance( magnet );

				if ( dist == 0 )
					continue;

				double distX = curWalker.x - magnet.x; // assuming repel
				double distY = curWalker.y - magnet.y;

				if ( magnet.getIsAttractive() )
				{
					distX = -distX;
					distY = -distY;
				} // todo i think effect hast o be abs val

				double effectX = (distX / dist) * (magnet.getK() / (dist * dist));
				double effectY = (distY / dist) * (magnet.getK() / (dist * dist));

				Point horizontalEffect = new Point( (int) Math.signum( effectX ), 0 );
				Point verticalEffect = new Point( 0, (int) Math.signum( effectY ) );

				effectX = Math.abs( effectX );
				effectY = Math.abs( effectY );

				if ( curWalker.getPossibleNextMoveWeights().containsKey( horizontalEffect ) )
				{
					double curWeight = curWalker.getPossibleNextMoveWeights().get( horizontalEffect );
					curWalker.getPossibleNextMoveWeights().put( horizontalEffect, curWeight + effectX );
				}

				if ( curWalker.getPossibleNextMoveWeights().containsKey( verticalEffect ) )
				{
					double curWeight = curWalker.getPossibleNextMoveWeights().get( verticalEffect );
					curWalker.getPossibleNextMoveWeights().put( verticalEffect, curWeight + effectY );
				}
			}
		}

		latch.countDown();
	}

	
}
