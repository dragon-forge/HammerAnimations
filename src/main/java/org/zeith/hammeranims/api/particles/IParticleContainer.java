package org.zeith.hammeranims.api.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.utils.IHammerReloadable;
import org.zeith.hammeranims.core.impl.api.particles.ParticleContainerImpl;

public interface IParticleContainer
		extends IHammerReloadable, IForgeRegistryEntry<IParticleContainer>
{
	/**
	 * Gets the registry key associated with this particle container.
	 * This key can be used to identify and retrieve the container from registry.
	 *
	 * @return The {@link ResourceLocation} registry key.
	 */
	default ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.particleContainers().getKey(this);
	}
	
	ParticleEffect getParticleEffect();
	
	/**
	 * Creates a new instance of a particle container.
	 *
	 * @return A new instance of {@link IParticleContainer}.
	 */
	static IParticleContainer create()
	{
		return new ParticleContainerImpl();
	}
	
	/**
	 * Creates a new instance of a particle container.
	 *
	 * @return A new instance of {@link IParticleContainer}.
	 */
	static IParticleContainer createNoSuffix()
	{
		return new ParticleContainerImpl(".json");
	}
}