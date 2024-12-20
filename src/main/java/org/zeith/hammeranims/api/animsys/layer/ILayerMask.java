package org.zeith.hammeranims.api.animsys.layer;

import java.util.*;
import java.util.function.Predicate;

@FunctionalInterface
public interface ILayerMask
		extends Predicate<String>
{
	ILayerMask TRUE = bone -> true;
	ILayerMask FALSE = bone -> false;
	
	@Override
	boolean test(String bone);
	
	@Override
	default ILayerMask negate()
	{
		ILayerMask th = this;
		return bone -> !th.test(bone);
	}
	
	default ILayerMask not()
	{
		ILayerMask th = this;
		return bone -> !th.test(bone);
	}
	
	@Override
	default ILayerMask or(Predicate<? super String> other)
	{
		ILayerMask th = this;
		return bone -> th.test(bone) || other.test(bone);
	}
	
	default ILayerMask nor(Predicate<? super String> other)
	{
		ILayerMask th = this;
		return bone -> !(th.test(bone) || other.test(bone));
	}
	
	default ILayerMask and(Predicate<? super String> other)
	{
		ILayerMask th = this;
		return bone -> th.test(bone) && other.test(bone);
	}
	
	default ILayerMask nand(Predicate<? super String> other)
	{
		ILayerMask th = this;
		return bone -> !(th.test(bone) && other.test(bone));
	}
	
	static ILayerMask anyOf(String... bones)
	{
		List<String> boneList = Arrays.asList(bones);
		return boneList::contains;
	}
	
	static ILayerMask noneOf(String... bones)
	{
		return anyOf(bones).negate();
	}
}