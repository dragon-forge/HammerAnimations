package org.zeith.hammeranims.api.particles.components;

import com.google.gson.JsonElement;

@FunctionalInterface
public interface IComponentDeserializer
{
	IParticleComponent fromJson(JsonElement element);
}