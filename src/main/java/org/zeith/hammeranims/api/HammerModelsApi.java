package org.zeith.hammeranims.api;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.registries.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animsys.AnimationSourceType;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;

@Mod.EventBusSubscriber
public class HammerModelsApi
{
	public static final EventBus EVENT_BUS = new EventBus();
	
	private static IForgeRegistry<IAnimationContainer> ANIMATION_CONTAINERS;
	private static IForgeRegistry<IGeometryContainer> GEOMETRY_CONTAINERS;
	private static IForgeRegistry<AnimationSourceType> ANIMATION_SOURCES;
	private static boolean hasInitialized = false;
	
	@SubscribeEvent
	public static void newRegistries(RegistryEvent.NewRegistry e)
	{
		ANIMATION_CONTAINERS = new RegistryBuilder<IAnimationContainer>()
				.setType(IAnimationContainer.class)
				.setName(HammerAnimations.id("animations"))
				.create();
		
		GEOMETRY_CONTAINERS = new RegistryBuilder<IGeometryContainer>()
				.setType(IGeometryContainer.class)
				.setName(HammerAnimations.id("geometry"))
				.create();
		
		ANIMATION_SOURCES = new RegistryBuilder<AnimationSourceType>()
				.setType(AnimationSourceType.class)
				.setName(HammerAnimations.id("animsources"))
				.create();
		
		hasInitialized = true;
	}
	
	public static boolean hasInitialized()
	{
		return hasInitialized;
	}
	
	public static IForgeRegistry<IAnimationContainer> animations()
	{
		return ANIMATION_CONTAINERS;
	}
	
	public static IForgeRegistry<IGeometryContainer> geometries()
	{
		return GEOMETRY_CONTAINERS;
	}
	
	public static IForgeRegistry<AnimationSourceType> animationSources()
	{
		return ANIMATION_SOURCES;
	}
}