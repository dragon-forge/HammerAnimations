package shaded.tuples;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public class Tuples
{
	static boolean streamEquals(Stream<?> s1, Stream<?> s2)
	{
		Iterator<?> iter1 = s1.iterator(), iter2 = s2.iterator();
		while(iter1.hasNext() && iter2.hasNext())
			if(!Objects.equals(iter1.next(), iter2.next()))
				return false;
		return !iter1.hasNext() && !iter2.hasNext();
	}
	
	public static <A, B> Tuple2<A, B> immutable(A a, B b)
	{
		return new Tuple2<>(a, b);
	}
}