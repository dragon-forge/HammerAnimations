package shaded.tuples.functions;

import java.util.function.*;

import shaded.tuples.consumers.Consumer3;

@FunctionalInterface
public interface Function3<A, B, C, RES>
{
	RES apply(A a, B b, C c);
	
	default <RES_MAPPED> Function3<A, B, C, RES_MAPPED> map(Function<RES, RES_MAPPED> mapper)
	{
		return (a, b, c) -> mapper.apply(apply(a, b, c));
	}
	
	default Consumer3<A, B, C> ignoreResult()
	{
		return this::apply;
	}
}