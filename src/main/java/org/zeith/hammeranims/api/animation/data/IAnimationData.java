package org.zeith.hammeranims.api.animation.data;

import org.zeith.hammeranims.api.animation.LoopMode;

import java.time.Duration;
import java.util.Map;

public interface IAnimationData
{
	LoopMode getLoopMode();
	
	Duration getLength();
	
	Map<String, BoneAnimation> getBoneAnimations();
}