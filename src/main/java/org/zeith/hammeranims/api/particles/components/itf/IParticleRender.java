package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.core.client.render.IVertexOutput;

public interface IParticleRender
		extends IParticleComponent
{
	default boolean supportsCollissionRendering()
	{
		return true;
	}
	
	void render(ParticleVariables vars, ParticleEmitter emitter, BedrockParticle particle, IVertexOutput builder, float partialTicks);
	
	void renderOnScreen(ParticleVariables vars, BedrockParticle particle, int x, int y, float scale, float partialTicks);
}