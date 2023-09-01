package org.zeith.hammeranims;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.*;
import org.apache.logging.log4j.*;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animsys.actions.AnimationAction;
import org.zeith.hammeranims.api.annotations.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDecoder;
import org.zeith.hammeranims.core.proxy.*;
import org.zeith.hammeranims.core.utils.BasicRegistrar;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.util.CommonMessages;
import org.zeith.hammerlib.util.mcf.ScanDataHelper;

import java.lang.annotation.Annotation;
import java.util.function.*;

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
		
		scan(RegisterAnimations.class, (n, mod) -> BasicRegistrar.perform(IAnimationContainer.class, HammerAnimationsApi::animations, n, mod));
		scan(RegisterGeometries.class, (n, mod) -> BasicRegistrar.perform(IGeometryContainer.class, HammerAnimationsApi::geometries, n, mod));
		scan(RegisterTimeFunctions.class, (n, mod) -> BasicRegistrar.perform(TimeFunction.class, HammerAnimationsApi::timeFunctions, n, mod));
		scan(RegisterAnimations.class, (n, mod) -> BasicRegistrar.perform(AnimationAction.class, HammerAnimationsApi::animationActions, n, mod));
	}
	
	@SubscribeEvent
	public void checkFingerprint(FMLFingerprintCheckEvent e)
	{
		CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c",
				LOG, "HammerAnimations", "https://www.curseforge.com/minecraft/mc-mods/hammer-animations"
		);
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
	private void scan(Class<? extends Annotation> type, BiConsumer<Supplier<Class<?>>, FMLModContainer> handler)
	{
		for(ScanDataHelper.ModAwareAnnotationData data : ScanDataHelper.lookupAnnotatedObjects(type))
		{
			var mod = data.getOwnerMod().orElse(null);
			if(mod == null)
			{
				LOG.warn("Skipping @{}-annotated class {} since it does not belong to any mod.", type.getSimpleName(), data.clazz());
				continue;
			}
			handler.accept(data::getOwnerClass, mod);
			LOG.info("Applied @{} to {}, which belongs to {} ({})", type.getSimpleName(), data.clazz(), mod.getModId(), mod.getModInfo()
					.getDisplayName());
		}
	}
}