package org.zeith.hammeranims.api.particles;

import com.google.common.collect.ImmutableMap;
import dev.zeith.lzvm.LzVariableStore;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.particles.components.*;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.*;
import org.zeith.hammeranims.api.particles.curve.ParticleCurve;
import org.zeith.hammerlib.util.java.Cast;

import java.util.*;

import static org.zeith.hammeranims.api.utils.InstanceGatherer.getComponents;

public class ParticleEffectInstance
{
	public final ParticleEffect effect;
	
	public final ParticleMaterial material;
	public final ResourceLocation texture;
	
	public final List<ParticleCurve> curves;
	
	public final Map<IParticleComponentType, IParticleCompInstance> components;
	public final List<IEmitterInitialize> emitterInitializes;
	public final List<IEmitterUpdate> emitterUpdates;
	public final List<IParticleInitialize> particleInitializes;
	public final List<IParticleUpdate> particleUpdates;
	public final List<IParticleRender> particleRender;
	public final List<IParticlePreRender> particlePreRender;
	public final List<IParticlePostRender> particlePostRender;
	public final List<IParticleExpiry> particleExpiry;
	
	public ParticleEffectInstance(ParticleEffect effect, LzVariableStore vars)
	{
		this.effect = effect;
		this.material = effect.material;
		this.texture = effect.texture;
		this.curves = effect.curves;
		
		ImmutableMap.Builder<IParticleComponentType, IParticleCompInstance> instances = ImmutableMap.builder();
		for(Map.Entry<IParticleComponentType, IParticleComponent> com : effect.components.entrySet())
			instances.put(com.getKey(), com.getValue().createInstance(vars));
		this.components = instances.build();
		
		Collection<IParticleCompInstance> coms = this.components.values();
		this.emitterInitializes = getComponents(coms, IEmitterInitialize.class);
		this.emitterUpdates = getComponents(coms, IEmitterUpdate.class);
		this.particleInitializes = getComponents(coms, IParticleInitialize.class);
		this.particleUpdates = getComponents(coms, IParticleUpdate.class);
		this.particleRender = getComponents(coms, IParticleRender.class);
		this.particlePreRender = getComponents(coms, IParticlePreRender.class);
		this.particlePostRender = getComponents(coms, IParticlePostRender.class);
		this.particleExpiry = getComponents(coms, IParticleExpiry.class);
	}
	
	public <T extends IParticleCompInstance> T get(Class<T> base, IParticleComponentType type)
	{
		return Cast.cast(components.get(type), base);
	}
}
