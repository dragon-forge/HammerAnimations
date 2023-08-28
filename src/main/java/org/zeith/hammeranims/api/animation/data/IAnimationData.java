package org.zeith.hammeranims.api.animation.data;

import org.zeith.hammeranims.api.animation.LoopMode;

import java.time.Duration;
import java.util.*;

public interface IAnimationData
{
	IAnimationData EMPTY = new IAnimationData()
	{
		@Override
		public LoopMode getLoopMode()
		{
			return LoopMode.ONCE;
		}
		
		@Override
		public Duration getLength()
		{
			return Duration.ZERO;
		}
		
		@Override
		public Map<String, BoneAnimation> getBoneAnimations()
		{
			return Collections.emptyMap();
		}
	};
	
	LoopMode getLoopMode();
	
	Duration getLength();
	
	Map<String, BoneAnimation> getBoneAnimations();
	
	default double getLengthSeconds()
	{
		return getLength().toMillis() / 1000D;
	}
}