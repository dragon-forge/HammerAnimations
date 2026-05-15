package org.zeith.hammeranims.core.contents.particles.components.extra;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.IParticleExpiry;
import org.zeith.hammeranims.api.particles.emitter.*;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammeranims.core.utils.*;
import org.zeith.hammeranims.joml.Vector3d;

public class ParcomExpirySubEmitter
		implements IParticleComponent
{
	public LzFactory[] offset = {InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero()};
	
	protected ResourceLocation subId;
	
	public ParcomExpirySubEmitter(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(element.has("offset"))
		{
			JsonArray array = element.getAsJsonArray("offset");
			
			if(array.size() >= 3)
			{
				this.offset[0] = InterpolatedDouble.parse(array.get(0));
				this.offset[1] = InterpolatedDouble.parse(array.get(1));
				this.offset[2] = InterpolatedDouble.parse(array.get(2));
			}
		}
		
		subId = InstanceHelpers.tryParseLocation(GsonHelper.getAsString(element, "sub_emitter"));
	}
	
	@Override
	public IParticleCompInstance createInstance(LzVariableStore vars)
	{
		return new ParcomExpirySubEmitterInstance(
				LzFactory.instantiate(vars, offset),
				subId
		);
	}
	
	public static class ParcomExpirySubEmitterInstance
			implements IParticleExpiry
	{
		public final LzExpression[] offset;
		protected final ResourceLocation subId;
		
		public ParcomExpirySubEmitterInstance(LzExpression[] offset, ResourceLocation subId)
		{
			this.offset = offset;
			this.subId = subId;
		}
		
		@Override
		public void expire(ParticleEmitter emitter, BedrockParticle particle)
		{
			Vector3d spawnPos = new Vector3d(particle.getGlobalPosition(emitter))
					.add(offset[0].get(), offset[1].get(), offset[2].get());
			
			ParticleWithEmitter pwe = new ParticleWithEmitter(
					emitter.world,
					spawnPos.x, spawnPos.y, spawnPos.z,
					IParticleContainer.byRegistryKey(subId)
			);
			
			pwe.getEmitter().setParent(emitter);
			pwe.spawn();
		}
	}
}