package org.zeith.hammeranims.api.animation;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.animation.data.IReadAnimationHolder;
import org.zeith.hammeranims.api.HammerModelsApi;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.animation.AnimationContainerImpl;
import org.zeith.hammeranims.api.annotations.*;

/**
 * Represents a container for animations in Hammer Models.
 * <p>
 * Animation containers may be registered in a simple way:
 * - Annotate your class with all animations with @{@link RegisterAnimations}
 * - Declare static final IAnimationContainer constants, annotate each one with @{@link AnimKey}
 * - Assign all these constants with {@link IAnimationContainer#create()} values.
 * - Optionally perform static import for {@link IAnimationContainer#create()}.
 */
public interface IAnimationContainer
		extends IForgeRegistryEntry<IAnimationContainer>
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
	 * This key can be used to identify and retrieve the container from registries.
	 *
	 * @return The {@link ResourceLocation} registry key.
	 */
	default ResourceLocation getRegistryKey()
	{
		return HammerModelsApi.animations().getKey(this);
	}
}