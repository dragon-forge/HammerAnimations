package org.zeith.hammeranims.api.animation;

import shaded.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.data.IReadAnimationHolder;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.utils.IHammerReloadable;
import org.zeith.hammeranims.core.impl.api.animation.AnimationContainerImpl;

/**
 * Represents a container for animations in Hammer Models.
 * <p>
 * Animation containers may be registered in a simple way:
 * - Annotate your class with all animations with
 * - Declare static final IAnimationContainer constants, annotate each one with
 * - Assign all these constants with {@link IAnimationContainer#create()} values.
 * - Optionally perform static import for {@link IAnimationContainer#create()}.
 */
public interface IAnimationContainer
		extends IAnimationSource, IHammerReloadable
{
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
	ResourceLocation getRegistryKey();
	
	void setRegistryKey(ResourceLocation id);
	
	default AnimationHolder holder(String sub)
	{
		return new AnimationHolder(this, sub);
	}
	
	@NotNull
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