package org.zeith.hammeranims.core.impl.api.animation;

import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IReadAnimationHolder;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.init.DefaultsHA;
import shaded.json.JSONObject;
import shaded.json.JSONTokener;
import shaded.util.ResourceLocation;

import java.util.Collection;
import java.util.Optional;

public class AnimationContainerImpl
		implements IAnimationContainer
{
	protected IReadAnimationHolder animations = IReadAnimationHolder.EMPTY;
	
	public final String suffix;
	
	public AnimationContainerImpl(String suffix)
	{
		this.suffix = suffix;
	}
	
	public AnimationContainerImpl()
	{
		this.suffix = ".animation.json";
	}
	
	protected Animation defaultAnimation;
	protected final AnimationHolder onlyHolder = new AnimationHolder(this, "default")
	{
		@NotNull
		@Override
		public Animation get()
		{
			Animation animation = defaultAnimation;
			if(animation == null) return DefaultsHA.NULL_ANIMATION_SYNTETIC;
			return animation;
		}
		
		@Override
		public AnimationLocation getLocation()
		{
			return get().getLocation();
		}
	};
	
	public static Optional<IReadAnimationHolder> defaultReadAnimation(IAnimationContainer container, Optional<String> text)
	{
		return text.map(JSONTokener::new)
				.map(v -> (JSONObject) v.nextValue())
				.map(json ->
				{
					ResourceLocation key = container.getRegistryKey();
					
					try
					{
						ReadAnimationHolderImpl holder = new ReadAnimationHolderImpl(key);
						
						JSONObject animations = json.getJSONObject("animations");
						String fmt = json.getString("format_version");
						
						for(String animKey : animations.keySet())
							holder.put(animKey, AnimationDecoder.decodeAnimation(animations.getJSONObject(animKey), container, animKey, fmt));
						
						return holder;
					} catch(Exception e)
					{
						HammerAnimations.LOG.error("Failed to load animation " + key + ", skipping.", e);
						return null;
					}
				});
	}
	
	@Override
	public void reload(IResourceProvider resources)
	{
		ResourceLocation key = getRegistryKey();
		
		ResourceLocation path = new ResourceLocation(key.getNamespace(),
				"bedrock/animations/" + key.getPath() + suffix
		);
		
		animations = Optional.ofNullable(defaultReadAnimation(this, resources.readAsString(path)).orElseGet(() ->
		{
			HammerAnimations.LOG.warn("Unable to load animation {} from file {}", key, path);
			return null;
		})).orElse(IReadAnimationHolder.EMPTY);
		
		Collection<Animation> anims = animations.values();
		
		if(anims.size() == 1)
			defaultAnimation = anims.iterator().next();
		else
			defaultAnimation = DefaultsHA.NULL_ANIMATION_SYNTETIC;
		
		if(HammerAnimationsApi.LOG_RELOADS)
			HammerAnimations.LOG.debug("Loaded {} animations in {}: {}", animations.getKeySet()
					.size(), key, animations.getKeySet()
			);
	}
	
	@Override
	public IReadAnimationHolder getAnimations()
	{
		return animations;
	}
	
	ResourceLocation id;
	
	@Override
	public ResourceLocation getRegistryKey()
	{
		return id;
	}
	
	@Override
	public void setRegistryKey(ResourceLocation id)
	{
		this.id = id;
	}
	
	@NotNull
	@Override
	public AnimationHolder holder()
	{
		return onlyHolder;
	}
	
	@Override
	public String toString()
	{
		return IAnimationContainer.class.getSimpleName() + "{" + getRegistryKey() + "}";
	}
}