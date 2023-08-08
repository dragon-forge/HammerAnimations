package org.zeith.hammeranims.core.init;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerModelsApi;
import org.zeith.hammeranims.api.animsys.AnimationSourceType;
import org.zeith.hammeranims.core.contents.TileAnimationSourceType;

@Mod.EventBusSubscriber
public class AnimationSourceTypesHM
{
	public static final AnimationSourceType TILE_TYPE = new TileAnimationSourceType()
			.setRegistryName(HammerAnimations.id("tile_entity"));
	
	@SubscribeEvent
	public static void register(RegistryEvent.Register<AnimationSourceType> e)
	{
		IForgeRegistry<AnimationSourceType> reg = e.getRegistry();
		
		if(reg == HammerModelsApi.animationSources())
		{
			reg.register(TILE_TYPE);
		}
	}
}