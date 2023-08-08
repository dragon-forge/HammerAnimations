package org.zeith.hammer.models.core.impl.api.animation;

import net.minecraft.util.ResourceLocation;
import org.zeith.hammer.models.HammerModels;
import org.zeith.hammer.models.api.animation.*;
import org.zeith.hammer.models.api.animation.data.IReadAnimationHolder;

import java.util.*;

public class ReadAnimationHolderImpl
		implements IReadAnimationHolder
{
	protected Map<String, Animation> animations = createAnimationMap();
	protected Map<String, Animation> animationsView = Collections.unmodifiableMap(animations);
	protected final ResourceLocation key;
	
	public ReadAnimationHolderImpl(ResourceLocation key)
	{
		this.key = key;
	}
	
	protected Map<String, Animation> createAnimationMap()
	{
		return new LinkedHashMap<>();
	}
	
	public void put(String key, Animation anim)
	{
		if(anim == null)
		{
			HammerModels.LOG.warn("Failed to decode animation " + key + " for " + key);
			return;
		}
		animations.put(key, anim);
	}
	
	@Override
	public Set<String> getKeySet()
	{
		return animationsView.keySet();
	}
	
	@Override
	public Animation get(String key)
	{
		return animations.get(key);
	}
	
	@Override
	public Set<Map.Entry<String, Animation>> entrySet()
	{
		return animationsView.entrySet();
	}
	
	@Override
	public Collection<Animation> values()
	{
		return animationsView.values();
	}
}