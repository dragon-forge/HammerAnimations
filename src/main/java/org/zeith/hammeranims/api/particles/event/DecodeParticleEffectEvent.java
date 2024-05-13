package org.zeith.hammeranims.api.particles.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.ParticleEffect;
import org.zeith.hammeranims.api.utils.IResourceProvider;

@Cancelable
public class DecodeParticleEffectEvent
		extends Event
{
	public final ResourceLocation path;
	public final IResourceProvider resources;
	public final IParticleContainer container;
	
	public final String json;
	
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
			super.setCanceled(true);
		} catch(UnsupportedOperationException e)
		{
		}
	}
	
	public ParticleEffect getDecoded()
	{
		return decoded;
	}
	
	@Override
	public void setCanceled(boolean cancel)
	{
	}
}