package org.zeith.hammeranims.core.impl.api.animation;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IReadAnimationHolder;
import org.zeith.hammeranims.api.animation.event.*;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammerlib.util.shaded.json.*;

import javax.annotation.Nonnull;
import java.util.*;

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
		@Nonnull
		@Override
		public Animation get()
		{
			Animation animation = defaultAnimation;
			if(animation == null)
			{
				if(this == DefaultsHA.NULL_ANIM)
				{
					HammerAnimations.LOG.warn("Unable to find default null animation. This is not supposed to happen!");
					return null;
				}
				return DefaultsHA.NULL_ANIM.get();
			}
			return animation;
		}
	};
	
	public static Optional<IReadAnimationHolder> defaultReadAnimation(IResourceProvider resources, IAnimationContainer container, Optional<String> text)
	{
		return text.map(JSONTokener::new)
				.flatMap(JSONTokener::nextValueOBJ)
				.map(json ->
				{
					ResourceLocation key = container.getRegistryKey();
					
					try
					{
						ReadAnimationHolderImpl holder = new ReadAnimationHolderImpl(key);
						
						var animations = json.getJSONObject("animations");
						String fmt = json.getString("format_version");
						
						for(String animKey : animations.keySet())
						{
							DecodeAnimationEvent evt = new DecodeAnimationEvent(resources, container, json, fmt, animKey, animations.get(animKey));
//							AnimationDecoder.decodeAnimation(evt);
							if(!evt.isCanceled()) HammerAnimationsApi.EVENT_BUS.post(evt);
							holder.put(animKey, evt.getDecoded());
						}
						
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
		
		AnimationContainerParseEvent event = new AnimationContainerParseEvent(resources, path, this);
		HammerAnimationsApi.EVENT_BUS.post(event);
		
		animations = Optional.ofNullable(event.getOverrideOrDefault(() -> defaultReadAnimation(resources, this, resources.readAsString(path)).orElseGet(() ->
		{
			HammerAnimations.LOG.warn("Unable to load animation {} from file {}", key, path);
			return null;
		}))).orElse(IReadAnimationHolder.EMPTY);
		
		Collection<Animation> anims = animations.values();
		
		if(anims.size() == 1)
			defaultAnimation = anims.iterator().next();
		else
			defaultAnimation = DefaultsHA.NULL_ANIM.get();
		
		HammerAnimations.LOG.debug("Loaded {} animations in {}: {}", animations.getKeySet()
				.size(), key, animations.getKeySet());
	}
	
	@Override
	public IReadAnimationHolder getAnimations()
	{
		return animations;
	}
	
	@Nonnull
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