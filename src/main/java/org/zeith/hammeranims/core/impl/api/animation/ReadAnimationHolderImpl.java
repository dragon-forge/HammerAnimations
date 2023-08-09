package org.zeith.hammeranims.core.impl.api.animation;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.Animation;
import org.zeith.hammeranims.api.animation.data.IReadAnimationHolder;

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
			HammerAnimations.LOG.warn("Failed to decode animation " + key + " for " + key);
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