package org.zeith.hammeranims.api.particles;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.IParticleComponentType;
import org.zeith.hammeranims.api.particles.components.itf.*;
import org.zeith.hammeranims.api.particles.curve.ParticleCurve;
import org.zeith.hammeranims.api.particles.event.CreateParticleEffectEvent;
import org.zeith.hammeranims.api.utils.InstanceGatherer;
import org.zeith.hammeranims.core.init.ParticleComponentsHA;

import java.util.*;

import static org.zeith.hammeranims.api.utils.InstanceGatherer.getComponents;

public class ParticleEffect
{
	public final IParticleContainer container;
	
	public final ParticleMaterial material;
	public final ResourceLocation texture;
	
	public final List<ParticleCurve> curves;
	
	public final Map<IParticleComponentType, IParticleComponent> components;
	public final List<IEmitterInitialize> emitterInitializes;
	public final List<IEmitterUpdate> emitterUpdates;
	public final List<IParticleInitialize> particleInitializes;
	public final List<IParticleUpdate> particleUpdates;
	public final List<IParticleRender> particleRender;
	public final List<IParticlePreRender> particlePreRender;
	public final List<IParticlePostRender> particlePostRender;
	public final List<IParticleExpiry> particleExpiry;
	
	public ParticleEffect(IParticleContainer container, ParticleMaterial material, ResourceLocation texture, List<ParticleCurve> curves, ImmutableMap.Builder<IParticleComponentType, IParticleComponent> components)
	{
		this.container = container;
		this.material = material;
		this.texture = texture;
		this.curves = curves;
		
		this.components = components.build();
		
		Collection<IParticleComponent> coms = this.components.values();
		this.emitterInitializes = getComponents(coms, IEmitterInitialize.class);
		this.emitterUpdates = getComponents(coms, IEmitterUpdate.class);
		this.particleInitializes = getComponents(coms, IParticleInitialize.class);
		this.particleUpdates = getComponents(coms, IParticleUpdate.class);
		this.particleRender = getComponents(coms, IParticleRender.class);
		this.particlePreRender = getComponents(coms, IParticlePreRender.class);
		this.particlePostRender = getComponents(coms, IParticlePostRender.class);
		this.particleExpiry = getComponents(coms, IParticleExpiry.class);
	}
	
	public static ParticleEffect empty(IParticleContainer container)
	{
		return builder().build(container);
	}
	
	public static Builder builder()
	{
		return new Builder();
	}
	
	public <T extends IParticleComponent> T get(Class<T> base, IParticleComponentType type)
	{
		return Cast.cast(components.get(type), base);
	}
	
	public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
	public static class Builder
	{
		protected final Map<IParticleComponentType, IParticleComponent> components = new HashMap<>();
		protected final List<ParticleCurve> curves = new ArrayList<>();
		protected ParticleMaterial material = ParticleMaterial.OPAQUE;
		protected ResourceLocation texture = LOCATION_BLOCKS_TEXTURE;
		
		public Builder material(ParticleMaterial material)
		{
			this.material = material;
			return this;
		}
		
		public Builder texture(ResourceLocation texture)
		{
			this.texture = texture;
			return this;
		}
		
		public Builder curve(ParticleCurve curve)
		{
			curves.add(curve);
			return this;
		}
		
		public Builder component(IParticleComponentType type, IParticleComponent component)
		{
			components.put(type, component);
			return this;
		}
		
		public ParticleEffect build(IParticleContainer container)
		{
			if(!components.containsKey(ParticleComponentsHA.PARTICLE_INITIAL_SPEED))
				component(ParticleComponentsHA.PARTICLE_INITIAL_SPEED, ParticleComponentsHA.PARTICLE_INITIAL_SPEED.fromJson(JsonNull.INSTANCE));
			
			//<editor-fold desc="Transmute this effect through event">
			HammerAnimationsApi.EVENT_BUS.post(new CreateParticleEffectEvent(container, components, this));
			ImmutableMap.Builder<IParticleComponentType, IParticleComponent> components =
					ImmutableMap.<IParticleComponentType, IParticleComponent>builder()
							.putAll(this.components);
			//</editor-fold>
			
			return new ParticleEffect(
					container,
					material, texture,
					Collections.unmodifiableList(curves),
					components
			);
		}
	}
}