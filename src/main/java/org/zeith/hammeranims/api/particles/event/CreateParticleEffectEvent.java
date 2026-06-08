package org.zeith.hammeranims.api.particles.event;

import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.ParticleEffect;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.IParticleComponentType;

import java.util.Map;
import java.util.function.Function;

@Getter
public class CreateParticleEffectEvent
		extends Event
{
	protected final IParticleContainer container;
	protected final Map<IParticleComponentType, IParticleComponent> components;
	protected final ParticleEffect.Builder builder;
	
	public CreateParticleEffectEvent(IParticleContainer container, Map<IParticleComponentType, IParticleComponent> components, ParticleEffect.Builder builder)
	{
		this.container = container;
		this.components = components;
		this.builder = builder;
	}
	
	public IParticleComponent get(IParticleComponentType type)
	{
		return components.get(type);
	}
	
	public IParticleComponent getOrCreate(IParticleComponentType type, Function<IParticleComponentType, IParticleComponent> factory)
	{
		return components.computeIfAbsent(type, factory);
	}
}