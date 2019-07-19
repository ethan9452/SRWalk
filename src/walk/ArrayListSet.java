package walk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;

public class ArrayListSet<E> extends ArrayList<E> implements Set<E>, List<E>
{
	public ArrayListSet( List<E> stuff )
	{
		super( stuff );
	}

	@Override
	public boolean add( E e )
	{
		if ( !contains( e ) )
		{
			return super.add( e );
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean addAll( Collection<? extends E> c )
	{
		boolean listChanged = false;

		for ( E obj : c )
		{
			if ( !contains( obj ) )
			{
				add( obj );
				listChanged = true;
			}
		}

		return listChanged;
	}

	@Override
	public void add( int index, E element )
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException( "dont use this!" );
	}

	@Override
	public boolean addAll( int index, Collection<? extends E> c )
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException( "dont use this!" );
	}

}
