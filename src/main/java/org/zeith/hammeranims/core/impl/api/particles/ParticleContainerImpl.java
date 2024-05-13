package org.zeith.hammeranims.core.impl.api.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.ParticleEffect;
import org.zeith.hammeranims.api.particles.event.DecodeParticleEffectEvent;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.Optional;

public class ParticleContainerImpl
		extends ForgeRegistryEntry<IParticleContainer>
		implements IParticleContainer
{
	public final String suffix;
	protected ParticleEffect effect = ParticleEffect.empty(this);
	
	public ParticleContainerImpl(String suffix)
	{
		this.suffix = suffix;
	}
	
	public ParticleContainerImpl()
	{
		this.suffix = ".particle.json";
	}
	
	@Override
	public boolean isDynamic()
	{
		return false;
	}
	
	@Override
	public ParticleEffect getParticleEffect()
	{
		return effect;
	}
	
	public static Optional<ParticleEffect> defaultReadParticle(ResourceLocation path, IResourceProvider resources, IParticleContainer container, Optional<String> text)
	{
		return text.map(txt ->
		{
			ResourceLocation key = container.getRegistryKey();
			
			try
			{
				DecodeParticleEffectEvent evt = new DecodeParticleEffectEvent(path, resources, container, txt);
				HammerAnimationsApi.EVENT_BUS.post(evt);
				
				return evt.getDecoded();
			} catch(Exception e)
			{
				HammerAnimations.LOG.error("Failed to load geometry " + key + ", skipping.", e);
				return null;
			}
		});
	}
	
	@Override
	public void reload(IResourceProvider resources)
	{
		ResourceLocation key = getRegistryKey();
		
		ResourceLocation path = new ResourceLocation(key.getNamespace(),
				"bedrock/particles/" + key.getPath() + suffix
		);
		
		effect = Optional.ofNullable(defaultReadParticle(path, resources, this, resources.readAsString(path)).orElseGet(() ->
		{
			HammerAnimations.LOG.warn("Unable to load particle effect {} from file {}", key, path);
			return null;
		})).orElseGet(() -> ParticleEffect.empty(this));
		
		if(HammerAnimationsApi.LOG_RELOADS)
			HammerAnimations.LOG.debug("Loaded {} particles container.", key);
	}
}