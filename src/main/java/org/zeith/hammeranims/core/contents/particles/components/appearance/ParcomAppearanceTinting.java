package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.zeith.lzvm.LzVariableStore;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.*;
import org.zeith.hammeranims.api.particles.components.itf.IParticleRender;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomAppearanceTinting
		implements IParticleComponent
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
	public int getSortingIndex()
	{
		return -10;
	}
	
	@Override
	public ParcomAppearanceTintingInstance createInstance(LzVariableStore vars)
	{
		return new ParcomAppearanceTintingInstance(color.newInstance(vars));
	}
	
	public static class ParcomAppearanceTintingInstance
			implements IParticleRender
	{
		public final TintInstance color;
		
		public ParcomAppearanceTintingInstance(TintInstance color)
		{
			this.color = color;
		}
		
		protected void setColor(BedrockParticle particle)
		{
			if(this.color != null)
			{
				this.color.compute(particle);
			} else
			{
				particle.r = particle.g = particle.b = particle.a = 1;
			}
		}
		
		@Override
		public void render(ParticleEmitter emitter, BedrockParticle particle, VertexConsumer builder, PoseStack pose, float partialTicks)
		{
			setColor(particle);
		}
		
		@Override
		public void renderOnScreen(BedrockParticle particle, VertexConsumer builder, PoseStack pose, int x, int y, float scale, float partialTicks)
		{
			setColor(particle);
		}
	}
}