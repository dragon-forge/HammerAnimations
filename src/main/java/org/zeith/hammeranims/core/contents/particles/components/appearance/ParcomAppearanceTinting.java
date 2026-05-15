package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.itf.IParticleRender;
import org.zeith.hammeranims.api.particles.emitter.*;
import org.zeith.hammeranims.core.client.render.IVertexOutput;

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
		public void render(ParticleEmitter emitter, BedrockParticle particle, IVertexOutput builder, float partialTicks)
		{
			setColor(particle);
		}
		
		@Override
		public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks)
		{
			setColor(particle);
		}
	}
}