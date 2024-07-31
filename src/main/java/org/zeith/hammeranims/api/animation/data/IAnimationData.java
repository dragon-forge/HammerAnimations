package org.zeith.hammeranims.api.animation.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedParticleEffect;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedSoundEffect;

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
		
		@Override
		public Int2ObjectMap<List<AnimatedSoundEffect>> getSoundEffects()
		{
			return Int2ObjectMaps.emptyMap();
		}
		
		@Override
		public Int2ObjectMap<List<AnimatedParticleEffect>> getParticleEffects()
		{
			return Int2ObjectMaps.emptyMap();
		}
		
		@Override
		public float getWeight()
		{
			return 1F;
		}
	};
	
	LoopMode getLoopMode();
	
	Duration getLength();
	
	Map<String, BoneAnimation> getBoneAnimations();
	
	Int2ObjectMap<List<AnimatedSoundEffect>> getSoundEffects();
	
	Int2ObjectMap<List<AnimatedParticleEffect>> getParticleEffects();
	
	default double getLengthSeconds()
	{
		return getLength().toMillis() / 1000D;
	}
	
	float getWeight();
}