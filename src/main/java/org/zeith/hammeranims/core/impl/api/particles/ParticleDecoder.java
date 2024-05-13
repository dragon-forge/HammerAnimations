package org.zeith.hammeranims.core.impl.api.particles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.particles.ParticleEffect;
import org.zeith.hammeranims.api.particles.event.DecodeParticleEffectEvent;
import org.zeith.hammeranims.core.jomljson.*;
import org.zeith.hammeranims.joml.*;

public class ParticleDecoder
{
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Vector2i.class, new Vector2iGsonAdapter())
			.registerTypeAdapter(Vector3f.class, new Vector3fGsonAdapter())
			.registerTypeAdapter(Vector3d.class, new Vector3dGsonAdapter())
			.registerTypeAdapter(ParticleEffect.Builder.class, new ParticleEffectAdapter())
			.create();
	
	static
	{
		HammerAnimationsApi.EVENT_BUS.addListener(ParticleDecoder::decodeParticle);
	}
	
	public static void init()
	{
	}
	
	private static void decodeParticle(DecodeParticleEffectEvent e)
	{
		ParticleEffect.Builder fx = GSON.fromJson(e.json, ParticleEffect.Builder.class);
		if(fx != null) e.setDecoded(fx.build(e.container));
	}
}