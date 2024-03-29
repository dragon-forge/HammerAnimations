package org.zeith.hammeranims;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.*;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDecoder;
import org.zeith.hammeranims.core.proxy.*;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.util.CommonMessages;

@Mod(HammerAnimations.MOD_ID)
public class HammerAnimations
{
	public static final String ROOT_PACKAGE = "org.zeith.hammeranims";
	public static final String MOD_ID = "hammeranims";
	public static final String MOD_NAME = "HammerAnimations";
	
	public static final Logger LOG = LogManager.getLogger(MOD_NAME);
	
	public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	
	public HammerAnimations()
	{
		CommonMessages.printMessageOnIllegalRedistribution(HammerAnimations.class,
				LOG, "HammerAnimations", "https://www.curseforge.com/minecraft/mc-mods/hammer-animations"
		);
		
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		AnimationDecoder.init();
		GeometryDecoder.init();
		
		LOG.info("{} is constructing.", MOD_NAME);
		PROXY.construct();
		
		LanguageAdapter.registerMod(MOD_ID);
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
}