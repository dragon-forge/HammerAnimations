package org.zeith.hammeranims.core.impl.api.particles;

import com.google.common.collect.ImmutableMap;
import com.zeitheron.hammercore.lib.zlib.json.*;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.ParticleEffect;
import org.zeith.hammeranims.api.utils.IExtendedResourceProvider;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExtraParticleEffects
{
	private final Function<ResourceLocation, IParticleContainer> loader;
	private final Map<ResourceLocation, IParticleContainer> extraEffects;
	
	public ExtraParticleEffects(Function<ResourceLocation, IParticleContainer> loader, Map<ResourceLocation, IParticleContainer> extraEffects)
	{
		this.loader = loader;
		this.extraEffects = Collections.synchronizedMap(new HashMap<>(extraEffects));
	}
	
	public Set<ResourceLocation> getKeys()
	{
		return extraEffects.keySet();
	}
	
	public IParticleContainer resolve(ResourceLocation id)
	{
		return extraEffects.computeIfAbsent(id, loader);
	}
	
	public static CompletableFuture<ExtraParticleEffects> load(IExtendedResourceProvider resources, Executor exe)
	{
		Set<ResourceLocation> toLoad = new HashSet<>();
		for(String extras : resources.readAllAsString(new ResourceLocation(HammerAnimations.MOD_ID, "bedrock/custom_particles.json")))
		{
			JSONObject res = Cast.cast(new JSONTokener(extras).nextValue(), JSONObject.class);
			if(res == null)
			{
				HammerAnimations.LOG.warn("Found invalid custom_particles.json");
				continue;
			}
			
			JSONArray load = res.optJSONArray("load");
			if(load == null) continue;
			
			for(int i = 0; i < load.length(); i++)
				toLoad.add(InstanceHelpers.tryParseLocation(load.getString(i)));
		}
		
		HammerAnimations.LOG.info("Loading {} custom particle effects.", toLoad.size());
		
		List<CompletableFuture<UnregisteredParticleContainer>> containers = toLoad.stream()
				.map(id -> CompletableFuture.supplyAsync(() -> createUnregistered(resources, id), exe))
				.collect(Collectors.toList());
		
		return CompletableFuture.allOf(containers.toArray(new CompletableFuture[0])).thenApply(__ ->
		{
			ImmutableMap.Builder<ResourceLocation, IParticleContainer> extras = ImmutableMap.builder();
			for(CompletableFuture<UnregisteredParticleContainer> c : containers)
			{
				UnregisteredParticleContainer upc = c.join();
				extras.put(upc.getRegistryKey(), upc);
			}
			return new ExtraParticleEffects(id -> createUnregistered(resources, id), extras.build());
		});
	}
	
	public static UnregisteredParticleContainer createUnregistered(IExtendedResourceProvider resources, ResourceLocation id)
	{
		UnregisteredParticleContainer ctr = new UnregisteredParticleContainer(id);
		
		ResourceLocation path = new ResourceLocation(id.getNamespace(),
				"bedrock/particles/" + id.getPath() + ".particle.json"
		);
		
		ctr.effect = Optional.ofNullable(ParticleContainerImpl.defaultReadParticle(path, resources, ctr, resources.readAsString(path)).orElseGet(() ->
		{
			HammerAnimations.LOG.warn("Unable to load custom particle effect {} from file {}", id, path);
			return null;
		})).orElseGet(() -> ParticleEffect.empty(ctr));
		
		if(HammerAnimationsApi.LOG_RELOADS)
			HammerAnimations.LOG.debug("Loaded custom particle effect {}", id);
		
		return ctr;
	}
	
	public static class UnregisteredParticleContainer
			implements IParticleContainer
	{
		protected final ResourceLocation id;
		protected ParticleEffect effect;
		
		public UnregisteredParticleContainer(ResourceLocation id)
		{
			this.id = id;
		}
		
		@Override
		public boolean isDynamic()
		{
			return true;
		}
		
		@Override
		public ParticleEffect getParticleEffect()
		{
			return effect;
		}
		
		@Override
		public ResourceLocation getRegistryKey()
		{
			return id;
		}
		
		@Override
		public IParticleContainer setRegistryName(ResourceLocation name)
		{
			return this;
		}
		
		@Nullable
		@Override
		public ResourceLocation getRegistryName()
		{
			return id;
		}
		
		@Override
		public Class<IParticleContainer> getRegistryType()
		{
			return IParticleContainer.class;
		}
		
		@Override
		public void reload(IResourceProvider provider)
		{
		}
	}
}