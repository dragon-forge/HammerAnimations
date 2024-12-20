package shaded.tuples;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tuple2<A, B>
		implements ITuple
{
	protected A a;
	protected B b;
	
	public Tuple2(A a, B b)
	{
		this.a = a;
		this.b = b;
	}
	
	public A a()
	{
		return a;
	}
	
	public B b()
	{
		return b;
	}
	
	public @Override int arity()
	{
		return 2;
	}
	
	public @Override Stream<?> stream()
	{
		return Stream.of(a, b);
	}
	
	public Tuple2.Mutable2<A, B> mutable()
	{
		return new Tuple2.Mutable2<>(a, b);
	}
	
	public Tuple2<A, B> immutable()
	{
		return this;
	}
	
	public Tuple2<A, B> copy()
	{
		return new Tuple2<>(a, b);
	}
	
	public @Override String toString()
	{
		return "Tuple2" + stream().map(String::valueOf).collect(Collectors.joining(", ", "{", "}"));
	}
	
	public @Override int hashCode()
	{
		return java.util.Objects.hash(a, b);
	}
	
	public @Override
	@SuppressWarnings("rawtypes") boolean equals(Object other)
	{
		if(!(other instanceof Tuple2 tuple)) return false;
		return tuple.arity() == arity() && Tuples.streamEquals(stream(), tuple.stream());
	}
	
	public static class Mutable2<A, B>
			extends Tuple2<A, B>
	{
		public Mutable2(A a, B b)
		{
			super(a, b);
		}
		
		public @Override Tuple2.Mutable2<A, B> mutable()
		{
			return this;
		}
		
		public @Override Tuple2.Mutable2<A, B> copy()
		{
			return new Tuple2.Mutable2<>(a, b);
		}
		
		public @Override Tuple2<A, B> immutable()
		{
			return new Tuple2<>(a, b);
		}
		
		public @Override String toString()
		{
			return "Tuple2.Mutable2" + stream().map(String::valueOf).collect(Collectors.joining(", ", "{", "}"));
		}
		
		public Mutable2<A, B> setA(A a)
		{
			this.a = a;
			return this;
		}
		
		public Mutable2<A, B> setB(B b)
		{
			this.b = b;
			return this;
		}
	}
}