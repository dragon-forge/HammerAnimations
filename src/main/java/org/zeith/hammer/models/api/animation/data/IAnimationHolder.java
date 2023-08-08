package org.zeith.hammer.models.api.animation.data;

import org.zeith.hammer.models.api.animation.Animation;

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