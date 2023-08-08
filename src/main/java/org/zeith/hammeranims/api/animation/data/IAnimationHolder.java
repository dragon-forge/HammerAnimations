package org.zeith.hammeranims.api.animation.data;

import org.zeith.hammeranims.api.animation.Animation;

import javax.annotation.Nullable;
import java.util.*;

public interface IAnimationHolder
{
	Set<String> getKeySet();
	
	@Nullable
	Animation get(String key);
	
	Set<Map.Entry<String, Animation>> entrySet();
	
	Collection<Animation> values();
}