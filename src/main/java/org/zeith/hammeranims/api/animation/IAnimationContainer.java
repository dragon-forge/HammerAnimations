package org.zeith.hammeranims.api.animation;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.data.IReadAnimationHolder;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.animation.AnimationContainerImpl;

import javax.annotation.Nonnull;

/**
 * Represents a container for animations in Hammer Models.
 * <p>
 * Animation containers may be registered in a simple way:
 * - Annotate your class with all animations with @{@link org.zeith.hammerlib.annotations.SimplyRegister}
 * - Declare static final IAnimationContainer constants, annotate each one with @{@link org.zeith.hammerlib.annotations.RegistryName}
 * - Assign all these constants with {@link IAnimationContainer#create()} values.
 * - Optionally perform static import for {@link IAnimationContainer#create()}.
 */
public interface IAnimationContainer
	extends IAnimationSource, IForgeRegistryEntry<IAnimationContainer>
{
	/**
	 * Reloads the animation container using the provided resource provider.
	 * This is an internal function and should never be called externally.
	 * Implementing it is fine.
	 *
	 * @param resources
	 * 		The resource provider used to reload the animations.
	 */
	void reload(IResourceProvider resources);
	
	/**
	 * Creates a new instance of an animation container.
	 *
	 * @return A new instance of {@link IAnimationContainer}.
	 */
	static IAnimationContainer create()
	{
		return new AnimationContainerImpl();
	}
	
	/**
	 * Creates a new instance of an animation container.
	 *
	 * @return A new instance of {@link IAnimationContainer}.
	 */
	static IAnimationContainer createNoSuffix()
	{
		return new AnimationContainerImpl(".json");
	}
	
	/**
	 * Retrieves the read-only animation holder associated with this container.
	 *
	 * @return The {@link IReadAnimationHolder} containing animations.
	 */
	IReadAnimationHolder getAnimations();
	
	/**
	 * Gets the registry key associated with this animation container.
	 * This key can be used to identify and retrieve the container from registry.
	 *
	 * @return The {@link ResourceLocation} registry key.
	 */
	default ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.animations().getKey(this);
	}
	
	default AnimationHolder holder(String sub)
	{
		return new AnimationHolder(this, sub);
	}
	
	@Nonnull
	AnimationHolder holder();
	
	@Override
	default AnimationLocation getLocation()
	{
		return holder().getLocation();
	}
	
	@Override
	default ConfiguredAnimation configure()
	{
		return holder().get().configure();
	}
}