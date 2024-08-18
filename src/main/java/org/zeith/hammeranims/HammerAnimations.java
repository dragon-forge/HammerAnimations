package org.zeith.hammeranims;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDecoder;
import org.zeith.hammeranims.core.impl.api.particles.ParticleDecoder;
import org.zeith.hammeranims.core.js.JsFactory;
import org.zeith.hammeranims.core.proxy.*;
import org.zeith.hammerlib.api.proxy.IProxy;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.util.CommonMessages;
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
		CommonMessages.printMessageOnIllegalRedistribution(HammerAnimations.class,
				LogManager.getLogger(MOD_NAME), "HammerAnimations", "https://www.curseforge.com/minecraft/mc-mods/hammer-animations"
		);
		
		modBus.register(this);
		AnimationDecoder.init();
		GeometryDecoder.init();
		ParticleDecoder.init();
		
		LOG.info("{} is constructing.", MOD_NAME);
		PROXY.construct(modBus);
		JsFactory.init(true);
		
		LanguageAdapter.registerMod(MOD_ID);
	}
	
	@SubscribeEvent
	public void checkFingerprint(FMLFingerprintCheckEvent e)
	{
		CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c",
				LogManager.getLogger(MOD_NAME), "HammerAnimations", "https://www.curseforge.com/minecraft/mc-mods/hammer-animations"
		);
	}
	
	public static ResourceLocation id(String path)
	{
		return Resources.location(MOD_ID, path);
	}
}