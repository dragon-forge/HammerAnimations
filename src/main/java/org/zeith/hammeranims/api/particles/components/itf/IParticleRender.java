package org.zeith.hammeranims.api.particles.components.itf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
	
	void render(ParticleVariables vars, ParticleEmitter emitter, BedrockParticle particle, VertexConsumer builder, PoseStack pose, float partialTicks);
	
	void renderOnScreen(ParticleVariables vars, BedrockParticle particle, VertexConsumer builder, PoseStack pose, int x, int y, float scale, float partialTicks);
}