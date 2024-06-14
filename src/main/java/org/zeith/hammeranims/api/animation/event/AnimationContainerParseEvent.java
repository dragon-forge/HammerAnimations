package org.zeith.hammeranims.api.animation.event;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animation.data.IReadAnimationHolder;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.function.Supplier;

public class AnimationContainerParseEvent
		extends Event
		implements ICancellableEvent
{
	public final IResourceProvider provider;
	public final ResourceLocation path;
	public final IAnimationContainer container;
	
	protected IReadAnimationHolder holder;
	
	public AnimationContainerParseEvent(IResourceProvider provider, ResourceLocation path, IAnimationContainer container)
	{
		this.provider = provider;
		this.path = path;
		this.container = container;
	}
	
	public void cancelWithOverride(IReadAnimationHolder holder)
	{
		this.holder = holder;
		ICancellableEvent.super.setCanceled(true);
	}
	
	public IReadAnimationHolder getOverrideOrDefault(Supplier<IReadAnimationHolder> def)
	{
		return holder != null && isCanceled() ? holder : def.get();
	}
	
	@Override
	public void setCanceled(boolean cancel)
	{
	}
}