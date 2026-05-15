package org.zeith.hammeranims.api.particles;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonNull;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.particles.components.*;
import org.zeith.hammeranims.api.particles.curve.ParticleCurve;
import org.zeith.hammeranims.api.particles.event.CreateParticleEffectEvent;
import org.zeith.hammeranims.core.init.ParticleComponentsHA;

import java.util.*;

public class ParticleEffect
{
	public final IParticleContainer container;
	
	public final ParticleMaterial material;
	public final ResourceLocation texture;
	
	public final List<ParticleCurve> curves;
	
	public final Map<IParticleComponentType, IParticleComponent> components;
	
	public ParticleEffect(IParticleContainer container, ParticleMaterial material, ResourceLocation texture, List<ParticleCurve> curves, ImmutableMap.Builder<IParticleComponentType, IParticleComponent> components)
	{
		this.container = container;
		this.material = material;
		this.texture = texture;
		this.curves = curves;
		this.components = components.build();
	}
	
	public static ParticleEffect empty(IParticleContainer container)
	{
		return builder().build(container);
	}
	
	public static Builder builder()
	{
		return new Builder();
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