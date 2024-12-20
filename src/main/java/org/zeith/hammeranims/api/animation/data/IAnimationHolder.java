package org.zeith.hammeranims.api.animation.data;

import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.api.animation.Animation;

import java.util.*;

public interface IAnimationHolder
{
	Set<String> getKeySet();
	
	@Nullable
	Animation get(String key);
	
	Set<Map.Entry<String, Animation>> entrySet();
	
	Collection<Animation> values();
}