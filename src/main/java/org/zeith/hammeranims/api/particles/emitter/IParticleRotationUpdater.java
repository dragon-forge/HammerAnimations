package org.zeith.hammeranims.api.particles.emitter;

import org.joml.Matrix3f;

public interface IParticleRotationUpdater
{
	void setMatrix(Matrix3f rotation);
	
	boolean emittingParticles();
}