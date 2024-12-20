package shaded.tuples;

import java.util.stream.Stream;

public interface ITuple
{
	int arity();
	
	Stream<?> stream();
}