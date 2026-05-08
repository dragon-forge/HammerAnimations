package org.zeith.hammeranims.api.particles.components.itf;

import com.mojang.blaze3d.vertex.*;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.emitter.*;

public interface IParticleRender
		extends IParticleCompInstance
{
	default boolean supportsCollissionRendering()
	{
		return true;
	}
	
	void render(ParticleEmitter emitter, BedrockParticle particle, VertexConsumer builder, PoseStack pose, float partialTicks);
	
	void renderOnScreen(BedrockParticle particle, VertexConsumer builder, PoseStack pose, int x, int y, float scale, float partialTicks);
}