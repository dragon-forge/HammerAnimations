package org.zeith.hammer.models.core.impl.api.animation;

import com.zeitheron.hammercore.lib.zlib.json.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammer.models.HammerModels;
import org.zeith.hammer.models.api.HammerModelsApi;
import org.zeith.hammer.models.api.animation.IAnimationContainer;
import org.zeith.hammer.models.api.animation.data.IReadAnimationHolder;
import org.zeith.hammer.models.api.animation.event.*;
import org.zeith.hammer.models.api.utils.IResourceProvider;

import java.util.Optional;

public class AnimationContainerImpl
		extends IForgeRegistryEntry.Impl<IAnimationContainer>
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
	
	public static Optional<IReadAnimationHolder> defaultReadAnimation(IAnimationContainer container, Optional<String> text)
	{
		return text.map(JSONTokener::new)
				.flatMap(JSONTokener::nextValueOBJ)
				.map(json ->
				{
					ResourceLocation key = container.getRegistryKey();
					
					try
					{
						ReadAnimationHolderImpl holder = new ReadAnimationHolderImpl(key);
						
						JSONObject animations = json.getJSONObject("animations");
						String fmt = json.getString("format_version");
						
						for(String animKey : animations.keySet())
						{
							DecodeAnimationEvent evt = new DecodeAnimationEvent(container, json, fmt, animKey, animations.get(animKey));
							AnimationDecoder.decodeAnimation(evt);
							if(!evt.isCanceled()) HammerModelsApi.EVENT_BUS.post(evt);
							holder.put(animKey, evt.getDecoded());
						}
						
						return holder;
					} catch(Exception e)
					{
						HammerModels.LOG.error("Failed to load model " + key + ", skipping.", e);
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
		HammerModelsApi.EVENT_BUS.post(event);
		
		animations = Optional.ofNullable(event.getOverrideOrDefault(() -> defaultReadAnimation(this, resources.readAsString(path)).orElseGet(() ->
		{
			HammerModels.LOG.warn("Unable to load animation {} from file {}", key, path);
			return null;
		}))).orElse(IReadAnimationHolder.EMPTY);
		
		HammerModels.LOG.debug("Loaded {} animations in {}: {}", animations.getKeySet()
				.size(), key, animations.getKeySet());
	}
	
	@Override
	public IReadAnimationHolder getAnimations()
	{
		return animations;
	}
	
	@Override
	public String toString()
	{
		return IAnimationContainer.class.getSimpleName() + "{" + getRegistryKey() + "}";
	}
}