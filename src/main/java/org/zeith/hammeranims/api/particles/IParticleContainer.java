package org.zeith.hammeranims.api.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.utils.IHammerReloadable;
import org.zeith.hammeranims.core.impl.api.particles.ExtraParticleEffects;
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
	
	boolean isDynamic();
	
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
	
	/**
	 * Retrieves a particle container that can be used to spawn particle emitter.
	 * <p>
	 * The returned container may be obtained from either forge registry, or a dynamic one provided by a resource pack.
	 */
	@Nullable
	static IParticleContainer byRegistryKey(ResourceLocation id)
	{
		ExtraParticleEffects ep = HammerAnimations.PROXY.getExtraParticles();
		if(ep != null)
		{
			IParticleContainer c = ep.resolve(id);
			if(c != null) return c;
		}
		return HammerAnimationsApi.particleContainers().getValue(id);
	}
}