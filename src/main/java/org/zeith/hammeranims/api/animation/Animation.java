package org.zeith.hammeranims.api.animation;

import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.core.init.DefaultsHA;

public class Animation
		implements IAnimationSource
{
	protected final IAnimationContainer container;
	protected final String key;
	protected final AnimationLocation location;
	
	protected final IAnimationData data;
	
	public Animation(IAnimationContainer container, String key, IAnimationData data)
	{
		this.container = container;
		this.key = key;
		this.location = new AnimationLocation(container.getRegistryKey(), key);
		this.data = data;
	}
	
	public IAnimationData getData()
	{
		return data;
	}
	
	@Override
	public AnimationLocation getLocation()
	{
		return location;
	}
	
	@Override
	public ConfiguredAnimation configure()
	{
		return new ConfiguredAnimation(this);
	}
	
	public boolean isNull()
	{
		return this == DefaultsHA.NULL_ANIMATION_SYNTETIC;
	}
	
	@Override
	public String toString()
	{
		return "Animation{" +
				"container=" + container +
				", key='" + key + '\'' +
				", location=" + location +
				", data=" + data +
				'}';
	}
}