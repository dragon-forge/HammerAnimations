package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.emitter.*;
import org.zeith.hammeranims.core.client.render.IVertexOutput;

public interface IParticleRender
		extends IParticleCompInstance
{
	default boolean supportsCollissionRendering()
	{
		return true;
	}
	
	void render(ParticleEmitter emitter, BedrockParticle particle, IVertexOutput builder, float partialTicks);
	
	void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks);
}