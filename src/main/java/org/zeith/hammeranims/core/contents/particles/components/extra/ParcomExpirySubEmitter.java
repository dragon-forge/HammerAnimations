package org.zeith.hammeranims.core.contents.particles.components.extra;

import com.google.gson.*;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.components.itf.IParticleExpiry;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammeranims.core.utils.GsonHelper;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.joml.Vector3d;

public class ParcomExpirySubEmitter
		implements IParticleExpiry
{
	@SuppressWarnings("rawtypes")
	public InterpolatedDouble[] offset = { InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero() };
	
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
	public void expire(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		
		Vector3d spawnPos = new Vector3d(particle.getGlobalPosition(emitter))
				.add(offset[0].get(v), offset[1].get(v), offset[2].get(v));
		
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				emitter.world,
				spawnPos.x, spawnPos.y, spawnPos.z,
				IParticleContainer.byRegistryKey(subId)
		);
		pwe.getEmitter().setParent(emitter);
		pwe.spawn();
	}
}