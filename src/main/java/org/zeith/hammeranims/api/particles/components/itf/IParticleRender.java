package org.zeith.hammeranims.api.particles.components.itf;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public interface IParticleRender
		extends IParticleComponent
{
	default boolean supportsCollissionRendering()
	{
		return true;
	}
	
	void render(ParticleVariables vars, ParticleEmitter emitter, BedrockParticle particle, IVertexBuilder builder, MatrixStack pose, float partialTicks);
	
	void renderOnScreen(ParticleVariables vars, BedrockParticle particle, IVertexBuilder builder, MatrixStack pose, int x, int y, float scale, float partialTicks);
}