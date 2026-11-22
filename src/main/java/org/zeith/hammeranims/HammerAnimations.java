package org.zeith.hammeranims;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.Mod;
import org.slf4j.*;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDecoder;
import org.zeith.hammeranims.core.impl.api.particles.ParticleDecoder;
import org.zeith.hammeranims.core.proxy.*;
import org.zeith.hammerlib.api.proxy.IProxy;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.util.mcf.Resources;

@Mod(HammerAnimations.MOD_ID)
public class HammerAnimations
{
	public static final String ROOT_PACKAGE = "org.zeith.hammeranims";
	public static final String MOD_ID = "hammeranims";
	public static final String MOD_NAME = "HammerAnimations";
	
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
	
	public static final CommonProxy PROXY = IProxy.create(() -> ClientProxy::new, () -> ServerProxy::new);
	
	public HammerAnimations(IEventBus modBus)
	{
		GradleBuild.construct(getClass());
		
		modBus.register(this);
		AnimationDecoder.init();
		GeometryDecoder.init();
		ParticleDecoder.init();
		
		LOG.info("{} is constructing.", MOD_NAME);
		PROXY.construct(modBus);
		
		LanguageAdapter.registerMod(MOD_ID);
	}
	
	@SubscribeEvent
	public void checkFingerprint(FMLFingerprintCheckEvent e)
	{
		GradleBuild.fingerprint(e);
	}
	
	public static ResourceLocation id(String path)
	{
		return Resources.location(MOD_ID, path);
	}
}