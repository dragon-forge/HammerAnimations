package org.zeith.hammeranims.api.particles.event;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.ParticleEffect;
import org.zeith.hammeranims.api.utils.IResourceProvider;

public class DecodeParticleEffectEvent
		extends Event
		implements ICancellableEvent
{
	public final ResourceLocation path;
	public final IResourceProvider resources;
	public final IParticleContainer container;
	
	public final String json;
	
	@Getter
	protected ParticleEffect decoded;
	
	public DecodeParticleEffectEvent(ResourceLocation path, IResourceProvider resources, IParticleContainer container, String json)
	{
		this.path = path;
		this.resources = resources;
		this.container = container;
		this.json = json;
	}
	
	public void setDecoded(ParticleEffect decoded)
	{
		this.decoded = decoded;
		try
		{
			ICancellableEvent.super.setCanceled(true);
		} catch(UnsupportedOperationException e)
		{
		}
	}
	
	@Override
	public void setCanceled(boolean cancel)
	{
	}
}