package org.zeith.hammeranims.core.impl.api.particles.components.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.zeith.hammeranims.api.particles.components.itf.IParticleRender;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomAppearanceTinting
		implements IParticleRender
{
	public Tint color = new Tint.Solid();
	
	public ParcomAppearanceTinting(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(element.has("color"))
		{
			JsonElement color = element.get("color");
			
			if(color.isJsonArray() || color.isJsonPrimitive())
			{
				this.color = Tint.parseColor(color);
			} else if(color.isJsonObject())
			{
				this.color = Tint.parseGradient(color.getAsJsonObject());
			}
		}
	}
	
	@Override
	public void render(ParticleVariables vars, ParticleEmitter emitter, BedrockParticle particle, VertexConsumer builder, PoseStack pose, float partialTicks)
	{
		this.renderOnScreen(vars, particle, builder, pose, 0, 0, 0, 0);
	}
	
	@Override
	public void renderOnScreen(ParticleVariables vars, BedrockParticle particle, VertexConsumer builder, PoseStack pose, int x, int y, float scale, float partialTicks)
	{
		if(this.color != null)
		{
			this.color.compute(vars, particle);
		} else
		{
			particle.r = particle.g = particle.b = particle.a = 1;
		}
	}
	
	@Override
	public int getSortingIndex()
	{
		return -10;
	}
}